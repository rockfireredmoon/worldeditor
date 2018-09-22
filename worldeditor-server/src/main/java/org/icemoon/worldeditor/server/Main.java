package org.icemoon.worldeditor.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.SessionTrackingMode;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

public class Main {
	private static final String DEFAULT_KEYSTORE_PASSWORD = "changeit";
	public final static String APPLICATION_NAME = "WorldEditor";
	protected Log log;
	private Options options;
	private String level = null;
	private CommandLine cli;
	private File webapp;
	private Server server;
	private ServerConnector httpConnector;
	private ContextHandlerCollection contexts;
	private File tmpDir;
	private ServerConnector httpsConnector;
	private File keystoreFile;
	private String keystoreType = "JKS";
	private File conf;
	private File logs;
	private long started;
	private int httpPort;
	private int httpsPort;
	private File serverStateFile;
	private SslContextFactory sslctx;

	public Options getOptions() {
		return options;
	}

	public CommandLine getCommandLine() {
		return cli;
	}

	public void stopServer() throws Exception {
		server.stop();
	}

	protected void init() throws Exception {
		started = System.currentTimeMillis();
		// We have parsed the command line, so start initialising
		configureFileLocations();
		initLogging();
		for (String path : System.getProperty("java.class.path").split(File.pathSeparator)) {
			log.info(path);
		}
		webapp = locateWebapp();
		setFileLocationSystemProperties();
		createServer();
		initHttpConnector();
		initHttpsConnector();
		initContexts();
		initHandlers();
	}

	protected void start() throws Exception {
		server.start();
		log.info("Server started in " + (System.currentTimeMillis() - started) / 1000 + " seconds");
		server.join();
	}

	protected String getLogLevel() {
		return level;
	}

	Locale parseLocale(String languageCountry) {
		String[] els = languageCountry.split("_");
		if (els.length == 2) {
			return new Locale(els[0], els[1]);
		} else if (els.length == 3) {
			return new Locale(els[0], els[1], els[2]);
		}
		return new Locale(els[0]);
	}

	void initHandlers() {
		HandlerCollection handlers = new HandlerCollection();
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		handlers.setHandlers(new Handler[] { contexts, new DefaultHandler(), requestLogHandler });
		server.setHandler(handlers);
		NCSARequestLog requestLog = new NCSARequestLog(logs + File.separator + "jetty.log");
		requestLog.setExtended(false);
		requestLogHandler.setRequestLog(requestLog);
	}

	void initContexts() throws IOException {
		contexts = new ContextHandlerCollection();
		String contextPath = "/";
		if (cli.hasOption("path")) {
			contextPath = cli.getOptionValue("path");
		}
		final WebAppContext context = new WebAppContext(webapp.getAbsolutePath(), contextPath);
		WebAppClassLoader classLoader = new WebAppClassLoader(context);
		context.setClassLoader(classLoader);
		context.setConfigurations(new Configuration[] { new WebInfConfiguration(), new WebXmlConfiguration(),
				new MetaInfConfiguration(), new FragmentConfiguration(), new JettyWebXmlConfiguration() });
		context.setParentLoaderPriority(true);
		context.setConfigurationDiscovered(true);
		context.getSessionHandler().setMaxInactiveInterval(60 * 60);
		context.getSessionHandler().setSessionTrackingModes(new LinkedHashSet<SessionTrackingMode>(Arrays.asList(SessionTrackingMode.COOKIE)));
		contexts.addHandler(context);
		File staticDir = new File(conf, "static-content");
		staticDir.mkdirs();
		WebAppContext staticContext = new WebAppContext(staticDir.getAbsolutePath(), "/static");
		staticContext.addServlet(DefaultServlet.class, "/");
		contexts.addHandler(staticContext);
	}

	void initHttpConnector() throws Exception {
		// Its best not to restart the http connector once its started
		if (httpConnector == null) {
			httpConnector = new ServerConnector(server);
			configureConnector("httpPort", 80, httpConnector, httpPort);
			server.addConnector(httpConnector);
		} else {
			restartWarning();
		}
	}

