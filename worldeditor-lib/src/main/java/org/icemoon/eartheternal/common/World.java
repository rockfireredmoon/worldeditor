package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

public class World<R extends IRoot> extends AbstractEntities<Sceneries<R>, Long, String, R> {
	private static final long serialVersionUID = 1L;
	private Map<Long, Scenery<R>> world = new HashMap<Long, Scenery<R>>();

	public World(String root) {
		this(null, root);
	}

	public World(R database, String root) {
		super(database, Long.class, root);
	}

	@Override
	public void doLoad() throws IOException {
		FileObject f = VFS.getManager().resolveFile(getFile());
		for (FileObject file : f.getChildren()) {
			Log.debug("Loading file " + file);
			if (file.getType().equals(FileType.FOLDER)) {
				if (!file.exists()) {
					Log.error("files", "Missing " + file);
				} else {
					final long id = Long.parseLong(file.getName().getBaseName());
					if (!contains(id)) {
						final Sceneries<R> item = createItem();
						item.setEntityId(id);
						item.setFile(f.getName().getURI());
						add(item);
					}
				}
			}
		}
	}

	Map<Long, Scenery<R>> getWorld() {
		return world;
	}

	@Override
	protected Sceneries<R> createItem() {
		return new Sceneries<R>(getDatabase(), getFile(), this);
	}

	public Scenery<R> getInAny(Long entityId) {
		for (Sceneries<R> r : values()) {
			Scenery<R> s = r.get(entityId);
			if (s != null)
				return s;
		}
		return null;
	}
}
