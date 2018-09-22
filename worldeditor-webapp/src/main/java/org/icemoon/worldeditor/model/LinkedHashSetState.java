package org.icemoon.worldeditor.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.Entity;

@SuppressWarnings("serial")
public final class LinkedHashSetState<T extends Entity<? extends Object>> implements IModel<Set<T>> {
	private Set<T> object = new LinkedHashSet<T>();

	@Override
	public void detach() {
	}

	@Override
	public Set<T> getObject() {
		return object;
	}

	@Override
	public void setObject(Set<T> object) {
		this.object = object;
	}
}