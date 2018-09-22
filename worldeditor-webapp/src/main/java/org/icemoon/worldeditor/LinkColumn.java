package org.icemoon.worldeditor;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilteredAbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.Entities;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IRoot;

@SuppressWarnings("serial")
public final class LinkColumn<C extends Entity<?>, T extends Entity<K>, K extends Serializable, L extends Serializable, R extends IRoot>
		extends FilteredAbstractColumn<C, String> {
	private IModel<String> filterModel;
	private String expression;
	private String displayExpression;
	private IModel<? extends Entities<T, K, L, R>> creaturesModel;

	public LinkColumn(IModel<String> displayModel, String sortProperty, IModel<String> filterModel,
			IModel<? extends Entities<T, K, L, R>> creaturesModel, String lookupExpression, String displayExpression) {
		super(displayModel, sortProperty);
		this.filterModel = filterModel;
		this.creaturesModel = creaturesModel;
		this.expression = lookupExpression;
		this.displayExpression = displayExpression;
	}

	@Override
	public void populateItem(Item<ICellPopulator<C>> cellItem, String componentId, final IModel<C> rowModel) {
		final PropertyModel<K> p = new PropertyModel<K>(rowModel, expression);
		cellItem.add(new Label(componentId, new Model<String>() {
			@Override
			public String getObject() {
				T val = creaturesModel.getObject().get(p.getObject());
				final PropertyModel<Object> o = new PropertyModel<Object>(val, displayExpression);
				Object v = o.getObject();
				return v == null ? null : v.toString();
			}
		}));
	}

	@Override
	public Component getFilter(String componentId, final FilterForm<?> form) {
		return new TextFilter<String>(componentId, filterModel, form);
	}
}