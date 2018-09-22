package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.SpawnPackage;

@SuppressWarnings("serial")
public final class SpawnPackageListModel extends ListModel<SpawnPackage> {
	private IModel<IDatabase> database;

	public SpawnPackageListModel(IModel<IDatabase> database) {
		this.database = database;
	}

	public List<SpawnPackage> getObject() {
		return new ArrayList<SpawnPackage>(database.getObject().getSpawnPackages().values());
	}
}