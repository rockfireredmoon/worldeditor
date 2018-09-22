package org.icemoon.worldeditor.model;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IRoot;

@SuppressWarnings("serial")
public abstract class DatabaseModel<T extends Entity<K>, K extends Serializable, L extends Serializable, R extends IRoot> implements IModel<Entities<T, K, L, R>> {
	@Override
	public void detach() {
	}

	@Override
	public void setObject(Entities<T, K, L, R> object) {
	}
}