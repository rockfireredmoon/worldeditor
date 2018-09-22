package org.icemoon.worldeditor.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class ClassedPropertyColumn<T> extends PropertyColumn<T, String> {

	private String cssClass;

	public ClassedPropertyColumn(IModel<String> displayModel, String propertyExpression, String cssClass) {
		super(displayModel, propertyExpression);
		this.cssClass = cssClass;
	}

	public ClassedPropertyColumn(IModel<String> displayModel, String sortProperty, String propertyExpression, String cssClass) {
		super(displayModel, sortProperty, propertyExpression);
		this.cssClass = cssClass;
	}

	public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> model) {
		cellItem.add(new AttributeModifier("class", cssClass));
		super.populateItem(cellItem, componentId, model);
	}
}
