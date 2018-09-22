package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class LootSets extends AbstractTableFileEntities<LootSet, String, String, IDatabase> {
	public LootSets(String... files) {
		this(null, files);
	}

	public LootSets(IDatabase database, String... files) {
		super(database, String.class, files);
	}

	@Override
	protected LootSet createItem() {
		return new LootSet(getDatabase());
	}
}
