package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.LogFactory;

public class Log {
	public final static org.apache.commons.logging.Log LOG = LogFactory.getLog("EEDB");
	private static List<String> todos = new ArrayList<String>();
	public static boolean debug = "true".equalsIgnoreCase(System.getProperty("ee.debug", "false"));

	public static void debug(String text) {
		if (debug) {
			LOG.info(text);
		}else {
			LOG.debug(text);
		}
	}

	public static void error(String artifact, String error, Throwable... exceptions) {
		if (debug) {
			LOG.error("[" + artifact + "]: " + error);
			for (Throwable t : exceptions) {
				LOG.error("Trace :-", t);
			}
		} else {
			LOG.error("[" + artifact + "]: " + error + (exceptions.length > 0 ? ". " + exceptions[0].getMessage() : ""));
		}
	}

	public static void info(String text) {
		LOG.info(text);
	}

	public static void todo(String artifact, String task) {
		String key = artifact + "-" + task;
		if (!todos.contains(key)) {
			todos.add(key);
			LOG.warn("TODO [" + artifact + "]: " + task);
		}
	}
}
