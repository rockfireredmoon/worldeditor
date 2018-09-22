package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class Abilities extends AbstractTableFileEntities<Ability, Integer, String, IDatabase> {
	public Abilities(String... files) {
		this(null, files);
	}

	public Abilities(IDatabase database, String... files) {
		super(database, Integer.class, files);
		setEolComments(false);
		setMinId(1);
		setMaxId(Integer.valueOf(Short.MAX_VALUE));
		setMinColumns(28);
		setMaxColumns(29);
		setQuotedFields(true);
		setHasHeaderRow(false);
	}

	@Override
	protected Ability createItem() {
		return new Ability(getDatabase());
	}
}
