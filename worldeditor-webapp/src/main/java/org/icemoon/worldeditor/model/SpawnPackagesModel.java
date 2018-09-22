package org.icemoon.worldeditor.model;

import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.SpawnPackage;
import org.icemoon.eartheternal.common.SpawnPackages;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class SpawnPackagesModel extends AbstractDatabaseModel<SpawnPackages, SpawnPackage, String, String, IDatabase> {
	public SpawnPackagesModel(AbstractPage page) {
		super(page);
	}

	public SpawnPackages getObject() {
		return database.getObject().getSpawnPackages();
	}
}