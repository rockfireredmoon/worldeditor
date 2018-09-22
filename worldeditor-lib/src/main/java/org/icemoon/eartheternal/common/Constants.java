package org.icemoon.eartheternal.common;

public class Constants {

	public final static int TILE_WIDTH = 256;
	public final static int TILE_HEIGHT = 256;
	public static final int MAX_ZOOM = 4;

	public final static int DEFAULT_USE_TIME = 2000;
	public final static int DEFAULT_PAGESIZE = 1920;
	public final static int DEFAULT_MAXAGGRORANGE = 1920; // This value doesn't
															// matter if it's
															// greater than the
															// range of the
															// spawn system's
															// maximum aggro
															// range. Only if
															// it's lower.
	public final static int DEFAULT_MAXLEASHRANGE = 500;
	public final static int DEFAULT_MAXLEASHRANGEINSTANCE = 375; // Automatically
																	// limit
																	// leash
																	// range for
																	// instances.
}
