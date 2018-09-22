package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class LootPackages extends AbstractTableFileEntities<LootPackage, String, String, IDatabase> {
	public LootPackages(IDatabase database, String... files) {
		super(database, String.class, files);
	}

	@Override
	protected LootPackage createItem() {
		return new LootPackage(getDatabase());
	}
}
