package org.icemoon.worldeditor.entities;

import org.icemoon.eartheternal.common.AbstractMultiINIFileEntities;
import org.icemoon.eartheternal.common.IRoot;

@SuppressWarnings("serial")
public class DatabaseStores extends AbstractMultiINIFileEntities<DatabaseStore, String, String, IRoot> {
	private String workspace;

	public DatabaseStores(IRoot database, String workspace, String... files) {
		super(database, String.class, files);
		this.workspace = workspace;
		setTrimComments(true);
	}

	@Override
	protected DatabaseStore createItem() {
		return new DatabaseStore(getDatabase(), this);
	}

	@Override
	public synchronized void save(DatabaseStore instance) {
		instance.setFile(getFile());
		super.save(instance);
	}

	public String getWorkspace() {
		return workspace;
	}
}
