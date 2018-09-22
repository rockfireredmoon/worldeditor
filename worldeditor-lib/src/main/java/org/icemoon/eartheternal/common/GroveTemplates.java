package org.icemoon.eartheternal.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class GroveTemplates extends AbstractTableFileEntities<GroveTemplate, String, String, IDatabase> {
	public GroveTemplates(String... files) {
		this(null, files);
	}

	public GroveTemplates(IDatabase database, String... files) {
		super(database, String.class, files);
	}

	@Override
	protected GroveTemplate createItem() {
		return new GroveTemplate(getDatabase());
	}

	@Override
	protected void writeInstance(GroveTemplate instance, boolean append) throws IOException {
	}
}
