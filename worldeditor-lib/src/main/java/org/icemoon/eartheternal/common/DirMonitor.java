package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

public class DirMonitor {
	public enum Change {
		ADDED, REMOVED, CHANGED, ERROR
	}

	private Map<String, Map<String, Long>> lastModifed = new HashMap<String, Map<String, Long>>();
	private FileSelector selector;

	public DirMonitor(String... dirs) {
		for (String dir : dirs) {
			lastModifed.put(dir, new HashMap<String, Long>());
		}
	}

	public final FileSelector getSelector() {
		return selector;
	}

	public final void setSelector(FileSelector selector) {
		this.selector = selector;
	}
	
	public Map<String, Change> getChanges() {
		Map<String, Change> c = new TreeMap<String, Change>();
		for (String dir : lastModifed.keySet()) {
			try {
				FileObject[] ch = listFiles(dir);
				Map<String, Long> fm = lastModifed.get(dir);
				Map<String, FileObject> found = new HashMap<String, FileObject>();
				// First look for now files
				for (FileObject ci : ch) {
					found.put(ci.getName().getURI(), ci);
					if (fm == null || !fm.containsKey(ci.getName().getURI()))
						c.put(ci.getName().getURI(), Change.ADDED);
				}
				// Now look for deleted and changed files
				if (fm != null) {
					for (String ci : fm.keySet()) {
						if (found.containsKey(ci)) {
							if (fm.get(ci) != found.get(ci).getContent().getLastModifiedTime())
								c.put(ci, Change.CHANGED);
						} else {
							c.put(ci, Change.REMOVED);
						}
					}
				}
			} catch (IOException ioe) {
				c.put(dir, Change.ERROR);
			}
		}
		return c;
	}
	
	public FileObject[] listFiles() throws FileSystemException {
		List<FileObject> l = null;
		for(String d : lastModifed.keySet()) {
			FileObject[] c = listFiles(d);
			if(c != null) {
				if(l == null)
					l = new ArrayList<FileObject>();
				l.addAll(Arrays.asList(c));
			}
		}
		return l == null ? null : l.toArray(new FileObject[0]);
	}

	public FileObject[] listFiles(String dir) throws FileSystemException {
		FileObject d = VFS.getManager().resolveFile(dir);
		FileObject[] ch = selector == null ? d.getChildren() : d.findFiles(selector);
		return ch;
	}

	public boolean isModified() {
		return !getChanges().isEmpty();
	}

	public void removeFile(FileObject f) {
		String path = f.getName().getURI();
		for (String dir : lastModifed.keySet()) {
			if (path.startsWith(dir + "/")) {
				Map<String, Long> m = lastModifed.get(dir);
				if (m != null) {
					if (m.containsKey(path)) {
						m.remove(path);
					}
					if (m.isEmpty())
						lastModifed.remove(dir);
					break;
				}
			}
		}
	}

	public void addFile(FileObject f) {
		try {
			String path = f.getName().getURI();
			long lastMod = f.getContent().getLastModifiedTime();
			for (String dir : lastModifed.keySet()) {
				if (path.startsWith(dir + "/")) {
					Map<String, Long> m = lastModifed.get(dir);
					if (m == null)
						m = new HashMap<String, Long>();
					m.put(path, lastMod);
					break;
				}
			}
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Failed to add file.", ioe);
		}
	}
}
