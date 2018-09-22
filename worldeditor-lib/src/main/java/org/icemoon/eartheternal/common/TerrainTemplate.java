package org.icemoon.eartheternal.common;

import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class TerrainTemplate extends AbstractEntities<TerrainTemplateTile, Tile, String, IDatabase> {
	public TerrainTemplate(IDatabase database) {
		super(database, Tile.class);
	}

	public TerrainTemplate(IDatabase database, String entityId, String file) {
		super(database, Tile.class, file);
		setEntityId(entityId);
	}

	@Override
	protected void doLoad() throws IOException {
		FileObject f = VFS.getManager().resolveFile(getFile());
		FileObject[] files = f.getChildren();
		if (files == null) {
			throw new IOException("Directory could not be read. Does it exist and is it readable?");
		}
		for (FileObject i : files) {
			if (i.getName().getBaseName().startsWith("Terrain-" + getEntityId() + "_")) {
				String[] parts = i.getName().getBaseName().split("_");
				if (parts.length > 1) {
					String p[] = parts[1].replaceAll("x", "").replace("y", ",").split(",");
					add(new TerrainTemplateTile(getDatabase(), new Tile(Integer.parseInt(p[0]), Integer.parseInt(p[1]))));
				}
				else {
					// TODO general template stuff
				}
			}
		}
	}

	@Override
	protected TerrainTemplateTile createItem() {
		return new TerrainTemplateTile(getDatabase());
	}
}
