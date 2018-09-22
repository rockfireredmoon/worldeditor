package org.icemoon.eartheternal.common;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class InstanceScripts extends AbstractScripts<InstanceScript, Long> {
	public InstanceScripts(String root) {
		this(null, root);
	}

	public InstanceScripts(IDatabase database, String root) {
		super(database, Long.class, root);
	}

	@Override
	protected InstanceScript createItem() {
		return new InstanceScript(getDatabase());
	}

	protected void onBeforeSave(InstanceScript instance) {
		String dir = getFile() + "/" + instance.getEntityId();
		try {
			FileObject fo = VFS.getManager().resolveFile(dir);
			if (!fo.exists()) {
				fo.createFolder();
			}
		} catch (FileSystemException fse) {
		}
		instance.setFile(dir + "/Script." + instance.getLanguage().getExtension());
	}

	protected FileSelector createSelector() {
		return new FileSelector() {
			@Override
			public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
				return fileInfo.getDepth() < 2;
			}

			@Override
			public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
				if (!fileInfo.getFile().isFile())
					return false;
				final String bn = fileInfo.getFile().getName().getBaseName().toLowerCase();
				return bn.equals("script.nut") || bn.endsWith("script.txt");
			}
		};
	}

	@Override
	protected Long filenameToEntityId(String key, FileObject file) {
		return Long.parseLong(FilenameUtils.getBaseName(file.getName().getParent().getBaseName()));
	}
}
