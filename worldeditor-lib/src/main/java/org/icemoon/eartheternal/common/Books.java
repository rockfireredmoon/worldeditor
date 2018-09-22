package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.icemoon.eartheternal.common.DirMonitor.Change;

public class Books extends AbstractSeparateINIFileEntities<Book, Long, String, IDatabase> {
	private static final long serialVersionUID = 1L;
	private DirMonitor monitor;

	public Books(String root) {
		this(null, root);
	}

	public Books(IDatabase database, String root) {
		super(database, Long.class, root);
		setMinId(1l);
		setMaxId(Long.valueOf(Integer.MAX_VALUE));
		monitor = new DirMonitor(root);
		monitor.setSelector(new FileSelector() {
			@Override
			public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
				return fileInfo.getDepth() == 0;
			}

			@Override
			public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
				if (!fileInfo.getFile().isFile())
					return false;
				final String bn = fileInfo.getFile().getName().getBaseName().toLowerCase();
				return bn.endsWith(".txt");
			}
		});
	}

	@Override
	protected Long getMinId() {
		// TODO Auto-generated method stub
		return super.getMinId();
	}

	@Override
	protected Book createItem() {
		return new Book(getDatabase());
	}

	@Override
	protected boolean needsLoad() {
		return super.needsLoad() || monitor.isModified();
	}

	@Override
	public void save(Book instance) {
		checkLoad();
		instance.setFile(getFile() + "/" + instance.getEntityId() + ".txt");
		boolean exists = instance.getEntityId() != null && contains(instance.getEntityId());
		try {
			if (exists) {
				Log.debug(instance + " already exists, so replacing");
				replace(instance);
				writeInstance(instance, false);
			} else {
				Log.debug(instance + " is new, so adding to end of file");
				createNew(instance);
				add(instance);
			}
			try {
				monitor.addFile(VFS.getManager().resolveFile(instance.getFile()));
			} catch (FileSystemException e) {
				throw new IllegalStateException("Failed to add file.", e);
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	@Override
	protected void doLoad() throws IOException {
		for (Map.Entry<String, Change> en : monitor.getChanges().entrySet()) {
			switch (en.getValue()) {
			case CHANGED:
				monitor.addFile(VFS.getManager().resolveFile(en.getKey()));
				Book scr = get(Long.parseLong(FilenameUtils.getBaseName(en.getKey())));
				if (scr != null) {
					remove(scr);
				}
			case ADDED:
				Long name = Long.parseLong(FilenameUtils.getBaseName(en.getKey()));
				Book script = createItem();
				script.setFile(en.getKey());
				script.setEntityId(name);
				monitor.addFile(VFS.getManager().resolveFile(en.getKey()));
				add(script);
				break;
			case REMOVED:
				monitor.removeFile(VFS.getManager().resolveFile(en.getKey()));
				scr = get(Long.parseLong(FilenameUtils.getBaseName(en.getKey())));
				if (scr != null) {
					remove(scr);
				}
				break;
			case ERROR:
				throw new IOException("Failed to load change " + en.getKey());
			}
		}
	}

	@Override
	protected Long createKey(String filename) {
		return Long.parseLong(FilenameUtils.getBaseName(filename));
	}
}
