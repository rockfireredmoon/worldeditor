package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.LootSet;
import org.icemoon.eartheternal.common.LootSets;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class LootSetsModel extends AbstractDatabaseModel<LootSets, LootSet, String, String, IDatabase> {
	public LootSetsModel(AbstractPage page) {
		super(page);
	}

	public LootSetsModel(IModel<? extends IDatabase> database) {
		super(database);
	}

	public LootSets getObject() {
		return database.getObject().getLootSets();
	}
}