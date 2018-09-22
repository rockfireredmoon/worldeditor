package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.icemoon.eartheternal.common.DirMonitor.Change;

public abstract class AbstractScripts<S extends Script<K>, K extends Serializable> extends AbstractEntities<S, K, String, IDatabase> {
	private static final long serialVersionUID = 1L;
	private DirMonitor monitor;

	public AbstractScripts(IDatabase database, Class<K> keyClass, String root) {
		super(database, keyClass, root);
		monitor = new DirMonitor(root);
		monitor.setSelector(createSelector());
	}

	protected FileSelector createSelector() {
		return new FileSelector() {
			@Override
			public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
				return fileInfo.getDepth() == 0;
			}

			@Override
			public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
				if (!fileInfo.getFile().isFile())
					return false;
				final String bn = fileInfo.getFile().getName().getBaseName().toLowerCase();
				return bn.endsWith(".nut") || bn.endsWith(".txt");
			}
		};
	}

	@Override
	protected boolean needsLoad() {
		return super.needsLoad() || monitor.isModified();
	}

	@Override
	public void save(S instance) {
		onBeforeSave(instance);
		super.save(instance);
		try {
			final FileObject file = VFS.getManager().resolveFile(instance.getFile());
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(file.getContent().getOutputStream()));
			try {
				pw.println(instance.getScript());
			} finally {
				pw.close();
			}
			monitor.addFile(file);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	protected void onBeforeSave(S instance) {
		instance.setFile(getFile() + "/" + instance.getEntityId() + "." + instance.getLanguage().getExtension());
	}

	@Override
	public void delete(S instance) {
		try {
			VFS.getManager().resolveFile(instance.getFile()).delete();
			super.delete(instance);
		} catch (FileSystemException fse) {
			throw new IllegalStateException("Failed to delete. " + instance, fse);
		}
	}

	@Override
	protected void doLoad() throws IOException {
		for (Map.Entry<String, Change> en : monitor.getChanges().entrySet()) {
			String key = en.getKey();
			FileObject resolved = VFS.getManager().resolveFile(key);
			switch (en.getValue()) {
			case CHANGED:
				monitor.addFile(resolved);
				S scr = get(filenameToEntityId(key, resolved));
				if (scr != null) {
					remove(scr);
				}
			case ADDED:
				S script = createItem();
				script.setFile(key);
				script.setEntityId(filenameToEntityId(key, resolved));
				monitor.addFile(resolved);
				add(script);
				break;
			case REMOVED:
				monitor.removeFile(resolved);
				scr = get(filenameToEntityId(key, resolved));
				if (scr != null) {
					remove(scr);
				}
				break;
			case ERROR:
				throw new IOException("Failed to load change " + key);
			}
		}
	}

	protected abstract K filenameToEntityId(String key, FileObject file);
}
