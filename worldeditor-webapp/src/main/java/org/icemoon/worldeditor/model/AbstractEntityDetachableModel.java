package org.icemoon.worldeditor.model;

import org.apache.wicket.model.LoadableDetachableModel;
import org.icemoon.eartheternal.common.Entity;

@SuppressWarnings("serial")
public abstract class AbstractEntityDetachableModel <T extends Entity, K extends Object> extends LoadableDetachableModel<T> {
	protected final K id;

	public AbstractEntityDetachableModel(T c) {
		this((K) c.getEntityId());
	}

	public AbstractEntityDetachableModel(K id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof AbstractEntityDetachableModel) {
			AbstractEntityDetachableModel other = (AbstractEntityDetachableModel) obj;
			return other.id == id;
		}
		return false;
	}

}