package org.icemoon.eartheternal.common;

import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;

@SuppressWarnings("serial")
public class TerrainTemplates extends AbstractEntities<TerrainTemplate, String, String, IDatabase> {
	public TerrainTemplates(IDatabase database, String file) {
		super(database, String.class, file);
	}

	public TerrainTemplates(IDatabase database) {
		super(database, String.class);
	}

	@Override
	protected TerrainTemplate createItem() {
		return new TerrainTemplate(getDatabase());
	}

	@Override
	protected void doLoad() throws IOException {
		FileObject f = VFS.getManager().resolveFile(getFile());
		FileObject[] files = f.getChildren();
		if (files == null) {
			throw new IOException("Directory could not be read. Does it exist and is it readable?");
		}
		for (FileObject i : files) {
			if (i.getName().getBaseName().startsWith("Terrain-")) {
				String[] parts = i.getName().getBaseName().split("-|_");
				if (parts.length > 1) {
					String tn = parts[1];
					if (get(tn) == null)
						add(new TerrainTemplate(getDatabase(), tn, f.getName().getURI()));
				}
			}
		}
	}
}
