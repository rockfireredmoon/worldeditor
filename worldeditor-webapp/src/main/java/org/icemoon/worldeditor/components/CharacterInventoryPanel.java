package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.model.GameItemQtipModel;
import org.icemoon.worldeditor.model.InventoryModel;
import org.odlabs.wiquery.ui.core.CoreUIJavaScriptResourceReference;

@SuppressWarnings("serial")
public class CharacterInventoryPanel extends Panel {
	final static String DEFAULT_ICON = "Icon-32-Icon_Holder2.png";

	public CharacterInventoryPanel(final String id, IModel<? extends GameCharacter> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		final ListView<Long> inventory = new ListView<Long>("inventoryItem",
				new InventoryModel(new PropertyModel<GameCharacter>(this, "character"),
						new PropertyModel<IDatabase>(this, "database"))) {
			@Override
			protected void populateItem(final ListItem<Long> item) {
				final GameItem gameItem = item.getModelObject() == 0 ? null
						: Application.getAppSession(getRequestCycle()).getDatabase().getItems().get(item.getModelObject());
				final Model<String> model1 = new Model<String>(
						gameItem == null || gameItem.getIcon1() == null || gameItem.getIcon1().equals("") ? DEFAULT_ICON
								: gameItem.getIcon1());
				final Model<String> model2 = new Model<String>(
						gameItem == null || gameItem.getIcon2() == null || gameItem.getIcon2().equals("") ? DEFAULT_ICON
								: gameItem.getIcon2());
				GameIconPanel iconPanel = new GameIconPanel("icon", model1, model2, 32);
				Link<String> link = new Link<String>("inventoryItemLink") {
					@Override
					public void onClick() {
					}
				};
				link.add(iconPanel);
				item.add(link);
				final GameItemQtipModel qtipModel = new GameItemQtipModel(new Model<GameItem>(gameItem));
				link.setMarkupId(qtipModel.getItemId());
				Label script = new Label("inventoryItemScript", qtipModel);
				script.setEscapeModelStrings(false);
				item.add(script);
			}
		};
		inventory.setReuseItems(false);
		add(inventory);
		super.onInitialize();
	}

	public GameCharacter getCharacter() {
		return (GameCharacter) getDefaultModelObject();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(
				CssHeaderItem.forReference(new CssResourceReference(CharacterInventoryPanel.class, "CharacterInventoryPanel.css")));
		response.render(JavaScriptHeaderItem.forReference(CoreUIJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Application.class, "jquery.qtip.js")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "jquery.qtip.css")));
	}
}