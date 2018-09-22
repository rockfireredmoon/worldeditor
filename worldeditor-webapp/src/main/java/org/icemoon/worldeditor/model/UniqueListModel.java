package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.Entity;

@SuppressWarnings("serial")
public class UniqueListModel<T extends Comparable<T>, E extends Entity<?>> extends ListModel<T> {
	
	private IModel<List<E>> source;
	private String expression;

	public UniqueListModel(IModel<List<E>> source, String expression) {
		this.source = source;
		this.expression = expression;
	}
	
	public List<T> getObject() {
		List<T> l = new ArrayList<T>();
		for(E entity : source.getObject()) {
			PropertyModel<T> p = new PropertyModel<T>(entity, expression);
			T val = p.getObject();
			if(val != null && !String.valueOf(val).equals("") && !l.contains(val)) {
				l.add(val);
			}
		}
		Collections.sort(l);
		return l;
	}

}
