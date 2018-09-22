package org.icemoon.eartheternal.common;

import java.io.Serializable;

public interface Entity<K extends Object> extends Serializable {
	
	Object clone() throws CloneNotSupportedException;
	
	K getEntityId();

	String getFile();

	boolean isLoaded();

	void load();

	void loadIfNotLoaded();

	void setEntityId(K id);

	void setFile(String file);

	void setLoaded(boolean loaded);
}