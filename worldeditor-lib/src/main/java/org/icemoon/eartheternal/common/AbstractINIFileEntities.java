package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;

/**
 * Container for entities that are built from a single file, on demand.
 */
@SuppressWarnings("serial")
public abstract class AbstractINIFileEntities<T extends AbstractINIFileEntity<K, R>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends AbstractEntities<T, K, L, R> {
	private String[] files;
	protected Map<String, Long> lastModified = new HashMap<String, Long>();
	private boolean trimComments;
	static int clashCount;

	public AbstractINIFileEntities(R database, Class<K> keyClass, String... files) {
		super(database, keyClass);
		this.files = files;
		if (files.length > 0) {
			setFile(files[0]);
		}
	}

	@Override
	public synchronized void delete(T instance) {
		super.delete(instance);
	}

	public T getByName(String name, boolean caseInsenstive) {
		for (T v : values()) {
			if (caseInsenstive ? v.toString().equalsIgnoreCase(name) : v.toString().equals(name)) {
				return v;
			}
		}
		return null;
	}

	public final boolean isTrimComments() {
		return trimComments;
	}

	public final void setFiles(String[] files) {
		this.files = files;
	}

	public final void setTrimComments(boolean trimComments) {
		this.trimComments = trimComments;
	}

	protected void createNew(T instance) throws IOException {
		// Just append
		if (instance.getFile() == null) {
			instance.setFile(getFile());
		}
		writeInstance(instance, true);
	}

	protected void doAddItem(FileObject file, T item) {
		item.setFile(file.getName().getURI());
		if (!item.isLoaded()) {
			item.load();
		}
		try {
			add(item);
		} catch (IllegalArgumentException iae) {
			onDuplicate(file, item, iae);
		}
	}

	protected void onDuplicate(FileObject file, T item, IllegalArgumentException iae) {
		@SuppressWarnings("unchecked")
		DuplicateHandler<T> dh = (DuplicateHandler<T>) getDatabase().getDuplicateHandler(item.getClass());
		if (dh != null) {
			dh.duplicate(get(item.getEntityId()), item);
		} else {
			Log.error(getClass().getSimpleName(),
					"Failed to add item (no. " + (++clashCount) + " due to duplicate key of " + item.getEntityId() + " in " + file
							+ ". You will not be able to edit this object, and it probably suggests bad data. ",
					iae);
		}
	}

	@Override
	protected void doLoad() throws IOException {
	}

	protected String[] getFiles() {
		return files;
	}

	protected boolean isComment(String lastLine) {
		return lastLine.startsWith(";") || lastLine.startsWith("//");
	}

	protected String trimComment(String value) {
		// Look for trailing comments
		if (trimComments) {
			int cidx = 0;
			while ((cidx = value.lastIndexOf(';')) != -1) {
				value = value.substring(0, cidx).trim();
			}
		}
		return value;
	}

	protected abstract void writeInstance(T instance, boolean append) throws IOException;
}
