package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class ZoneDefs extends AbstractMultiINIFileEntities<ZoneDef, Long, String, IDatabase> {
	public ZoneDefs(String... files) {
		this(null, files);
	}

	public ZoneDefs(IDatabase database, String... files) {
		super(database, Long.class, files);
		setMinId(1l);
		setMaxId(Long.valueOf(Integer.MAX_VALUE));
		setTrimComments(true);
	}

	@Override
	protected ZoneDef createItem() {
		return new ZoneDef(getDatabase());
	}
}
