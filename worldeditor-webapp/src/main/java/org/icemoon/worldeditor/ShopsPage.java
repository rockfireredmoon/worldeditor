package org.icemoon.worldeditor;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GameItems;
import org.icemoon.eartheternal.common.Hint;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Shop;
import org.icemoon.eartheternal.common.ShopItem;
import org.icemoon.eartheternal.common.Shops;
import org.icemoon.eartheternal.common.SpawnKey;
import org.icemoon.worldeditor.components.SelectionBuilder;

@SuppressWarnings("serial")
public class ShopsPage extends AbstractShopsPage<Shop, ShopItem, Shops> {
	protected static final class ShopItemSelectionBuilder extends SelectionBuilder<Long, GameItem, String, GameItems, ShopItem, IDatabase> {
		private String comment;

		protected ShopItemSelectionBuilder(String id, Class<Long> keyClass, Class<GameItem> entityClass,
				Class<? extends Page> listPage, IModel<GameItems> db, String displayNameExpression) {
			super(id, keyClass, entityClass, listPage, db, displayNameExpression);
		}

		@Override
		protected void onEdit(AjaxRequestTarget target, ShopItem newItem) {
			comment = newItem.getComment();
		}

		@Override
		public ShopItem convert(Long key) {
			return new ShopItem(key, comment);
		}

		@Override
		public Long deconvert(ShopItem key) {
			// comment = key.getComment();
			return key.getId();
		}

		protected void buildForm(Form<?> newItemForm) {
			super.buildForm(newItemForm);
			newItemForm.add(new TextField<String>("comment", new PropertyModel<String>(this, "comment")).setOutputMarkupId(true));
		}

		@Override
		protected void onSelection(AjaxRequestTarget target, GameItem entity) {
			if (StringUtils.isBlank(comment)) {
				comment = entity.getDisplayName();
				target.add(get("newItemForm").get("comment"));
			}
		}

		@Override
		public void resetItemEdit() {
			super.resetItemEdit();
			comment = null;
		}
	}

	@Override
	protected void additionalValidation() {
		Creature c = Application.getAppSession(getRequestCycle()).getDatabase().getCreatures()
				.get(getSelected().getEntityId().getCreature());
		if (!c.getHints().contains(Hint.COPPER_SHOPKEEPER)) {
			form.error(String.format("Selected creature '%s' (%d) must be a 'Copper Shop keeper'", c.getDisplayName(),
					c.getEntityId()));
		}
	}

	@Override
	protected Shop createNewInstance() {
		Shop shop = new Shop(getDatabase());
		SpawnKey id = new SpawnKey();
		Shop selected = getSelected();
		if (selected != null && selected.getEntityId() != null)
			id.setZone(selected.getEntityId().getZone());
		shop.setEntityId(id);
		return shop;
	}

	@Override
	public Shops getEntityDatabase() {
		return getDatabase().getShops();
	}

	@Override
	protected SelectionBuilder<?, ?, ?, ?, ?, ?> createSelectionBuilder() {
		return new ShopItemSelectionBuilder("items", Long.class, GameItem.class, ItemsPage.class, new Model<GameItems>() {
			@Override
			public GameItems getObject() {
				return Application.getAppSession(getRequestCycle()).getDatabase().getItems();
			}
		}, "displayName");
	}
}
