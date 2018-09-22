package org.icemoon.worldeditor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IRoot;

@SuppressWarnings("serial")
public class FilterableSortableEntitiesDataProvider<T extends Entity<K>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends SortableDataProvider<T, String> implements IFilterStateLocator<T> {
	private IModel<? extends Entities<T, K, L, R>> entitiesModel;
	private T filterState;

	public FilterableSortableEntitiesDataProvider(IModel<? extends Entities<T, K, L, R>> entitiesModel, T filter) {
		this("entityId", entitiesModel, filter);
	}

	public FilterableSortableEntitiesDataProvider(String defaultSortColumn, IModel<? extends Entities<T, K, L, R>> entitiesModel,
			T filter) {
		setSort(defaultSortColumn, SortOrder.ASCENDING);
		this.entitiesModel = entitiesModel;
		this.filterState = filter;
	}

	public Iterator<T> iterator(long first, long count) {
		List<T> l = getFilteredEntities();
		// Sort them
		if (getSort() != null) {
			final String sortProperty = getSort().getProperty();
			Collections.sort(l, new Comparator<T>() {
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				public int compare(T o1, T o2) {
					PropertyModel<Object> m1 = new PropertyModel<Object>(o1, sortProperty);
					PropertyModel<Object> m2 = new PropertyModel<Object>(o2, sortProperty);
					Object ob1 = m1.getObject();
					Object ob2 = m2.getObject();
					Comparable c1 = ob1 == null ? "" : (ob1 instanceof Comparable ? (Comparable) ob1 : ob1.toString());
					Comparable c2 = ob2 == null ? ""
							: (ob2 instanceof Comparable && !(c1 instanceof String) ? (Comparable) ob2 : ob2.toString());
					return c1.compareTo(c2);
				}
			});
			if (!getSort().isAscending()) {
				Collections.reverse(l);
			}
		}
		// Slice them
		long eidx = first + count;
		if (eidx > l.size()) {
			eidx = l.size();
		}
		List<T> subList = l.subList((int) first, (int) eidx);
		return subList.iterator();
	}

	private List<T> getFilteredEntities() {
		Entities<T, K, L, R> o = entitiesModel.getObject();
		List<T> l = new ArrayList<T>();
		// Filter them
		for (T t : new ArrayList<T>(o.values())) {
			if (matches(t, filterState)) {
				l.add(t);
			}
		}
		return l;
	}

	protected boolean matches(T object, T filter) {
		return true;
	}

	public long size() {
		return getFilteredEntities().size();
	}

	public IModel<T> model(T object) {
		return new DetachableEntityModel<T, K, L, R>(object, entitiesModel);
	}

	@Override
	public T getFilterState() {
		return filterState;
	}

	@Override
	public void setFilterState(T filterState) {
		this.filterState = filterState;
	}
}