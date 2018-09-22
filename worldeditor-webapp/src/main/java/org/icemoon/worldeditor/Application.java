package org.icemoon.worldeditor;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NoInitialContextException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.SharedResources;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.session.ISessionStore.BindListener;
import org.apache.wicket.util.convert.converter.DoubleConverter;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.DefaultSiteData;
import org.icemoon.eartheternal.common.EEPrincipal;
import org.icemoon.eartheternal.common.GameIcon;
import org.icemoon.eartheternal.common.GameIcons;
import org.icemoon.eartheternal.common.IAuthenticator;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.Log;
import org.icemoon.eartheternal.common.MapImage;
import org.icemoon.eartheternal.common.MapImages;
import org.icemoon.eartheternal.common.StaticDataDatabase;
import org.icemoon.worldeditor.entities.ActiveUser;
import org.icemoon.worldeditor.entities.ActiveUsers;
import org.icemoon.worldeditor.entities.DatabaseStore;
import org.icemoon.worldeditor.entities.DatabaseStores;
import org.icemoon.worldeditor.player.AccountsPage;
import org.icemoon.worldeditor.player.CharacterPage;
import org.icemoon.worldeditor.player.CharactersPage;
import org.icemoon.worldeditor.player.HomePage;
import org.icemoon.worldeditor.resource.FileObjectResourceStream;
import org.icemoon.worldeditor.search.SearchPage;
import org.icemoon.worldeditor.tools.ToolsPage;
import org.odlabs.wiquery.ui.themes.WiQueryCoreThemeResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends WebApplication implements IAuthenticator {
	final static Logger LOG = LoggerFactory.getLogger(Application.class);
	public static final String KEY = "dbfe";
	private StaticDataDatabase staticDatabase;
	private IUserData userData;
	private DefaultSiteData siteData;
	private IAuthenticator authenticator;
	private DatabaseStores databaseStores;
	private ActiveUsers activeUsers;
	private Map<String, IDatabase> databases = new HashMap<String, IDatabase>();

	/**
	 * Constructor
	 */
	public Application() {
	}

	public final IUserData getUserData() {
		return userData;
	}

	public final DefaultSiteData getSiteData() {
		return siteData;
	}

	@Override
	protected void init() {
		super.init();
		setPageManagerProvider(new NoSerializationPageManagerProvider(this));
		IPackageResourceGuard packageResourceGuard = getResourceSettings().getPackageResourceGuard();
		if (packageResourceGuard instanceof SecurePackageResourceGuard) {
			SecurePackageResourceGuard guard = (SecurePackageResourceGuard) packageResourceGuard;
			guard.addPattern("+*.ttf");
		}
		/*
		 * Site data holds user data stuff specific to this application (not
		 * used in game)
		 */
		final FileObject siteDir = getDir("ee.site", "sitepath");
		siteData = new DefaultSiteData(siteDir, Preferences.userNodeForPackage(Application.class));
		/*
		 * The workspace contains cloned copies of remote data. Must be a local
		 * directory
		 */
		FileObject workspaceDir = getDir("ee.workspace", "workspace");
		if (!workspaceDir.getName().getScheme().equals("file")) {
			throw new IllegalStateException("Workspace directory " + workspaceDir + " must be local.");
		}
		/* Stores a list of all the available game database stores */
		try {
			databaseStores = new DatabaseStores(null, workspaceDir.getName().getPath(),
					siteDir.resolveFile("DataStores.txt").getName().getURI());
		} catch (FileSystemException e1) {
			throw new RuntimeException(e1);
		}
		/* Holds currently active users */
		activeUsers = new ActiveUsers(null);
		/*
		 * Static data holds stuff global across all workspaces but not used in
		 * the game data (Map images, Icons etc)
		 */
		staticDatabase = new StaticDataDatabase(getDir("ee.static", "staticpath"));
		/*
		 * Authenticator. If we have user data, use that, otherwise assuume
		 * connecting to TAW server
		 */
		if (userData == null) {
			String serverUrl = System.getProperty("ee.authUrl", "http://www.theanubianwar.com/rest");
			if (serverUrl == null)
				throw new IllegalStateException("Must set ee.authUrl system property.");
			authenticator = new TAWAuthenticator(serverUrl);
		} else
			authenticator = this;
		mountPages();
		try {
			loadSharedResources();
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
		addResourceReplacement(WiQueryCoreThemeResourceReference.get(), new WiQueryCoreThemeResourceReference("eedbfe"));
		getSessionStore().registerBindListener(new BindListener() {
			@Override
			public void bindingSession(Request request, Session newSession) {
				ActiveUser au = new ActiveUser(null);
				au.setEntityId(newSession.getId());
				getActiveUsers().save(au);
			}
		});
	}

	protected FileObject getDir(String sysprop, String jndi) {
		FileObject dir = null;
		try {
			if (System.getProperty(sysprop) != null) {
				String eestatic = System.getProperty(sysprop);
				dir = VFS.getManager().resolveFile(eestatic);
			} else {
				Context initCtx = new InitialContext();
				try {
					Context envCtx = (Context) initCtx.lookup("java:comp/env");
					dir = VFS.getManager().resolveFile((String) envCtx.lookup(jndi));
				} catch (NoInitialContextException nce) {
				}
			}
		} catch (Exception fse) {
			throw new RuntimeException(fse);
		}
		if (dir == null)
			throw new IllegalArgumentException("No " + sysprop + " or " + jndi + ". Please set.");
		return dir;
	}

	public StaticDataDatabase getStaticDatabase() {
		return staticDatabase;
	}

	protected void mountPages() {
		if (userData != null) {
			mountPage("/accounts.html", AccountsPage.class);
			mountPage("/characters.html", CharactersPage.class);
			mountPage("/character.html", CharacterPage.class);
			mountPage("/home.html", HomePage.class);
			mountPage("/questtree.html", QuestTreePage.class);
		}
		mountPage("/books.html", BooksPage.class);
		mountPage("/dialog.html", DialogsPage.class);
		mountPage("/stores.html", DatabaseStoresPage.class);
		mountPage("/instances.html", ZoneDefsPage.class);
		mountPage("/interactions.html", InteractionsPage.class);
		mountPage("/creatures.html", CreaturesPage.class);
		mountPage("/creature.html", CreaturePage.class);
		mountPage("/aiscripts.html", AIScriptsPage.class);
		mountPage("/quests.html", QuestsPage.class);
		mountPage("/items.html", ItemsPage.class);
		mountPage("/item.html", ItemPage.class);
		mountPage("/login.html", LoginPage.class);
		mountPage("/maps.html", MapDefsPage.class);
		mountPage("/map.html", DynamicMapPage.class);
		mountPage("/spawnpackages.html", SpawnPackagesPage.class);
		mountPage("/scenery.html", SceneryPage.class);
		mountPage("/search.html", SearchPage.class);
		mountPage("/quest.html", QuestPage.class);
		mountPage("/tools.html", ToolsPage.class);
		mountPage("/itemtip.html", ItemTipPage.class);
		mountPage("/lootpackages.html", LootPackagesPage.class);
		mountPage("/lootsets.html", LootSetsPage.class);
		mountPage("/lootcreatures.html", LootCreaturesPage.class);
		mountPage("/abilities.html", AbilitiesPage.class);
		mountPage("/active.html", ActiveUsersPage.class);
		mountPage("/shops.html", ShopsPage.class);
		mountPage("/essenceshops.html", EssenceShopsPage.class);
		mountPage("/questscripts.html", QuestScriptsPage.class);
		mountPage("/instancescripts.html", InstanceScriptsPage.class);
		mountPage("/markers.html", ZoneMarkersPage.class);
		mountPage("/rescosts.html", ResCostsPage.class);
		mountPage("/crafting.html", CraftDefsPage.class);
		mountPage("/dropprofiles.html", DropProfilesPage.class);
		mountPage("/terrain.html", TerrainTemplatesPage.class);
	}

	protected void loadSharedResources() throws FileSystemException {
		SharedResources resources = getSharedResources();
		final MapImages mapImages = staticDatabase.getMapImages();
		for (MapImage map : mapImages.values()) {
			resources.add(MapImages.class, map.getEntityId(), null, null, null,
					new ResourceStreamResource(new FileObjectResourceStream(VFS.getManager().resolveFile(map.getFile()))));
		}
		final GameIcons gameIcons = staticDatabase.getGameIcons();
		for (GameIcon icon : gameIcons.values()) {
			resources.add(GameIcons.class, icon.getEntityId(), null, null, null,
					new ResourceStreamResource(new FileObjectResourceStream(VFS.getManager().resolveFile(icon.getFile()))));
		}
		if (gameIcons.size() == 0 && mapImages.size() == 0) {
			Log.error("Application", "Cannot find any Item icons or Map images. Have you installed them to the correct location?");
		}
	}

	@Override
	protected void onDestroy() {
	}

	@Override
	protected IConverterLocator newConverterLocator() {
		ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
		final DoubleConverter converter = new DoubleConverter();
		final NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setGroupingUsed(false);
		converter.setNumberFormat(Locale.getDefault(), nf);
		locator.set(Double.class, converter);
		return locator;
	}

	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEVELOPMENT;
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return userData == null ? ZoneDefsPage.class : HomePage.class;
	}

	public final IAuthenticator getAuthenticator() {
		return authenticator;
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new AppSession(request, userData);
	}

	public IDatabase getDatabase(String id) {
		IDatabase database = databases.get(id);
		if (database == null) {
			if (id == null)
				throw new IllegalArgumentException("Please choose the datastore you wish to work with.");
			DatabaseStore store = getDatabaseStores().get(id);
			if (store == null)
				throw new IllegalArgumentException("No such database store.");
			database = store.createDatabase();
			if (database == null)
				throw new IllegalStateException("Database Store is not yet checked-out");
			databases.put(id, database);
		}
		return database;
	}

	public static Application getApp() {
		return (Application) get();
	}

	public static AppSession getAppSession(RequestCycle requestCycle) {
		return (AppSession) (get().fetchCreateAndSetSession(requestCycle));
	}

	@Override
	public EEPrincipal login(String username, char[] password) {
		Account account = userData.getAccounts().getAccount(username);
		if (account != null && account.checkPassword(new String(password))) {
			return account;
		}
		return null;
	}

	public DatabaseStores getDatabaseStores() {
		return databaseStores;
	}

	public ActiveUsers getActiveUsers() {
		return activeUsers;
	}
}
