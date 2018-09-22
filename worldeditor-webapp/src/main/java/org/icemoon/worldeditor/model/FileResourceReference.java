package org.icemoon.worldeditor.model;

import java.io.File;

import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.FileResource;

public class FileResourceReference extends ResourceReference {
	private static final long serialVersionUID = 1L;
	private String contentType;
	private File file;

	public FileResourceReference(String name, String contentType, File file) {
		super(name);
		Application.get().getResourceReferenceRegistry().registerResourceReference(this);
		this.contentType = contentType;
		this.file= file;
	}

	@Override
	public IResource getResource() {
		return new FileResource(contentType, file);
	}

}