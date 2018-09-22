package org.icemoon.eartheternal.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
/**
 * For use by entities whose container is made up of multiple ini files in a one
 * or more roots
 */
public abstract class AbstractSeparateINIFileEntities<T extends AbstractSeparateINIFileEntity<K, R>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends AbstractINIFileEntities<T, K, L, R> {
	public AbstractSeparateINIFileEntities(R database, Class<K> keyClass, String... files) {
		super(database, keyClass, files);
	}

	@Override
	public synchronized void delete(T instance) {
		T origInstance = get(instance.getEntityId());
		if (origInstance == null) {
			// Not there anymore
			return;
		}
		super.delete(instance);
		try {
			final FileObject file = VFS.getManager().resolveFile(instance.getFile());
			if (!file.delete()) {
				throw new IOException("Failed to delete " + file);
			}
			checkLoad();
		} catch (IOException ioe) {
			throw new RuntimeException();
		}
	}

	@Override
	public synchronized void save(T instance) {
		checkLoad();
		boolean exists = instance.getEntityId() != null && contains(instance.getEntityId());
		try {
			final FileObject file = VFS.getManager().resolveFile(instance.getFile());
			if (exists) {
				Log.debug(instance + " already exists, so replacing");
				replace(instance);
				INIWriter pw = new INIWriter(file.getContent().getOutputStream());
				try {
					instance.write(pw);
				} finally {
					pw.close();
				}
			} else {
				Log.debug(instance + " is new, so adding to end of file");
				createNew(instance);
				add(instance);
			}
			// We do not need to load again, file positions have been updated
			lastModified.put(instance.getFile(), file.getContent().getLastModifiedTime());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public void saveRaw(T instance, String raw) throws IOException {
		StringReader r = new StringReader(raw);
		BufferedReader bir = new BufferedReader(r);
		String line;
		String section = null;
		while ((line = bir.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("[") && line.endsWith("]")) {
				section = line.substring(1, line.length() - 1);
				instance.set("", "", section);
			}
		}
	}

	protected abstract K createKey(String filename);

	@Override
	protected void doLoad() throws IOException {
		for (String s : getFiles()) {
			FileObject f = VFS.getManager().resolveFile(s);
			if (f.exists()) {
				final String fname = f.getName().getURI();
				final boolean containsKey = lastModified.containsKey(fname);
				if (!containsKey || f.getContent().getLastModifiedTime() != lastModified.get(fname)) {
					if (containsKey)
						Log.info("Reloading " + f + " because it has changed to " + f.getContent().getLastModifiedTime() + " from "
								+ lastModified.get(fname));
					else
						Log.info("Reloading " + f + " because it is new to us");
					try {
						FileObject[] items = findActualFiles(f);
						for (FileObject itemFile : items) {
							T item = createItem();
							item.setFile(itemFile.getName().getURI());
							item.setEntityId(createKey(itemFile.getName().getBaseName()));
							add(item);
						}
						lastModified.put(fname, f.getContent().getLastModifiedTime());
						Log.info("Reloaded " + f);
					} catch (IOException ioe) {
						throw new RuntimeException(ioe);
					}
				}
			} else {
				Log.debug("Ignoring " + f + " because it does not exist");
			}
		}
	}

	protected FileObject[] findActualFiles(FileObject f) throws FileSystemException {
		return f.findFiles(new NonHiddenFilesFilter());
	}

	@Override
	protected void writeInstance(T instance, boolean append) throws IOException {
		FileObject f = VFS.getManager().resolveFile(instance.getFile());
		INIWriter pw = new INIWriter(f.getContent().getOutputStream(append));
		try {
			instance.write(pw);
		} finally {
			pw.close();
		}
	}
}
