package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.eartheternal.common.Quests;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class QuestsModel extends AbstractDatabaseModel<Quests, Quest, Long, String, IDatabase> {
	public QuestsModel(AbstractPage page) {
		super(page);
	}

	public QuestsModel(IModel<? extends IDatabase> database) {
		super(database);
	}

	public Quests getObject() {
		return database.getObject().getQuests();
	}
}