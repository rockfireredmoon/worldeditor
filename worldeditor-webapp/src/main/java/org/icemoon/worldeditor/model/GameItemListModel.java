package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;

@SuppressWarnings("serial")
public final class GameItemListModel extends ListModel<GameItem> {
	private IModel<IDatabase> database;

	public GameItemListModel(IModel<IDatabase> database) {
		this.database = database;
	}

	public List<GameItem> getObject() {
		return new ArrayList<GameItem>(database.getObject().getItems().values());
	}
}