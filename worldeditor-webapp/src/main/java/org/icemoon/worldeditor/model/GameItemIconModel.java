package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;

@SuppressWarnings("serial")
public class GameItemIconModel extends Model<String> {
	private IModel<Long> itemIdModel;
	private String property;
	private IModel<IDatabase> database;

	public GameItemIconModel(IModel<Long> itemIdModel, String property, IModel<IDatabase> database) {
		this.itemIdModel = itemIdModel;
		this.property = property;
		this.database = database;
	}

	public String getObject() {
		final Long object = itemIdModel.getObject();
		GameItem item = object == null ? null : database.getObject().getItems().get(object);
		return item == null ? null : new PropertyModel<String>(item, property).getObject();
	}
}