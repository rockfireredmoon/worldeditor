package org.icemoon.eartheternal.common;

import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class AttachmentsList extends AbstractEntities<Attachments, String, String, ISiteData> {
	public AttachmentsList(String root) {
		this(null, root);
	}

	public AttachmentsList(ISiteData database, String root) {
		super(database, String.class, root);
		try {
			FileObject f = VFS.getManager().resolveFile(root);
			f.createFolder();
		} catch (FileSystemException fse) {
			throw new RuntimeException(fse);
		}
	}

	public Attachments createNew(Class<? extends Entity<?>> clazz) {
		try {
			FileObject f = VFS.getManager().resolveFile(getFile());
			FileObject file = f.resolveFile(clazz.getSimpleName() + ".txt");
			Attachments a = new Attachments(getDatabase(), file.getName().getURI());
			save(a);
			return a;
		} catch (FileSystemException fse) {
			throw new RuntimeException(fse);
		}
	}

	@Override
	protected Attachments createItem() {
		return new Attachments(getDatabase());
	}

	@Override
	protected void doLoad() throws IOException {
		FileObject f = VFS.getManager().resolveFile(getFile());
		FileObject[] files = f.getChildren();
		if (files == null) {
			throw new IOException("Directory " + getFile() + " could not be read. Does it exist and is it readable?");
		}
		for (FileObject instanceDir : files) {
			if (instanceDir.getType().equals(FileType.FILE) && instanceDir.getName().getBaseName().endsWith(".txt")) {
				Attachments mim = new Attachments(getDatabase(), instanceDir.getName().getURI());
				mim.load();
				add(mim);
			}
		}
	}
}
