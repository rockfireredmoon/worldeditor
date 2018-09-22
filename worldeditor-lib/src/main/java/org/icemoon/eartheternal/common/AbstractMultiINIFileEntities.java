package org.icemoon.eartheternal.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
/**
 * For use by entities whose container is a single file with many INI style
 * entries contained within. Will contain {@link AbstractMultiINIFileEntity}
 * objects.
 */
public abstract class AbstractMultiINIFileEntities<T extends AbstractMultiINIFileEntity<K, R>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends AbstractINIFileEntities<T, K, L, R> {
	public enum IDType {
		LOWEST, HIGHEST, FIRST, LAST, MANUAL
	}

	public AbstractMultiINIFileEntities(Class<K> keyClass, String... files) {
		this(null, keyClass, files);
	}
	
	public AbstractMultiINIFileEntities(R database, Class<K> keyClass, String... files) {
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
			final File tempFile = File.createTempFile("ent", ".tmp");
			BufferedReader r = new BufferedReader(new InputStreamReader(file.getContent().getInputStream()));
			long startPosition = origInstance.getStartPosition();
			long endPosition = origInstance.getEndPosition();
			int lines = (int) (endPosition - startPosition);
			Log.debug("Deleting lines " + startPosition + " to " + endPosition);
			try {
				PrintWriter pw = new PrintWriter(tempFile);
				try {
					String line = null;
					int lineNo = 0;
					while ((line = r.readLine()) != null) {
						if (lineNo < startPosition || lineNo >= endPosition) {
							pw.print(line + "\r\n");
						}
						lineNo += 1;
					}
				} finally {
					pw.close();
				}
			} finally {
				r.close();
			}
			Util.copyFileToFileObject(tempFile, file);
			tempFile.delete();
			/*
			 * Now for all items that occur after the end position of the
			 * deleted item, adjust their line numbers by the number of lines
			 * removed
			 */
			for (T t : values) {
				if (t.getStartPosition() >= endPosition) {
					t.bounds(t.getStartPosition() - lines, t.getEndPosition() - lines);
				}
			}
			checkLoad();
		} catch (IOException ioe) {
			throw new RuntimeException();
		}
	}

	public long getEndPosition(T instance) {
		if (instance.getFile() != null) {
			List<T> by = getValuesByFile(instance.getFile());
			if (by != null && by.size() > 0) {
				return (by.get(by.size() - 1).getEndPosition());
			}
		}
		return 0;
	}

	@Override
	public synchronized void save(T instance) {
		checkLoad();
		boolean exists = instance.getEntityId() != null && contains(instance.getEntityId());
		try {
			if (!exists) {
				onSavingNew(instance);
				if (StringUtils.isBlank(instance.getFile()))
					throw new IllegalArgumentException("File must be set for this type of entity.");
			}
			final FileObject file = VFS.getManager().resolveFile(instance.getFile());
			if (exists) {
				Log.debug(instance + " already exists, so replacing");
				T origInstance = get(instance.getEntityId());
				replace(instance);
				long origSize = origInstance.getEndPosition() - origInstance.getStartPosition();
				int origIndex = indexOf(origInstance);
				Log.debug(instance + " was originallly at index " + origIndex);
				final File tempFile = File.createTempFile("ent", ".tmp");
				long adjustment = 0;
				BufferedReader r = new BufferedReader(new InputStreamReader(file.getContent().getInputStream()));
				try {
					INIWriter pw = new INIWriter(tempFile);
					try {
						String line = null;
						long lineNo = 0;
						while ((line = r.readLine()) != null) {
							if (lineNo < origInstance.getStartPosition() || lineNo >= origInstance.getEndPosition() - 1) {
								// Write other items
								pw.println(line);
							} else {
								/*
								 * Write amended item, counting how many lines
								 * it takes up. We then use the difference
								 * between the new size of the item and the
								 * original size to determine how many lines to
								 * adjust all the other items by
								 */
								long currentLine = pw.getCount();
								Log.debug("Starting to write at line " + currentLine);
								instance.write(pw);
								instance.bounds(currentLine, pw.getCount());
								long entrySize = pw.getCount() - currentLine;
								Log.debug("Finished write at line " + pw.getCount() + ", entry is now " + entrySize + " (it was "
										+ origSize + ")");
								adjustment = entrySize - origSize;
								Log.debug(instance + " causes an adjustment of " + adjustment);
								lineNo = origInstance.getEndPosition();
								// Read in the original lines
								for (int i = 0; i < origSize - 1; i++) {
									r.readLine();
								}
								Log.debug("Sunk " + (origSize - 1) + "lines");
							}
							lineNo += 1;
						}
					} finally {
						pw.close();
					}
				} finally {
					r.close();
				}
				Util.copyFileToFileObject(tempFile, file);
				updateLastModified(instance, file);
				tempFile.delete();
				// All of the items that came after this item will now need
				// their line positions shifting
				if (adjustment != 0) {
					final List<T> values = getValues();
					Log.debug("Shifting " + (origIndex + 1) + " to " + values.size());
					for (T rx : values.subList(origIndex + 1, values.size())) {
						rx.bounds(rx.getStartPosition() + adjustment, rx.getEndPosition() + adjustment);
					}
				}
			} else {
				Log.debug(instance + " is new, so adding to end of file");
				createNew(instance);
				updateLastModified(instance, file);
				add(instance);
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	protected void onSavingNew(T instance) {
	}

	protected void updateLastModified(T instance, final FileObject file) throws FileSystemException {
		// We do not need to load again, file positions have been updated
		lastModified.put(instance.getFile(), file.getContent().getLastModifiedTime());
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

	protected boolean needsLoad() {
		if (!loaded)
			return true;
		try {
			for (Map.Entry<String, Long> en : lastModified.entrySet()) {
				FileObject fo = VFS.getManager().resolveFile(en.getKey());
				if (!fo.exists() || fo.getContent().getLastModifiedTime() != en.getValue()) {
					return true;
				}
			}
		} catch (FileSystemException fse) {
			throw new IllegalStateException("Failed to check last modified times.", fse);
		}
		return false;
	}

	@Override
	protected void doLoad() throws IOException {
		Log.info("Loading multiple ini files " + getEntityId() + " (" + getFile() + ") from " + Arrays.asList(getFiles()));
		for (String s : getFiles()) {
			FileObject f = VFS.getManager().resolveFile(s);
			if (f.exists() && f.getType().equals(FileType.FILE)) {
				final String fname = f.getName().getURI();
				final boolean containsKey = lastModified.containsKey(fname);
				if (!containsKey || f.getContent().getLastModifiedTime() != lastModified.get(fname)) {
					if (containsKey) {
						Log.info("Reloading " + f + " because it has changed to " + f.getContent().getLastModifiedTime() + " from "
								+ lastModified.get(fname));
						// Remove all entries that came from that file
						for (T t : new ArrayList<T>(getValues())) {
							if (shouldRemove(t, f.getName().getURI())) {
								Log.debug("Removing: " + t + " at " + t.getStartPosition() + "-" + t.getEndPosition());
								remove(t);
							}
						}
					} else
						Log.debug("Loading " + f + " because its new to us (in " + getArtifactName() + " [" + hashCode() + "]");
					try {
						loadFile(f);
						lastModified.put(fname, f.getContent().getLastModifiedTime());
						Log.debug("Reloaded " + f);
					} catch (IOException ioe) {
						throw new RuntimeException(ioe);
					}
				}
			} else {
				Log.debug("Ignoring " + f + " because it does not exist or is not a file");
			}
		}
	}

	public final synchronized K getNextIdInFile(FileObject file, IDType type) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContent().getInputStream()));
		try {
			String line = null;
			K max = null;
			int idx;
			String k;
			String v;
			/*
			 * Each items start/end lines are inclusive, exclusive respectively
			 */
			while ((line = reader.readLine()) != null) {
				line = line.trim().replace("\r", "");
				if (!line.equals("") && !line.startsWith("[") && !line.startsWith(";")) {
					idx = line.indexOf('=');
					k = line;
					v = null;
					if (idx != -1) {
						k = line.substring(0, idx);
						v = line.substring(idx + 1);
					}
					max = extractMaxId(k, v, max, type);
				}
			}
			return max;
		} finally {
			reader.close();
		}
	}

	protected K extractMaxId(String k, String v, K max, IDType type) {
		return max;
	}

	protected final synchronized void loadFile(FileObject file) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContent().getInputStream()));
		try {
			String line = null;
			T item = null;
			int lineNo = 0;
			String key = null;
			String value = null;
			String section = null;
			long startLine = -1;
			long lastEnd = -1;
			/*
			 * Each items start/end lines are inclusive, exclusive respectively
			 */
			while ((line = reader.readLine()) != null) {
				line = line.trim().replace("\r", "");
				if (line.startsWith("[") && line.endsWith("]")) {
					/* Processing a new section. */
					/*
					 * Finish off any unprocessed keys now we have reached a
					 * definite end
					 */
					if (item != null) {
						if (key != null) {
							item.set(key, trimComment(Util.trimTail(value)), section);
							key = null;
							value = null;
						}
					}
					/* Get the new section name */
					section = line.substring(1, line.length() - 1);
					if (item != null) {
						/* Tell the item about the new section */
						item.set("", "", section);
					}
					if (line.equals("[ENTRY]")) {
						/* Start of a new item */
						if (item != null) {
							/* Commit the new item */
							item.bounds(startLine, lastEnd == -1 ? lineNo - 1 : lastEnd - 1);
							if (item.getEntityId() == null) {
								throw new IOException("Adding entity without ID at line " + lineNo + " in " + getFile());
							}
							doAddItem(file, item);
						}
						startLine = lineNo;
						// Start of new record
						item = createItem();
					}
					/* Stop tracking start of last comment block */
					lastEnd = -1;
				} else {
					if (item == null) {
						/* Still waiting for an item to appear */
					} else {
						if (isComment(line)) {
							if (key != null) {
								/*
								 * Finish off any unprocessed keys now we have
								 * reached a definite end
								 */
								item.set(key, trimComment(Util.trimTail(value)), section);
								key = null;
								value = null;
							}
							if (lastEnd == -1)
								/*
								 * To prevent comments between entries being
								 * part of any entry, records where it starts
								 */
								lastEnd = lineNo;
						} else {
							if (!line.equals("") && (key == null || line.matches("^[a-zA-Z_0-9.]+=.*"))) {
								/*
								 * We are not yet accumulating a value and the
								 * line is valid. Reset some state
								 */
								lastEnd = -1;
								if (key != null) {
									/*
									 * Finish off any unprocessed keys now we
									 * have reached a definite end
									 */
									item.set(key, trimComment(Util.trimTail(value)), section);
									key = null;
									value = null;
								}
								int idx = line.indexOf("=");
								if (idx == -1) {
									throw new IOException("Invalid for for line " + lineNo + " in " + file + ". '" + line + "'");
								}
								key = line.substring(0, idx);
								value = line.substring(idx + 1);
							} else {
								if (value != null) {
									/*
									 * If we are currently accumulating a line,
									 * append it
									 */
									value += "\n" + line;
								}
							}
						}
					}
				}
				lineNo++;
			}
			/* Commit any remaining items */
			if (item != null) {
				if (key != null) {
					/*
					 * Finish off any unprocessed keys now we have reached a
					 * definite end
					 */
					item.set(key, trimComment(Util.trimTail(value)), section);
				}
				item.bounds(startLine, lastEnd == -1 ? lineNo : lastEnd - 1);
				if (item.getEntityId() == null) {
					throw new IOException(String.format("Adding entity to %s without ID at line %d", file, lineNo));
				}
				doAddItem(file, item);
			}
		} finally {
			reader.close();
		}
	}

	@Override
	protected void writeInstance(T instance, boolean append) throws IOException {
		// Get the end position of the last item
		long line = getEndPosition(instance);
		long start = line;
		Log.debug("Inserting at line " + start + " to " + instance.getFile());
		FileObject f = VFS.getManager().resolveFile(instance.getFile());
		boolean empty = !f.exists() || f.getContent().getSize() == 0;
		int hasNewLines = f.exists() ? INIReader.countTrailingNewLines(f) : 0;
		INIWriter pw = new INIWriter(f.getContent().getOutputStream(append));
		try {
			if (hasNewLines == 0 && !empty) {
				pw.println();
				pw.println();
				start++;
				line--;
			} else if (hasNewLines == 1) {
				pw.println();
				start++;
			}
			instance.write(pw);
		} finally {
			pw.close();
		}
		line += pw.getCount();
		instance.bounds(start, line);
	}
}
