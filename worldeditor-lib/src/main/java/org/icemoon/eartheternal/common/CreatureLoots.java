package org.icemoon.eartheternal.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class CreatureLoots extends AbstractINIFileEntities<CreatureLoot, Long, String, IDatabase> {
	public CreatureLoots(String... files) {
		this(null, files);
	}

	public CreatureLoots(IDatabase database, String... files) {
		super(database, Long.class, files);
		setTrimComments(true);
	}

	@Override
	protected CreatureLoot createItem() {
		return new CreatureLoot(getDatabase());
	}

	@Override
	protected void writeInstance(CreatureLoot instance, boolean append) throws IOException {
		// TODO Auto-generated method stub
	}
}
