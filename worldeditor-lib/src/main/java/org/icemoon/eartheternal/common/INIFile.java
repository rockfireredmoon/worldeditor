package org.icemoon.eartheternal.common;


public interface INIFile {
	void set(String name, String value, String section);
	void write(INIWriter writer);
}
