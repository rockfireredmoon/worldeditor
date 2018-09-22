package org.icemoon.eartheternal.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class TerrainTemplateTile extends AbstractEntity<Tile, IDatabase> {
	public TerrainTemplateTile(IDatabase database) {
		super(database);
	}
	public TerrainTemplateTile(IDatabase database, Tile entityId) {
		super(database, null, entityId);
	}

	@Override
	protected void doLoad() throws IOException {
	}
}
