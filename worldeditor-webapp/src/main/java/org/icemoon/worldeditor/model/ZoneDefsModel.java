package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.eartheternal.common.ZoneDefs;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class ZoneDefsModel extends AbstractDatabaseModel<ZoneDefs, ZoneDef, Long, String, IDatabase> {
	public ZoneDefsModel(AbstractPage page) {
		super(page);
	}

	public ZoneDefsModel(IModel<? extends IDatabase> database) {
		super(database);
	}

	public ZoneDefs getObject() {
		return database.getObject().getZoneDefs();
	}
}