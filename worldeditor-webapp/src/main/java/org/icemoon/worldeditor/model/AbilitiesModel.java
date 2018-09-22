package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.Ability;
import org.icemoon.eartheternal.common.IDatabase;

@SuppressWarnings("serial")
public final class AbilitiesModel extends ListModel<Ability> {
	private IModel<IDatabase> database;

	public AbilitiesModel(IModel<IDatabase> database) {
		this.database = database;
	}

	public List<Ability> getObject() {
		return new ArrayList<Ability>(database.getObject().getAbilities().values());
	}
}