	void initHttpsConnector() throws Exception {
		if (httpsConnector == null) {
			checkKeystore();
			 sslctx = new SslContextFactory();
			httpsConnector = new ServerConnector(server, sslctx);
			sslctx.setKeyStorePath(keystoreFile.getAbsolutePath());
			sslctx.setKeyStoreType(keystoreType);
			sslctx.setKeyManagerPassword(DEFAULT_KEYSTORE_PASSWORD);
			sslctx.setKeyStorePassword(DEFAULT_KEYSTORE_PASSWORD);
			sslctx.setTrustStorePassword(DEFAULT_KEYSTORE_PASSWORD);
			configureConnector("httpsPort", 443, httpsConnector, httpsPort);
			server.addConnector(httpsConnector);
		} else {
			restartWarning();
		}
	}

	void checkKeystore() throws Exception {
		File p12KeystoreFile = new File(conf, "keystore.p12");
		if (p12KeystoreFile.exists()) {
			keystoreFile = p12KeystoreFile;
			keystoreType = "PKCS12";
		} else {
			keystoreFile = new File(conf, "keystore");
		}
		if (!keystoreFile.exists()) {
			String keytoolPath = System.getProperty("java.home") + File.separator + "bin" + File.separator + "keytool";
			runCommand(new String[] { System.getProperty("java.home") + File.separator + "bin" + File.separator + "keytool",
					"-genkey", "-alias", "jetty", "-keyalg", "RSA", "-keypass", DEFAULT_KEYSTORE_PASSWORD, "-dname",
					"CN=localhost, OU=EE TestCertificate, O=EE, L=Unknown, ST=Unknown, C=Unknown", "-keystore",
					keystoreFile.getAbsolutePath(), "-storepass", DEFAULT_KEYSTORE_PASSWORD, "-storetype", keystoreType });
			// We want to use the same cert and key for webmin
			File pkcs12ForWebmin = keystoreFile;
			if (!keystoreType.equalsIgnoreCase("pkcs12")) {
				// If it is not already PKCS12, export it
				pkcs12ForWebmin = new File(keystoreFile.getParentFile(), keystoreFile.getName() + ".exp");
				runCommand(keytoolPath, "-importkeystore", "-srckeystore", keystoreFile.getAbsolutePath(), "-destkeystore",
						pkcs12ForWebmin.getAbsolutePath(), "-deststoretype", "PKCS12", "-deststorepass", "changeit",
						"-srcstorepass", "changeit", "-noprompt");
			}
			// Convert to PEM
			File pemOut = new File(pkcs12ForWebmin.getParentFile(), "webmin.pem");
			runCommand("openssl", "pkcs12", "-in", pkcs12ForWebmin.getAbsolutePath(), "-passin", "pass:changeit", "-out",
					pemOut.getAbsolutePath(), "-nodes");
		}
	}

	void runCommand(String... args) throws InterruptedException, IOException {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Process p = pb.start();
		InputStream in = p.getInputStream();
		try {
			IOUtils.copy(in, System.out);
			int ret = p.waitFor();
			if (ret != 0) {
				throw new IOException("Non-zero return code. " + ret);
			}
		} finally {
			in.close();
		}
	}

	void restartWarning() {
		System.err.println("WARNING: Server must be restart for HTTP server changes to take effect");
	}

	void configureConnector(String portKey, int defaultPort, ServerConnector connector, int overridePort) throws Exception {
		int actualPort = overridePort > 0 ? overridePort : defaultPort;
		connector.setPort(actualPort);
		System.setProperty("ee." + portKey, String.valueOf(actualPort));
		if (connector.getHost() != null) {
			System.setProperty("ee." + portKey + ".addr", connector.getHost());
		}
		String l = System.getProperty("ee.interfaces");
		if (l == null) {
			l = "";
		} else {
			l += ",";
		}
		System.setProperty("ee.interfaces", l + portKey);
		// connector.setHost("0.0.0.0");
	}

