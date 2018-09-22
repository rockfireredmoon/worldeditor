package org.icemoon.worldeditor.treetable;

import javax.swing.tree.TreeNode;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.PropertyRenderableColumn;

@SuppressWarnings("serial")
public class ClassedPropertyColumn<T> extends PropertyRenderableColumn<T> {

	private String cssClass;

	public ClassedPropertyColumn(ColumnLocation loc, String header, String propertyExpression, String cssClass) {
		super(loc, header, propertyExpression);
		this.cssClass = cssClass;
	}

	@Override
	public Component newCell(MarkupContainer parent, String id, TreeNode node, int level) {
		Component c = super.newCell(parent, id, node, level);
		c.add(new AttributeModifier("class", cssClass));
		return c;
	}
}
