package org.icemoon.eartheternal.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.icemoon.eartheternal.common.Attachment.AttachedFile;

@SuppressWarnings("serial")
public class Attachments extends AbstractMultiINIFileEntities<Attachment, String, String, ISiteData> {

	public Attachments() {
		super(String.class);
	}
	
	public Attachments(ISiteData database) {
		super(database, String.class);
	}

	public Attachments(ISiteData database, String file) {
		super(database, String.class, file);
		setEntityId(FilenameUtils.getName(file));
	}

	public Attachment createNew(Entity<?> entity) {
		Attachment a = new Attachment(getDatabase(), getFile(), String.valueOf(entity.getEntityId()), this);
		save(a);
		return get(a.getEntityId());
	}

	public Long getInstanceId() {
		return Long.parseLong(FilenameUtils.getBaseName(getFile()));
	}

	public int getMaxAttachments() {
		return 1;
	}

	@Override
	public void save(Attachment instance) {
		// Restrict the number of attachments
		final List<AttachedFile> files = instance.getFiles();
		Collections.sort(files, new Comparator<AttachedFile>() {
			@Override
			public int compare(AttachedFile o1, AttachedFile o2) {
				return o1.getEntityId().compareTo(o2.getEntityId());
			}
		});
		while (files.size() > getMaxAttachments()) {
			try {
				FileObject f = VFS.getManager().resolveFile(files.get(0).getFile());
				f.delete();
			} catch (FileSystemException e) {
				throw new RuntimeException(e);
			}
			files.remove(0);
		}
		super.save(instance);
	}

	@Override
	protected Attachment createItem() {
		return new Attachment(getDatabase(), this);
	}
}
