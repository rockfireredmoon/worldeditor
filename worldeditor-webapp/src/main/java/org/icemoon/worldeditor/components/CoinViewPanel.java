package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;

@SuppressWarnings("serial")
public class CoinViewPanel extends Panel {

	private int gold, silver, copper;

	public CoinViewPanel(final String id, IModel<Long> coins) {
		super(id, coins);
		add(new Label("gold", new PropertyModel<Integer>(this, "gold")));
		add(new Label("silver", new PropertyModel<Integer>(this, "silver")));
		add(new Label("copper", new PropertyModel<Integer>(this, "copper")));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(CoinViewPanel.class, "CoinViewPanel.css")));
	}

	@Override
	protected void onBeforeRender() {
		Long total = (Long) getDefaultModelObject();
		if (total == null) {
			total = 0l;
		}
		gold = (int) (total / 10000);
		silver = (int) ((total - (gold * 10000)) / 100);
		copper = (int) ((total - ((gold * 10000) + (silver * 100))));
		super.onBeforeRender();
	}
}