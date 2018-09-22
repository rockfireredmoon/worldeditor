package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class ZoneMarkers extends AbstractMultiINIFileEntities<ZoneMarker, Long, String, IDatabase> {
	public ZoneMarkers(String... files) {
		this(null, files);
	}

	public ZoneMarkers(IDatabase database, String... files) {
		super(database, Long.class, files);
		setMinId(1l);
		setMaxId(Long.valueOf(Integer.MAX_VALUE));
		setTrimComments(true);
	}

	@Override
	public synchronized void onSavingNew(ZoneMarker instance) {
		instance.setFile(getFile());
	}

	@Override
	protected ZoneMarker createItem() {
		return new ZoneMarker(getDatabase());
	}
}
