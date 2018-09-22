package org.icemoon.eartheternal.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

public class SessionVars extends AbstractEntity<Void, IUserData> {
	private static final long serialVersionUID = 1L;
	private Map<String, Long> ids = new TreeMap<String, Long>();

	public SessionVars(IUserData database, String path) {
		super(database, path, null);
	}

	public synchronized long peekId(String id) {
		return ids.containsKey(id) ? ids.get(id) : 0;
	}

	public synchronized long nextId(String id) {
		return nextId(id, 1);
	}

	public synchronized long nextId(String id, int incr) {
		if (!ids.containsKey(id)) {
			ids.put(id, 0l);
		}
		long v = ids.get(id);
		ids.put(id, v + incr);
		saveIds();
		return v;
	}

	public synchronized void setId(String id, long val) {
		ids.put(id, val);
		saveIds();
	}

	protected void saveIds() {
		try {
			INIWriter iw = new INIWriter(getFileObject().getContent().getOutputStream());
			try {
				iw.println("; This file stores session settings and is automatically saved on exit.");
				for (Map.Entry<String, Long> en : ids.entrySet()) {
					iw.println(en.getKey() + "=" + en.getValue());
				}
			} finally {
				iw.close();
			}
		} catch (IOException ioe) {
			throw new IllegalStateException("Failed to save SessionVars.", ioe);
		}
	}

	@Override
	protected void doLoad() throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(getFileObject().getContent().getInputStream()));
		try {
			String line = null;
			while ((line = r.readLine()) != null) {
				line = line.trim();
				if (!line.equals("") & !line.startsWith(";")) {
					int idx = line.indexOf('=');
					if (idx == -1)
						throw new IOException("Invalid data in " + getFile());
					ids.put(line.substring(0, idx), Long.parseLong(line.substring(idx + 1)));
				}
			}
		} finally {
		}
	}

	private FileObject getFileObject() throws FileSystemException {
		return VFS.getManager().resolveFile(getFile());
	}
}
