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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

/**
 * Container for entities that are built from a single file, on demand.
 */
@SuppressWarnings("serial")
public abstract class AbstractTableFileEntities<T extends AbstractTableFileEntity<K, R>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends AbstractEntities<T, K, L, R> {
	private String[] files;
	protected Map<String, Long> lastModified = new HashMap<String, Long>();
	private boolean hasHeaderRow = true;
	private String colDelimiter = "\t";
	private String[] headers;
	private boolean quotedFields;
	private boolean useMinMaxColumns;
	private int minColumns = 1;
	private int maxColumns = Integer.MAX_VALUE;
	private boolean eolComments = true;

	public AbstractTableFileEntities(R database, Class<K> keyClass, String... files) {
		super(database, keyClass);
		this.files = files;
		if (files.length > 0) {
			setFile(files[0]);
		}
	}

	public final boolean isEolComments() {
		return eolComments;
	}

	public final void setEolComments(boolean eolComments) {
		this.eolComments = eolComments;
	}

	public final int getMinColumns() {
		return minColumns;
	}

	public final void setMinColumns(int minColumns) {
		this.minColumns = minColumns;
		useMinMaxColumns = true;
	}

	public final int getMaxColumns() {
		return maxColumns;
	}

	public final void setMaxColumns(int maxColumns) {
		this.maxColumns = maxColumns;
		useMinMaxColumns = true;
	}

	public final boolean isQuotedFields() {
		return quotedFields;
	}

