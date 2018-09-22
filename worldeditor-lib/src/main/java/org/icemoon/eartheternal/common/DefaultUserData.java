package org.icemoon.eartheternal.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

public class DefaultUserData implements IUserData {
	private FileObject serverDir;
	private Accounts accounts;
	private GameCharacters characters;
	private World groveList;
	private SessionVars sessionVars;
	private Map<Class<?>, DuplicateHandler<?>> duplicateHandlers = new HashMap<Class<?>, DuplicateHandler<?>>();

	public DefaultUserData() {
	}

	public DefaultUserData(File serverDir) {
		try {
			setServerDir(VFS.getManager().resolveFile(serverDir.getAbsolutePath()));
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	public DefaultUserData(FileObject serverDir) {
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

	@Override
	public synchronized Accounts getAccounts() {
		if (accounts == null) {
			try {
				accounts = new Accounts(this, serverDir.resolveFile("Accounts").getName().getURI());
				accounts.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return accounts;
	}

	@Override
	public synchronized GameCharacters getCharacters() {
		if (characters == null) {
			try {
				characters = new GameCharacters(this, serverDir.resolveFile("Characters").getName().getURI());
				characters.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return characters;
	}

	@Override
	public synchronized World getGroveList() {
		if (groveList == null) {
			try {
				groveList = new World(this, serverDir.resolveFile("Grove").getName().getURI());
				groveList.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return groveList;
	}

	@Override
	public synchronized FileObject getServerDirectory() {
		return serverDir;
	}

	@Override
	public synchronized SessionVars getSessionVars() {
		if (sessionVars == null) {
			try {
				sessionVars = new SessionVars(this, serverDir.resolveFile("SessionVars.txt").getName().getURI());
				sessionVars.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return sessionVars;
	}

	protected void setServerDir(FileObject serverDir) {
		this.serverDir = serverDir;
	}
}
