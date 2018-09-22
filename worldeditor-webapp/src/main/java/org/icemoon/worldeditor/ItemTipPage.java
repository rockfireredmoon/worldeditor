package org.icemoon.worldeditor;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameCharacters;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.worldeditor.components.ItemTipPanel;
import org.icemoon.worldeditor.player.AbstractUserPage;

@SuppressWarnings("serial")
public class ItemTipPage extends AbstractUserPage {
	@Override
	protected void onInitialize() {
		super.onInitialize();
		setDefaultModel(new Model<GameItem>(getDatabase().getItems().get(getPageParameters().get("id").toLongObject())));
		add(new ItemTipPanel("tipItem", getModel()));
		ListView<GameCharacter> equipped = new ListView<GameCharacter>("equippedItems",
				new PropertyModel<List<GameCharacter>>(this, "characters")) {
			@Override
			protected void populateItem(ListItem<GameCharacter> item) {
				item.add(new Label("characterName", new PropertyModel<String>(item.getModel(), "displayName")));
				item.add(new ItemTipPanel("equippedItem", getEquippedModel(item.getModel())));
			}
		};
		add(equipped);
	}

	public List<GameCharacter> getCharacters() {
		PageParameters p = getPageParameters();
		final GameCharacters characters = Application.getApp().getUserData().getCharacters();
		if (!p.get("c").isNull()) {
			return Arrays.asList(characters.get(p.get("c").toLongObject()));
		}
		final AppSession appSession = Application.getAppSession(getRequestCycle());
		if (appSession.isAdmin()) {
			return Collections.emptyList();
		}
		List<GameCharacter> l = new ArrayList<GameCharacter>();
		final Principal user = appSession.getUser();
		if (user instanceof Account) {
			for (Long c : ((Account) user).getCharacters()) {
				l.add(characters.get(c));
			}
		}
		return l;
	}

	public IModel<GameItem> getModel() {
		return (IModel<GameItem>) getDefaultModel();
	}

	public GameItem getModelObject() {
		return getModel().getObject();
	}

	private IModel<GameItem> getEquippedModel(final IModel<GameCharacter> model) {
		return new Model<GameItem>() {
			public GameItem getObject() {
				Long eq = model.getObject().getEquipment().get(getModelObject().getEquipType().toSlot());
				if (eq == null) {
					return null;
				}
				return getDatabase().getItems().get(eq);
			}
		};
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(ItemTipPage.class, "ItemTipPage.css")));
	}
}