	void createServer() {
		putState("Starting HTTP server");
		server = new Server();
		server.setStopAtShutdown(true);
		server.addLifeCycleListener(new LifeCycle.Listener() {
			@Override
			public void lifeCycleStopping(LifeCycle event) {
				putState("Stopping HTTP server");
			}

			@Override
			public void lifeCycleStopped(LifeCycle event) {
				putState("Stopped HTTP server");
			}

			@Override
			public void lifeCycleStarting(LifeCycle event) {
			}

			@Override
			public void lifeCycleStarted(LifeCycle event) {
				putState("Started HTTP server");
			}

			@Override
			public void lifeCycleFailure(LifeCycle event, Throwable cause) {
				putState("Failed to start HTTP server. " + cause.getMessage());
			}
		});
	}

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		try {
			int exit = main.parseCli(args);
			if (exit > -1) {
				System.exit(exit);
			}
		} catch (ParseException pe) {
			System.err.println(main.getClass().getName() + ": " + pe.getMessage());
			System.exit(1);
		}
		main.init();
		main.start();
	}

	void configureFileLocations() throws IOException {
		tmpDir = checkDir("tmp");
		logs = checkDir("logs");
		conf = checkDir("conf");
		serverStateFile = new File(tmpDir, "server.state");
		serverStateFile.deleteOnExit();
	}

	void putState(String state) {
		try {
			PrintWriter fos = new PrintWriter(new FileOutputStream(serverStateFile));
			try {
				log.debug("Writing server state");
				fos.println(state);
			} finally {
				fos.close();
			}
		} catch (IOException ioe) {
			log.error("Failed to write server state.");
		}
	}

	void setFileLocationSystemProperties() {
		System.setProperty("ee.conf", conf.getAbsolutePath());
	}

	File checkDir(String name) throws IOException {
		File dir = new File(name);
		if (cli.hasOption(name)) {
			dir = new File(cli.getOptionValue(name));
		}
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Could not create directory " + dir);
		}
		return dir;
	}

	File[] checkDirs(String name, String... defaultPaths) throws IOException {
		List<File> f = new ArrayList<File>();
		if (cli.hasOption(name)) {
			defaultPaths = cli.getOptionValue(name).split(":");
		}
		for (String s : defaultPaths) {
			File dir = new File(s);
			if (!dir.exists() && !dir.mkdirs()) {
				throw new IOException("Could not create directory " + dir);
			}
			f.add(dir);
		}
		return f.toArray(new File[0]);
	}

	protected File locateWebapp() throws IOException {
		File webapp = null;
		if (cli.hasOption("webapp")) {
			webapp = new File(cli.getOptionValue("webapp"));
		} else {
			webapp = new File(".." + File.separator + "worldeditor-webapp" + File.separator + "src" + File.separator + "main"
					+ File.separator + "webapp");
		}
		if (!webapp.exists()) {
			throw new IOException("Web application could not be found at " + webapp);
		}
		log.info("Loading webapp from " + webapp);
		return webapp;
	}

	void initLogging() throws IOException {
		// Locate and load the log properties, possible using user specified
		// log4j configuration file
		URL resource = getClass().getResource("/server-log4j.xml");
		if (resource == null) {
			resource = getClass().getResource("/server-log4j.properties");
		}
		// Alter the default values in the logging configuration
		if (cli.hasOption("logconfig")) {
			File file = new File(cli.getOptionValue("logconfig"));
			resource = file.toURI().toURL();
		}
		if (cli.hasOption("quiet")) {
			level = "ERROR";
		} else if (cli.hasOption("verbose")) {
			level = "INFO";
		} else if (cli.hasOption("debug")) {
			level = "DEBUG";
		}
		if (resource == null) {
			BasicConfigurator.configure();
			log = LogFactory.getLog(getClass());
		} else {
			if (resource.getPath().endsWith(".properties")) {
				InputStream in = resource.openStream();
				Properties logProperties = new Properties();
				try {
					logProperties.load(in);
				} finally {
					in.close();
				}
				if (level != null) {
					logProperties.put("log4j.rootCategory", level + ", logfile, stdout");
				}
				// Log location
				logProperties.put("log4j.appender.logfile.File", logs.getAbsolutePath() + File.separator + "server.log");
				logProperties.put("log4j.logger." + getClass().getName(), level == null ? "WARN" : level);
				PropertyConfigurator.configure(logProperties);
				log = LogFactory.getLog(getClass());
			} else {
				DOMConfigurator.configure(resource);
				Logger.getRootLogger().setLevel(Level.toLevel(level == null ? "WARN" : level));
				log = LogFactory.getLog(getClass());
			}
		}
		onInitLogging();
	}

	protected void onInitLogging() {
		// For subclasses to override
	}

	protected void createOptions() {
		Option help = new Option("h", "help", false, "print this message");
		Option version = new Option("v", "version", false, "print the version information and exit");
		Option quiet = new Option("q", "quiet", false, "be extra quiet");
		Option verbose = new Option("v", "verbose", false, "be extra verbose");
		Option debug = new Option("x", "debug", false, "print extra debugging information");
		Option logConfig = new Option("g", "logconfig", true, "use given file for logging configuration");
		Option logs = new Option("l", "logs", true, "location of log files");
		Option temp = new Option("t", "tmp", true, "location of temporary files");
		Option db = new Option("d", "db", true, "location of database files");
		Option conf = new Option("c", "conf", true, "location of other configuration");
		Option plugins = new Option("P", "plugins", true, "location(s) of plugins");
		Option backups = new Option("B", "backups", true, "location(s) of backups");
		Option contextPath = new Option("p", "path", true, "path webapp runs under");
		Option httpPort = new Option("H", "http", true, "override configured http port");
		Option httpsPort = new Option("S", "https", true, "override configured https port");
		Option webapp = new Option("w", "webapp", true, "location of webapp");
		options = new Options();
		options.addOption(help);
		options.addOption(version);
		options.addOption(quiet);
		options.addOption(verbose);
		options.addOption(debug);
		options.addOption(temp);
		options.addOption(logConfig);
		options.addOption(logs);
		options.addOption(temp);
		options.addOption(db);
		options.addOption(conf);
		options.addOption(plugins);
		options.addOption(backups);
		options.addOption(contextPath);
		options.addOption(httpPort);
		options.addOption(httpsPort);
		options.addOption(webapp);
	}

	protected int parseCli(String[] args) throws org.apache.commons.cli.ParseException {
		createOptions();
		CommandLineParser parser = new DefaultParser();
		cli = parser.parse(options, args);
		if (cli.hasOption("help")) {
			printHelp();
			return 0;
		} else if (cli.hasOption("version")) {
			printVersion();
			return 0;
		}
		if (cli.hasOption("H")) {
			httpPort = Integer.parseInt(cli.getOptionValue("http"));
		}
		if (cli.hasOption("S")) {
			httpsPort = Integer.parseInt(cli.getOptionValue("https"));
		}
		return -1;
	}

	void printVersion() {
		URL resource = getClass().getResource("/META-INF/maven/org.eternallyunderground/wui-core/pom.properties");
		if (resource == null) {
			System.out.println(APPLICATION_NAME + " (Development version)");
		} else {
			Properties properties = new Properties();
			try {
				InputStream in = resource.openStream();
				try {
					properties.load(in);
					System.out.println(APPLICATION_NAME + " (" + properties.getProperty("version") + ")");
				} finally {
					in.close();
				}
			} catch (IOException ioe) {
				System.out.println(APPLICATION_NAME + " (Unknown version)");
			}
		}
	}

	void printHelp() {
		printVersion();
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getClass().getName(), options, true);
	}
}
