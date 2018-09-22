package org.icemoon.worldeditor.model;

import java.io.Serializable;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IRoot;

@SuppressWarnings("serial")
public class DetachableEntityModel<T extends Entity<K>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends AbstractEntityDetachableModel<T, K> {
	private IModel<? extends Entities<T, K, L, R>> entitiesModel;

	public DetachableEntityModel(T c, IModel<? extends Entities<T, K, L, R>> entitiesModel) {
		super(c);
		this.entitiesModel = entitiesModel;
	}

	public DetachableEntityModel(K id, IModel<? extends Entities<T, K, L, R>> entitiesModel) {
		super(id);
		this.entitiesModel = entitiesModel;
	}

	@Override
	protected T load() {
		return entitiesModel.getObject().get(id);
	}
}