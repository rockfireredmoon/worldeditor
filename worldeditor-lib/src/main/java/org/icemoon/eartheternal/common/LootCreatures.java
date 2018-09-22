package org.icemoon.eartheternal.common;

import org.apache.commons.vfs2.FileObject;

@SuppressWarnings("serial")
public class LootCreatures extends AbstractTableFileEntities<LootCreature, Long, String, IDatabase> {
	public LootCreatures(String... files) {
		this(null, files) ;
	}

	public LootCreatures(IDatabase database, String... files) {
		super(database, Long.class, files);
	}

	@Override
	protected LootCreature createItem() {
		return new LootCreature(getDatabase());
	}

	protected void doAddItem(FileObject file, LootCreature item, int line) {
		item.setEntityId((long) line);
		super.doAddItem(file, item, line);
	}
}
