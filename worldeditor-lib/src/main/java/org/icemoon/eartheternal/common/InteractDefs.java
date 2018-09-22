package org.icemoon.eartheternal.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class InteractDefs extends AbstractMultiINIFileEntities<InteractDef, String, String, IDatabase> {

	public InteractDefs(String file) {
		this(null, file);
	}
	
	public InteractDefs(IDatabase database, String file) {
		super(database, String.class, file);
	}

	@Override
	protected InteractDef createItem() {
		return new InteractDef(getDatabase());
	}

	@Override
	public synchronized void save(InteractDef instance) {
		instance.setFile(getFile());
		super.save(instance);
	}

	@Override
	protected void createNew(InteractDef instance) throws IOException {
		instance.setFile(getFile());
		super.createNew(instance);
	}
}
