package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class DropProfiles extends AbstractTableFileEntities<DropProfile, String, String, IDatabase> {

	public DropProfiles(String... files) {
		this(null, files);
	}
	public DropProfiles(IDatabase database, String... files) {
		super(database, String.class, files);
	}

	@Override
	protected DropProfile createItem() {
		return new DropProfile(getDatabase());
	}

}
