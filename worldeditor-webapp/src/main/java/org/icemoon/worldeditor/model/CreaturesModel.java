package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.Creatures;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class CreaturesModel extends AbstractDatabaseModel<Creatures,Creature,Long,String, IDatabase> {
	public CreaturesModel(AbstractPage page) {
		super(page);
	}

	public CreaturesModel(IModel<? extends IDatabase> database) {
		super(database);
	}

	public Creatures getObject() {
		return database.getObject().getCreatures();
	}
}