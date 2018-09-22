package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.icemoon.eartheternal.common.DirMonitor.Change;

public class Dialogs extends AbstractSeparateINIFileEntities<Dialog, String, String, IDatabase> {
	private static final long serialVersionUID = 1L;
	private DirMonitor monitor;

	public Dialogs(String root) {
		this(null, root);
	}

	public Dialogs(IDatabase database, String root) {
		super(database, String.class, root);
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
	protected Dialog createItem() {
		return new Dialog(getDatabase());
	}

	@Override
	protected boolean needsLoad() {
		return super.needsLoad() || monitor.isModified();
	}

	@Override
	public void save(Dialog instance) {
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
				Dialog scr = get(FilenameUtils.getBaseName(en.getKey()));
				if (scr != null) {
					remove(scr);
				}
			case ADDED:
				String name = FilenameUtils.getBaseName(en.getKey());
				Dialog script = createItem();
				script.setFile(en.getKey());
				script.setEntityId(name);
				monitor.addFile(VFS.getManager().resolveFile(en.getKey()));
				add(script);
				break;
			case REMOVED:
				monitor.removeFile(VFS.getManager().resolveFile(en.getKey()));
				scr = get(FilenameUtils.getBaseName(en.getKey()));
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
	protected String createKey(String filename) {
		return FilenameUtils.getBaseName(filename);
	}
}
