package org.icemoon.eartheternal.common;

public enum Language {
	GSL, SQUIRREL;
	String getExtension() {
		switch (this) {
		case GSL:
			return "txt";
		default:
			return "nut";
		}
	}
}