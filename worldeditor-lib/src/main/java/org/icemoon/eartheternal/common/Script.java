package org.icemoon.eartheternal.common;

import java.io.Serializable;

public interface Script<K extends Serializable> extends Entity<K> {
	Language getLanguage();
	String getScript();
}
