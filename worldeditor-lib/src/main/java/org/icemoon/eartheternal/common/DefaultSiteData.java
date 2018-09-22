package org.icemoon.eartheternal.common;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

public class DefaultSiteData implements ISiteData {
	private FileObject serverDir;
	private AttachmentsList attacmentsList;
	private Preferences prefs;
	private Map<Class<?>, DuplicateHandler<?>> duplicateHandlers = new HashMap<Class<?>, DuplicateHandler<?>>();

	public DefaultSiteData(Preferences prefs) {
		this.prefs = prefs;
	}

	public DefaultSiteData(File serverDir, Preferences prefs) {
		this.prefs = prefs;
		try {
			setServerDir(VFS.getManager().resolveFile(serverDir.getAbsolutePath()));
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	public DefaultSiteData(FileObject serverDir, Preferences prefs) {
		this.prefs = prefs;
		setServerDir(serverDir);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> DuplicateHandler<T> getDuplicateHandler(Class<T> clazz) {
		return (DuplicateHandler<T>) duplicateHandlers.get(clazz);
	}

	@Override
	public void setDuplicateHandler(Class<?> clazz, DuplicateHandler<?> handler) {
		duplicateHandlers.put(clazz, handler);
	}

	protected void setServerDir(FileObject serverDir) {
		this.serverDir = serverDir;
	}

	@Override
	public synchronized AttachmentsList getAttachmentsList() {
		if (attacmentsList == null) {
			try {
				attacmentsList = new AttachmentsList(this, serverDir.resolveFile("Attachment").getName().getURI());
				attacmentsList.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return attacmentsList;
	}

	@Override
	public FileObject getServerDirectory() {
		return serverDir;
	}

	@Override
	public Preferences getPreferences(Principal user) {
		return user == null ? prefs : prefs.node(user.getName());
	}
}
