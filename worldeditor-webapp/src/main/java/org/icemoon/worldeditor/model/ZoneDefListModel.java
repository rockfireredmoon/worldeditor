package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.ZoneDef;

@SuppressWarnings("serial")
public final class ZoneDefListModel extends ListModel<ZoneDef> {
	private IModel<IDatabase> database;

	public ZoneDefListModel(IModel<IDatabase> database) {
		this.database = database;
	}

	public List<ZoneDef> getObject() {
		return new ArrayList<ZoneDef>(database.getObject().getZoneDefs().values());
	}
}