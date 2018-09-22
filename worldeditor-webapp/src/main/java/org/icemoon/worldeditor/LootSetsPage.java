package org.icemoon.worldeditor;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GameItems;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.LootRarity;
import org.icemoon.eartheternal.common.LootSet;
import org.icemoon.eartheternal.common.LootSets;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.components.SelectionBuilder;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class LootSetsPage extends AbstractEntityPage<LootSet, String, String, LootSets, IDatabase> {
	private boolean autoComment = true;

	public LootSetsPage() {
		super("entityId", String.class);
	}

	@Override
	protected void additionalValidation() {
		if (autoComment) {
			IDatabase database = Application.getAppSession(getRequestCycle()).getDatabase();
			StringBuilder bui = new StringBuilder();
			for (Long it : getSelected().getItems()) {
				if (bui.length() > 0)
					bui.append(", ");
				bui.append(it);
				bui.append(":");
				GameItem item = database.getItems().get(it);
				if (item != null) {
					bui.append(item.getDisplayName());
				} else
					bui.append("Unknown");
			}
			getSelected().setComment(bui.toString());
		}
	}

	@Override
	protected void buildForm(final Form<LootSet> form) {
		form.add(new DropDownChoice<LootRarity>("rarity", Arrays.asList(LootRarity.values())).add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(getComponent().getParent().get("rate"));
			}
		}));
		form.add(new CheckBox("autoComment", new PropertyModel<Boolean>(this, "autoComment")) {
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
		});
		form.add(new TextArea<String>("comment") {
			@Override
			public boolean isEnabled() {
				return !autoComment;
			}
		});
		form.add(new TextField<Integer>("rate") {
			@Override
			public boolean isEnabled() {
				return getSelected().getRarity() != null && getSelected().getRarity().isRarityUsed();
			}
		}.setOutputMarkupId(true));
		form.add(new SelectionBuilder<Long, GameItem, String, GameItems, Long, IDatabase>("items", Long.class, GameItem.class,
				ItemsPage.class, new Model<GameItems>() {
					@Override
					public GameItems getObject() {
						return Application.getAppSession(getRequestCycle()).getDatabase().getItems();
					}
				}, "displayName"));
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(LootSetsPage.class, "LootSetsPage.css")));
		super.onRenderEntityHead(response);
	}

	@Override
	protected LootSet createNewInstance() {
		return new LootSet(getDatabase());
	}

	@Override
	public LootSets getEntityDatabase() {
		return getDatabase().getLootSets();
	}

	@Override
	protected void buildColumns(List<IColumn<LootSet, String>> columns) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		IModel<List<? extends LootRarity>> listModel = new ListModel(Arrays.asList(LootRarity.values()));
		columns.add(new ChoiceFilteredPropertyColumn<LootSet, LootRarity, String>(new ResourceModel("column.rarity"), "rarity",
				"rarity", listModel) {
			protected IChoiceRenderer<LootRarity> getChoiceRenderer() {
				return new IChoiceRenderer<LootRarity>() {
					@Override
					public Object getDisplayValue(LootRarity object) {
						return String.valueOf(object.name().charAt(0));
					}

					@Override
					public String getIdValue(LootRarity object, int index) {
						return object.name();
					}
				};
			}

			public void populateItem(Item<ICellPopulator<LootSet>> cellItem, String componentId, IModel<LootSet> model) {
				LootRarity rarity = model.getObject().getRarity();
				if (rarity != null) {
					cellItem.add(new AttributeModifier("class", "lootRarity lootRarity_" + rarity.name().toLowerCase()));
				}
				Label label = new Label(componentId, new Model<String>(Util.toEnglish(rarity, true)));
				cellItem.add(label);
			}
		});
		columns.add(
				new TextFilteredClassedPropertyColumn<LootSet, String>(new ResourceModel("column.rate"), "rate", "rate", "rate"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void select(LootSet selected, int selectedIndex) {
		super.select(selected, selectedIndex);
		if (form != null)
			((SelectionBuilder) form.get("items")).resetItemEdit();
	}
}
