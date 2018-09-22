package org.icemoon.worldeditor;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.EssenceShop;
import org.icemoon.eartheternal.common.EssenceShopItem;
import org.icemoon.eartheternal.common.EssenceShops;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GameItems;
import org.icemoon.eartheternal.common.Hint;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.SpawnKey;
import org.icemoon.worldeditor.components.SelectionBuilder;
import org.icemoon.worldeditor.model.GameItemsModel;

@SuppressWarnings("serial")
public class EssenceShopsPage extends AbstractShopsPage<EssenceShop, EssenceShopItem, EssenceShops> {
	protected static final class EssenceShopItemSelectionBuilder
			extends SelectionBuilder<Long, GameItem, String, GameItems, EssenceShopItem, IDatabase> {
		private String comment;
		private Integer tokens;

		protected EssenceShopItemSelectionBuilder(String id, Class<Long> keyClass, Class<GameItem> entityClass,
				Class<? extends Page> listPage, IModel<GameItems> db, String displayNameExpression) {
			super(id, keyClass, entityClass, listPage, db, displayNameExpression);
		}

		@Override
		protected void onEdit(AjaxRequestTarget target, EssenceShopItem newItem) {
			comment = newItem.getComment();
			tokens = newItem.getTokens();
		}

		@Override
		public EssenceShopItem convert(Long key) {
			EssenceShopItem essenceShopItem = new EssenceShopItem(key, comment);
			essenceShopItem.setTokens(tokens == null ? 0 : tokens);
			return essenceShopItem;
		}

		@Override
		public Long deconvert(EssenceShopItem key) {
			// comment = key.getComment();
			return key.getId();
		}

		protected void buildForm(Form<?> newItemForm) {
			super.buildForm(newItemForm);
			newItemForm.add(new TextField<String>("comment", new PropertyModel<String>(this, "comment"), String.class)
					.setOutputMarkupId(true));
			newItemForm.add(new TextField<Integer>("tokens", new PropertyModel<Integer>(this, "tokens"), Integer.class)
					.setRequired(false).setOutputMarkupId(true));
		}

		@Override
		protected void onSelection(AjaxRequestTarget target, GameItem entity) {
			if (StringUtils.isBlank(comment)) {
				comment = entity.getDisplayName();
				target.add(get("selectionBuilder").get("newItemForm").get("comment"));
				target.add(get("selectionBuilder").get("newItemForm").get("tokens"));
			}
		}

		@Override
		public void resetItemEdit() {
			super.resetItemEdit();
			comment = null;
		}
	}

	@Override
	protected void buildForm(Form<EssenceShop> form) {
		super.buildForm(form);
		form.add(new TextArea<String>("comment"));
		form.add(new SelectorPanel<Long, GameItem, String, IDatabase>("essence", new Model<String>("Essence"),
				new GameItemsModel(this), "displayName", GameItem.class, Long.class, ItemsPage.class).setShowLabel(true)
						.setShowClear(true));
	}

	@Override
	protected void additionalValidation() {
		Creature c = Application.getAppSession(getRequestCycle()).getDatabase().getCreatures()
				.get(getSelected().getEntityId().getCreature());
		if (!c.getHints().contains(Hint.VENDOR)) {
			form.error(String.format("Selected creature '%s' (%d) must be a 'Vendor'", c.getDisplayName(), c.getEntityId()));
		}
	}

	@Override
	protected EssenceShop createNewInstance() {
		EssenceShop shop = new EssenceShop(getDatabase());
		SpawnKey id = new SpawnKey();
		EssenceShop selected = getSelected();
		if (selected != null && selected.getEntityId() != null)
			id.setZone(selected.getEntityId().getZone());
		shop.setEntityId(id);
		return shop;
	}

	@Override
	public EssenceShops getEntityDatabase() {
		return getDatabase().getEssenceShops();
	}

	@Override
	protected SelectionBuilder<?, ?, ?, ?, ?, ?> createSelectionBuilder() {
		return new EssenceShopItemSelectionBuilder("items", Long.class, GameItem.class, ItemsPage.class, new Model<GameItems>() {
			@Override
			public GameItems getObject() {
				return Application.getAppSession(getRequestCycle()).getDatabase().getItems();
			}
		}, "displayName");
	}
}
