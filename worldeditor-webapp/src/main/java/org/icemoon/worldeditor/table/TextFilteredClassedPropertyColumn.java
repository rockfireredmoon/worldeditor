package org.icemoon.worldeditor.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@SuppressWarnings("serial")
public class TextFilteredClassedPropertyColumn<T, F> extends TextFilteredPropertyColumn<T, F, String> {

	private String cssClass;
	private Class<F> clazz;

	public TextFilteredClassedPropertyColumn(IModel<String> displayModel, String propertyExpression, String cssClass) {
		super(displayModel, propertyExpression);
		this.cssClass = cssClass;
	}

	public TextFilteredClassedPropertyColumn(Class<F> clazz, IModel<String> displayModel, String propertyExpression, String cssClass) {
		super(displayModel, propertyExpression);
		this.cssClass = cssClass;
		this.clazz = clazz;
	}

	public TextFilteredClassedPropertyColumn(IModel<String> displayModel, String sortProperty, String propertyExpression, String cssClass) {
		super(displayModel, sortProperty, propertyExpression);
		this.cssClass = cssClass;
	}
	public TextFilteredClassedPropertyColumn(Class<F> clazz, IModel<String> displayModel, String sortProperty, String propertyExpression, String cssClass) {
		super(displayModel, sortProperty, propertyExpression);
		this.cssClass = cssClass;
		this.clazz = clazz;
	}

	public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> model) {
		cellItem.add(new AttributeModifier("class", cssClass));
		super.populateItem(cellItem, componentId, model);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getFilter(String componentId, FilterForm<?> form) {
		TextFilter<T> f = (TextFilter<T>)super.getFilter(componentId, form);
		if(clazz != null) {
			// For some reason wicket can't work out the ID class on it's own
			f.getFilter().setType(clazz);
			f.getFilter().add(new AttributeAppender("class", new Model<String>(clazz + "Filter"), " "));
		}
		return f;
	}
	
}
