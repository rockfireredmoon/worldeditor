package org.icemoon.worldeditor.model;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.worldeditor.AbstractPage;

public class AbstractDatabaseModel<T extends Entities<E, K, L, R>, E extends Entity<K>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends Model<T> {
	private static final long serialVersionUID = 1L;
	protected IModel<? extends IDatabase> database;

	public AbstractDatabaseModel(AbstractPage page) {
		this(new PropertyModel<IDatabase>(page, "database"));
	}

	public AbstractDatabaseModel(IModel<? extends IDatabase> database) {
		this.database = database;
	}
}
