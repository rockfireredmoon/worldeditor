package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.GameItem;

@SuppressWarnings("serial")
public class ItemTipPanel extends Panel {

	public ItemTipPanel(final String id, IModel<GameItem> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		add(new Label("displayName", new PropertyModel<String>(this, "modelObject.displayName")));
		super.onInitialize();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(ItemTipPanel.class, "ItemTipPanel.css")));
	}

	public IModel<GameItem> getModel() {
		return (IModel<GameItem>) getDefaultModel();
	}

	public GameItem getModelObject() {
		return getModel().getObject();
	}
}