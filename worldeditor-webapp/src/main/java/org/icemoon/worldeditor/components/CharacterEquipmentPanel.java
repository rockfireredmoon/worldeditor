package org.icemoon.worldeditor.components;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.AttributeModifier;
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
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.Slot;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.model.EquipmentModel;
import org.icemoon.worldeditor.model.GameItemQtipModel;
import org.odlabs.wiquery.ui.core.CoreUIJavaScriptResourceReference;

@SuppressWarnings("serial")
public class CharacterEquipmentPanel extends Panel {
	final static String DEFAULT_ICON = "Icon-32-Icon_Holder2.png";
	private boolean bags;

	public CharacterEquipmentPanel(final String id, IModel<? extends GameCharacter> model, boolean bags) {
		super(id, model);
		this.bags = bags;
	}

	@Override
	protected void onInitialize() {
		final ListView<Map.Entry<Slot, Long>> equipment = new ListView<Map.Entry<Slot, Long>>("equipmentItem",
				new EquipmentModel(new PropertyModel<GameCharacter>(this, "character"), bags)) {
			@Override
			protected void populateItem(final ListItem<Map.Entry<Slot, Long>> item) {
				final Entry<Slot, Long> modelObject = item.getModelObject();
				item.add(new AttributeModifier("class", "bodypart " + modelObject.getKey().name().toLowerCase()));
				GameItem gameItem = modelObject.getValue() == 0 ? null
						: Application.getAppSession(getRequestCycle()).getDatabase().getItems().get(modelObject.getValue());
				final Model<String> model1 = new Model<String>(
						gameItem == null || gameItem.getIcon1() == null || gameItem.getIcon1().equals("") ? null
								: gameItem.getIcon1());
				final Model<String> model2 = new Model<String>(
						gameItem == null || gameItem.getIcon2() == null || gameItem.getIcon2().equals("") ? null
								: gameItem.getIcon2());
				GameIconPanel iconPanel = new GameIconPanel("icon", model1, model2, 32);
				Link<String> link = new Link<String>("equipmentItemLink") {
					@Override
					public void onClick() {
					}
				};
				link.add(iconPanel);
				item.add(link);
				final GameItemQtipModel qtipModel = new GameItemQtipModel(new Model<GameItem>(gameItem));
				link.setMarkupId(qtipModel.getItemId());
				Label script = new Label("equipmentItemScript", qtipModel);
				script.setEscapeModelStrings(false);
				item.add(script);
			}
		};
		equipment.setReuseItems(false);
		add(equipment);
		super.onInitialize();
	}

	public GameCharacter getCharacter() {
		return (GameCharacter) getDefaultModelObject();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(
				CssHeaderItem.forReference(new CssResourceReference(CharacterEquipmentPanel.class, "CharacterEquipmentPanel.css")));
		response.render(JavaScriptHeaderItem.forReference(CoreUIJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem.forReference(new CssResourceReference(Application.class, "jquery.qtip.js")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(Application.class, "jquery.qtip.css")));
	}
}