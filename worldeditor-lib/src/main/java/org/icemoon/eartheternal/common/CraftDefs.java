package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class CraftDefs extends AbstractTableFileEntities<CraftDef, String, String, IDatabase> {
	public CraftDefs(String... files) {
		this(null, files);
	}

	public CraftDefs(IDatabase database, String... files) {
		super(database, String.class, files);
	}

	@Override
	protected CraftDef createItem() {
		return new CraftDef(getDatabase());
	}
}
