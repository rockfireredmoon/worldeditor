package org.icemoon.worldeditor;

import java.util.List;

import org.icemoon.eartheternal.common.Attachment;
import org.icemoon.eartheternal.common.Attachments;
import org.icemoon.eartheternal.common.AttachmentsList;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.ISiteData;
import org.icemoon.eartheternal.common.Attachment.AttachedFile;

public class AttachmentUtil {
	public static Attachment getAttachment(Entity<?> entity, ISiteData database) {
		Attachments attachments = getAttachments(entity, database);
		Attachment attachment = attachments.get(String.valueOf(entity.getEntityId()));
		if (attachment == null) {
			attachment = attachments.createNew(entity);
		}
		return attachment;
	}

	@SuppressWarnings("unchecked")
	public static Attachments getAttachments(Entity<?> entity, ISiteData database) {
		if (entity == null) {
			return null;
		}
		AttachmentsList list = database.getAttachmentsList();
		Attachments attachments = list.get(entity.getClass().getSimpleName());
		if (attachments == null) {
			attachments = list.createNew((Class<? extends Entity<?>>) entity.getClass());
		}
		return attachments;
	}

	public static AttachedFile getFirstAttachedFile(Entity<?> entity, ISiteData database) {
		Attachments attachments = getAttachments(entity, database);
		if (attachments == null) {
			return null;
		}
		Attachment attachment = attachments.get(String.valueOf(entity.getEntityId()));
		if (attachment == null) {
			return null;
		}
		final List<AttachedFile> files = attachment.getFiles();
		return files.isEmpty() ? null : files.get(0);
	}
}
