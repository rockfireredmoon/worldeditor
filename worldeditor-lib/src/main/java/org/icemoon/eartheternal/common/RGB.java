package org.icemoon.eartheternal.common;

import java.io.Serializable;


public interface RGB extends Serializable, Comparable<RGB> {

	int  getAlpha();
	int getBlue();
	int getGreen();
	int getRed();
}
