package org.icemoon.worldeditor.model;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.FileObjectResource;

public class FileObjectResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;
	private String contentType;
	private String file;

	public FileObjectResourceReference(String name, String contentType, String file) {
		super(name);
		Application.get().getResourceReferenceRegistry().registerResourceReference(this);
		this.contentType = contentType;
		this.file = file;
	}

	@Override
	public IResource getResource() {
		try {
			return new FileObjectResource(contentType, VFS.getManager().resolveFile(file));
		} catch (FileSystemException e) {
			return null;
		}
	}

}