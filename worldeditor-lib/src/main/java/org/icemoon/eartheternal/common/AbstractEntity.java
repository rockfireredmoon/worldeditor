package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class AbstractEntity<K extends Object, R extends IRoot> implements Serializable, Cloneable, Entity<K> {
	protected boolean loaded;
	private K id;
	private String file;
	private boolean checkingLoad;
	
	private transient R database;

	public AbstractEntity() {
	}

	public AbstractEntity(R database) {
		this.database = database;
	}

	public AbstractEntity(R database, String file, K id) {
		this.database = database;
		setFile(file);
		setEntityId(id);
	}

	public final R getDatabase() {
		return database;
	}

	public final void setDatabase(R database) {
		this.database = database;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icemoon.eartheternal.common.Entity#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractEntity other = (AbstractEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icemoon.eartheternal.common.Entity#getEntityId()
	 */
	@Override
	public K getEntityId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icemoon.eartheternal.common.Entity#getFile()
	 */
	@Override
	public final String getFile() {
		return file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icemoon.eartheternal.common.Entity#isLoaded()
	 */
	@Override
	public final boolean isLoaded() {
		return loaded;
	}

	@Override
	public void load() {
		checkLoad();
	}

	@Override
	public final void loadIfNotLoaded() {
		if (needsLoad()) {
			try {
				doLoad();
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
			loaded = true;
		}
	}

	protected boolean needsLoad() {
		return !loaded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icemoon.eartheternal.common.Entity#setEntityId(K)
	 */
	@Override
	public void setEntityId(K id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icemoon.eartheternal.common.Entity#setFile(java.lang.String)
	 */
	@Override
	public final void setFile(String file) {
		this.file = file;
		onSetFile(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.icemoon.eartheternal.common.Entity#setLoaded(boolean)
	 */
	@Override
	public final void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	protected final void checkLoad() {
		if (checkingLoad) {
			return;
		}
		checkingLoad = true;
		try {
			loadIfNotLoaded();
		} finally {
			checkingLoad = false;
		}
	}

	protected abstract void doLoad() throws IOException;

	protected final String getArtifactName() {
		return getEntityId() + "@" + getFile();
	}

	protected void onSetFile(String file) {
	}
}
