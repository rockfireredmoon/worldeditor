package org.icemoon.eartheternal.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

public class StaticDataDatabase implements IStatic {
	private FileObject directory;
	private MapImages gameMaps;
	private GameIcons gameIcons;
	private Map<Class<?>, DuplicateHandler<?>> duplicateHandlers = new HashMap<Class<?>, DuplicateHandler<?>>();

	public StaticDataDatabase() {
		this(null);
	}

	public StaticDataDatabase(FileObject directory) {
		if (directory == null) {
			try {
				directory = VFS.getManager().resolveFile(new File("src"), "main/data");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		this.directory = directory;
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

	public GameIcons getGameIcons() {
		if (gameIcons == null) {
			try {
				gameIcons = new GameIcons(this, directory.resolveFile("Icon").getName().getURI());
				gameIcons.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return gameIcons;
	}

	public MapImages getMapImages() {
		if (gameMaps == null) {
			try {
				gameMaps = new MapImages(this, directory.resolveFile("Maps").getName().getURI());
				gameMaps.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return gameMaps;
	}

	public FileObject getServerDirectory() {
		return directory;
	}
}
