package org.icemoon.worldeditor.model;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.PackageResource;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Attachment.AttachedFile;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.AttachmentUtil;
import org.icemoon.worldeditor.FileObjectResource;

@SuppressWarnings("serial")
public class EntityAvatarModel extends Model<IResource> {
	private IModel<? extends Entity<?>> character;

	public EntityAvatarModel(IModel<? extends Entity<?>> character) {
		this.character = character;
	}

	public IResource getObject() {
		final AttachedFile af = AttachmentUtil.getFirstAttachedFile(character.getObject(), Application.getApp().getSiteData());
		if (af == null) {
			return new PackageResource(Application.class, "questionmark.png", null, null, null) {
			};
		}
		try {
			return new FileObjectResource(af.getType(), VFS.getManager().resolveFile(af.getFile()));
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}
}
