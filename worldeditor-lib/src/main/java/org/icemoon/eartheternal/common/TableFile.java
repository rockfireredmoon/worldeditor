package org.icemoon.eartheternal.common;

public interface TableFile {
	void set(String[] row, String comment);
	void write(INIWriter writer);
}
