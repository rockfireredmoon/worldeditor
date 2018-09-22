package org.icemoon.eartheternal.common;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.RandomAccessContent;
import org.apache.commons.vfs2.util.RandomAccessMode;

public abstract class INIReader<T extends AbstractINIFileEntity<K, R>, K extends Serializable, R extends IRoot> {

	private boolean trimComments;
	private String start = "ENTRY";

	public final boolean isTrimComments() {
		return trimComments;
	}

	public final String getStart() {
		return start;
	}

	public final void setStart(String start) {
		this.start = start;
	}

	public final synchronized void loadFile(FileObject file) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContent().getInputStream()));
		try {
			String line = null;
			String lastLine = null;
			T item = null;
			int lineNo = 0;
			String key = null;
			String value = null;
			String section = null;
			long startLine = -1;
			long endLine = -1;

			while ((line = reader.readLine()) != null) {
				
				if (startLine == -1) {
					startLine = lineNo;
				}

				line = line.trim().replace("\r", "");

				if (line.startsWith("[") && line.endsWith("]")) {

					if (item != null) {
						if (key != null) {
							item.set(key, trimComment(Util.trimTail(value)), section);
							key = null;
							value = null;
						}
					}

					section = line.substring(1, line.length() - 1);

					if (item != null) {
						item.set("", "", section);
					}

					if (isEntryStart(line)) {
						if (lastLine != null && !"".equals(lastLine) && !isComment(lastLine)) {
							Log.error(getClass().getSimpleName(), "No blank line between entries at line " + lineNo);
						}

						if (item != null) {
							if (endLine == -1) {
								endLine = lineNo;
							}
							if (item instanceof AbstractMultiINIFileEntity) {
								((AbstractMultiINIFileEntity) item).bounds(startLine, endLine + 1);
							}
							if (item.getEntityId() == null) {
								throw new IOException("Adding entity without ID at line " + lineNo + " in " + file);
							}
							doAddItem(file, item);
						}
						startLine = lineNo;
						endLine = -1;

						// Start of new record
						item = createItem();
						item.setFile(file.getName().getURI());
					} else {
						endLine = lineNo;
					}
				} else {
					//
					if (!isComment(line) && item != null) {
						if (!line.equals("") && (key == null || line.matches("^[a-zA-Z_0-9.]+=.*"))) {
							endLine = lineNo;
							if (key != null) {
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
								value += "\n" + line;
							}
						}
					} else if (isComment(line) && item != null) {
						if (key != null) {
							item.set(key, trimComment(Util.trimTail(value)), section);
							key = null;
							value = null;
						}
					}
				}
				lineNo++;
				lastLine = line;
			}
			if (item != null) {
				if (key != null) {
					item.set(key, trimComment(Util.trimTail(value)), section);
				}
				if (startLine > endLine + 1) {
					Log.error(getClass().getName(), "Startline after endline in " + file + " at  " + startLine + " / " + endLine);
				}
				if (item instanceof AbstractMultiINIFileEntity) {
					((AbstractMultiINIFileEntity<?,?>) item).bounds(startLine, endLine + 1);
				}
				if (item.getEntityId() == null) {
					throw new IOException("Adding entity without ID at line " + lineNo);
				}
				doAddItem(file, item);
			}
		} finally {
			reader.close();
		}
	}

	protected boolean isEntryStart(String line) {
		return line.equals("[" + start + "]");
	}

	public final void setTrimComments(boolean trimComments) {
		this.trimComments = trimComments;
	}

	protected abstract T createItem();

	protected abstract void doAddItem(FileObject file, T item);

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
	/**
	 * Count the number trailing new lines up to a maximum of 2. This is used to
	 * determine if one or more new lines should be inserted at the end of the
	 * file when adding a new item. If the file ends without end line endings, 2
	 * new lines should be added to leave a gap. If a single new line already
	 * exists, only one line need be inserted. If there are already 2 new lines,
	 * no further lines need be added (the same should happen if the file is
	 * empty and so 2 is returned in this case)
	 * 
	 * @param file
	 * @return new lines
	 * @throws IOException
	 */
	public static int countTrailingNewLines(FileObject file) throws IOException {
		long bytes = file.getContent().getSize();
		if (bytes > 1) {
			int read = (int) Math.min(4l, bytes);
			long start = bytes - read;
			byte[] buf = new byte[read];
			try {
				RandomAccessContent rc = file.getContent().getRandomAccessContent(RandomAccessMode.READ);
				try {
					rc.seek(start);
					rc.readFully(buf);
				} finally {
					rc.close();
				}
			} catch (FileSystemException fse) {
				// Try using a stream and skip instead
				DataInputStream in = new DataInputStream(file.getContent().getInputStream());
				try {
					in.skip(start);
					in.readFully(buf);
				} finally {
					in.close();
				}
			}
			// Now we have trailing bit, count line feed characters and use the
			// maximum
			return Math.max(count(10, buf), count(13, buf));
		}
		return 2;
	}

	public static int count(int v, byte[] arr) {
		int c = 0;
		for (int b : arr) {
			if (v == b)
				c++;
		}
		return c;
	}

}
