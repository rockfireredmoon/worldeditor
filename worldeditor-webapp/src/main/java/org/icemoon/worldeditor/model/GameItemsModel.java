package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GameItems;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class GameItemsModel extends AbstractDatabaseModel<GameItems, GameItem, Long, String, IDatabase> {
	public GameItemsModel(AbstractPage page) {
		super(page);
	}

	public GameItemsModel(IModel<? extends IDatabase> database) {
		super(database);
	}

	public GameItems getObject() {
		return database.getObject().getItems();
	}
}