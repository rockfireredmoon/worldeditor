package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.Entity;

@SuppressWarnings("serial")
public class EntityChooserProvider<T extends Entity<?>>  implements ITreeProvider<T> {

	private Entities<T, ?, ?, ?> entities;

	public EntityChooserProvider(Entities<T, ?, ?, ?> entities) {
		this.entities = entities;
	}

	@Override
	public void detach() {
	}

	@Override
	public Iterator<T> getRoots() {
		return entities.values().iterator();
	}

	@Override
	public boolean hasChildren(T object) {
		return false;
	}

	@Override
	public Iterator<T> getChildren(T object) {
		return new ArrayList<T>().iterator();
	}

	@Override
	public IModel<T> model(T object) {
		return new Model<T>(object);
	}

}
