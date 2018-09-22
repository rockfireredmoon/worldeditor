package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.MapDef;

@SuppressWarnings("serial")
public final class MapDefListModel extends ListModel<MapDef> {
	private IModel<IDatabase> database;

	public MapDefListModel(IModel<IDatabase> database) {
		this.database = database;
	}

	public List<MapDef> getObject() {
		return new ArrayList<MapDef>(database.getObject().getMapDefs().values());
	}
}