	public final void setQuotedFields(boolean quotedFields) {
		this.quotedFields = quotedFields;
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
			Log.debug("Deleting lines " + origInstance.getLine());
			try {
				PrintWriter pw = new PrintWriter(tempFile);
				try {
					String line = null;
					int lineNo = 0;
					while ((line = r.readLine()) != null) {
						if (lineNo != origInstance.getLine()) {
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
			checkLoad();
		} catch (IOException ioe) {
			throw new RuntimeException();
		}
	}

	public T getByName(String name, boolean caseInsenstive) {
		for (T v : values()) {
			if (caseInsenstive ? v.toString().equalsIgnoreCase(name) : v.toString().equals(name)) {
				return v;
			}
		}
		return null;
	}

	public final String getColDelimiter() {
		return colDelimiter;
	}

	public long getLine(T instance) {
		if (instance.getFile() != null) {
			List<T> by = getValuesByFile(instance.getFile());
			if (by != null && by.size() > 0) {
				return (by.get(by.size() - 1).getLine()) + 1;
			}
		}
		return 0;
	}

	public final boolean isHasHeaderRow() {
		return hasHeaderRow;
	}

	@Override
	public synchronized void save(T instance) {
		checkLoad();
		boolean exists = instance.getEntityId() != null && contains(instance.getEntityId());
		try {
			if (!exists) {
				instance.setFile(getFile());
			}
			final FileObject file = VFS.getManager().resolveFile(instance.getFile());
			if (exists) {
				Log.debug(instance + " already exists, so replacing");
				T origInstance = get(instance.getEntityId());
				replace(instance);
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
							if (lineNo != origInstance.getLine()) {
								// Write other items
								pw.println(line);
							} else {
								long currentLine = pw.getCount();
								Log.debug("Starting to write at line " + currentLine);
								instance.write(pw);
								instance.line(currentLine);
								long entrySize = pw.getCount() - currentLine;
								Log.debug("Finished write at line " + pw.getCount() + ", entry is now " + entrySize);
								Log.debug(instance + " causes an adjustment of " + adjustment);
								lineNo = origInstance.getLine();
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
				// All of the items that came after this item will now need
				// their line positions shifting
				if (adjustment != 0) {
					final List<T> values = getValues();
					Log.debug("Shifting " + (origIndex + 1) + " to " + values.size());
					for (T rx : values.subList(origIndex + 1, values.size())) {
						rx.line(rx.getLine() + 1);
					}
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
		while ((line = bir.readLine()) != null) {
			line = line.trim();
			instance.set(line.split(colDelimiter), null);
		}
	}

	public final void setColDelimiter(String colDelimiter) {
		this.colDelimiter = colDelimiter;
	}

	public final void setFiles(String[] files) {
		this.files = files;
	}

	public final void setHasHeaderRow(boolean hasHeaderRow) {
		this.hasHeaderRow = hasHeaderRow;
	}

	protected void createNew(T instance) throws IOException {
		// Just append
		if (instance.getFile() == null) {
			instance.setFile(getFile());
		}
		writeInstance(instance, true);
	}

	protected void doAddItem(FileObject file, T item, int line) {
		try {
			item.setFile(file.getName().getURI());
			if (!item.isLoaded()) {
				item.load();
			}
			add(item);
		} catch (IllegalArgumentException iae) {
			Log.error(getClass().getSimpleName(), "Failed to add item due to duplicate key of " + item.getEntityId() + " in " + file
					+ ". You will not be able to edit this object, and it probably suggests bad data.");
		}
	}

	@Override
	protected boolean needsLoad() {
		if (super.needsLoad())
			return true;
		for (Map.Entry<String, Long> en : lastModified.entrySet()) {
			try {
				FileObject f = VFS.getManager().resolveFile(en.getKey());
				if (f.getContent().getLastModifiedTime() != en.getValue())
					return true;
			} catch (FileSystemException fse) {
			}
		}
		return false;
	}

	@Override
	protected void doLoad() throws IOException {
		for (String s : getFiles()) {
			FileObject f = VFS.getManager().resolveFile(s);
			if (f.exists() && f.getType().equals(FileType.FILE)) {
				final String fname = f.getName().getURI();
				final boolean containsKey = lastModified.containsKey(fname);
				if (!containsKey || f.getContent().getLastModifiedTime() != lastModified.get(fname)) {
					if (containsKey)
						Log.info("Reloading " + f + " because it has changed to " + f.getContent().getLastModifiedTime() + " from "
								+ lastModified.get(fname));
					else
						Log.info("Reloading " + f + " because it is new to us");
					// Remove all entries that came from that file
					for (T t : new ArrayList<T>(getValues())) {
						if (shouldRemove(t, f.getName().getURI())) {
							Log.debug("Removing: " + t + " at " + t.getLine());
							remove(t);
						}
					}
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

	protected String[] getFiles() {
		return files;
	}

	protected boolean isComment(String lastLine) {
		return lastLine.startsWith("#");
	}

	protected final synchronized void loadFile(FileObject file) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContent().getInputStream()));
		try {
			String line = null;
			int lineNo = 0;
			long startLine = -1;
			if (hasHeaderRow)
				headers = null;
			while ((line = reader.readLine()) != null) {
				if (startLine == -1) {
					startLine = lineNo;
				}
				line = line.trim().replace("\r", "");
				if (isComment(line))
					continue;
				if (headers == null && hasHeaderRow) {
					headers = line.split(colDelimiter);
				} else {
					String comment = null;
					if (eolComments) {
						CommentTrimmer ct = trimComment(line);
						line = ct.getContent();
						comment = ct.getComment();
					}
					String[] row = line.split(colDelimiter);
					if (headers == null && !hasHeaderRow) {
						List<String> h = new ArrayList<String>(row.length);
						for (int i = 0; i < row.length; i++)
							h.add("Column" + i);
						headers = h.toArray(new String[0]);
					}
					if (useMinMaxColumns) {
						if (row.length < minColumns || row.length > maxColumns)
							Log.error(getArtifactName(),
									String.format("Line %s has more or less than the min (%d) or max (d) number of colums (%d)",
											lineNo, minColumns, maxColumns, row.length));
					} else if (row.length != headers.length) {
						Log.error(getArtifactName(),
								String.format(
										"Line %s has a different number of columns (%d) to the first line (%d) with a non-comment row",
										lineNo, row.length, headers.length));
					}
					if (quotedFields) {
						for (int i = 0; i < row.length; i++) {
							if (row[i].startsWith("\""))
								row[i] = row[i].substring(1);
							if (row[i].endsWith("\""))
								row[i] = row[i].substring(0, row[i].length() - 1);
						}
					}
					T t = createItem();
					t.line(lineNo);
					try {
						t.set(row, comment);
						doAddItem(file, t, lineNo);
					} catch (IndexOutOfBoundsException ioobe) {
						Log.error(getArtifactName(), String.format("Line %d (of %d for %d)failed to parse. %s", lineNo, row.length,
								headers.length, ioobe.getMessage()));
					}
				}
				lineNo++;
			}
		} finally {
			reader.close();
		}
	}

	protected CommentTrimmer trimComment(String value) {
		return new CommentTrimmer(value);
	}

	protected void writeInstance(T instance, boolean append) throws IOException {
		long line = getLine(instance);
		long start = line;
		Log.debug("Inserting at line " + start + " to " + instance.getFile());
		FileObject f = VFS.getManager().resolveFile(instance.getFile());
		boolean empty = !f.exists() || f.getContent().getSize() == 0;
		int hasNewLines = f.exists() ? INIReader.countTrailingNewLines(f) : 0;
		INIWriter pw = new INIWriter(f.getContent().getOutputStream(append));
		try {
			if (hasNewLines == 0 && !empty) {
				pw.println();
				start++;
				line--;
			}
			instance.write(pw);
		} finally {
			pw.close();
		}
		line += pw.getCount();
		instance.line(start);
	}
}
