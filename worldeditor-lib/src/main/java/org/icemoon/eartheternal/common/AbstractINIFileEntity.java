package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public abstract class AbstractINIFileEntity<K extends Object, R extends IRoot> extends AbstractEntity<K, R> implements INIFile {

	public AbstractINIFileEntity(R database) {
		super(database);
	}

	public AbstractINIFileEntity(R database, String file, K id) {
		super(database, file, id);
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void load() {
		checkLoad();		
	}

}
