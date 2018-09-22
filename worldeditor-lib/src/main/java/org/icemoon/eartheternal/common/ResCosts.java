package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class ResCosts extends AbstractTableFileEntities<ResCost, Integer, String, IDatabase> {
	public ResCosts(String... files) {
		this(null, files);
	}

	public ResCosts(IDatabase database, String... files) {
		super(database, Integer.class, files);
		setHasHeaderRow(false);
	}

	@Override
	protected ResCost createItem() {
		return new ResCost(getDatabase());
	}

	protected boolean isComment(String lastLine) {
		return lastLine.startsWith(";");
	}
}
