package org.icemoon.worldeditor.model;

import org.icemoon.eartheternal.common.DropProfile;
import org.icemoon.eartheternal.common.DropProfiles;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class DropProfilesModel extends AbstractDatabaseModel<DropProfiles, DropProfile, String, String, IDatabase> {
	public DropProfilesModel(AbstractPage page) {
		super(page);
	}

	public DropProfiles getObject() {
		return database.getObject().getDropProfiles();
	}
}