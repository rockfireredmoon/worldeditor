package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.CraftDef;
import org.icemoon.eartheternal.common.CraftDefs;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.CraftDef.AbstractCraftCondition;
import org.icemoon.eartheternal.common.CraftDef.ItemType;
import org.icemoon.eartheternal.common.CraftDef.Quality;
import org.icemoon.eartheternal.common.CraftDef.RequireID;
import org.icemoon.eartheternal.common.CraftDef.RequireIDMult;
import org.icemoon.eartheternal.common.CraftDef.RequireIDXMult;
import org.icemoon.worldeditor.components.AbstractConditionDetailsPanel;
import org.icemoon.worldeditor.components.ConditionPanel;
import org.icemoon.worldeditor.components.ItemBuilder;
import org.icemoon.worldeditor.components.ItemQualityPanel;
import org.icemoon.worldeditor.components.ItemTypePanel;
import org.icemoon.worldeditor.components.RequireIDMultPanel;
import org.icemoon.worldeditor.components.RequireIDPanel;
import org.icemoon.worldeditor.components.RequireIDXMultPanel;

@SuppressWarnings("serial")
public class CraftDefsPage extends AbstractEntityPage<CraftDef, String, String, CraftDefs, IDatabase> {
	private static final class ConditionBuilder extends ItemBuilder<AbstractCraftCondition> {
		private AbstractCraftCondition condition;
		private IModel<IDatabase> database;

		private ConditionBuilder(String id, IModel<IDatabase> database) {
			super(id);
			this.database = database;
		}

		@Override
		protected AbstractCraftCondition getItem() {
			return new CraftDef.RequireID();
		}

		@Override
		protected void onEdit(AjaxRequestTarget target, AbstractCraftCondition newItem) {
			condition = newItem;
		}

		@Override
		protected void buildForm(Form<AbstractCraftCondition> newItemForm) {
			super.buildForm(newItemForm);
			newItemForm.add(new ConditionPanel<AbstractCraftCondition>("condition",
					new PropertyModel<AbstractCraftCondition>(this, "item")) {
				@Override
				public List<Class<? extends AbstractCraftCondition>> getConditions() {
					return new ArrayList<Class<? extends AbstractCraftCondition>>(
							CraftDef.getSupportedConditions().values());
				}

				@Override
				protected AbstractConditionDetailsPanel<? extends AbstractCraftCondition> createPanel() {
					if (Quality.class.equals(getCondition())) {
						condition = new Quality();
						return new ItemQualityPanel("conditionPanel",
								new PropertyModel<Quality>(ConditionBuilder.this, "condition"));
					} else if (RequireID.class.equals(getCondition())) {
						condition = new RequireID();
						return new RequireIDPanel("conditionPanel",
								new PropertyModel<RequireID>(ConditionBuilder.this, "condition"), database);
					} else if (RequireIDMult.class.equals(getCondition())) {
						condition = new RequireIDMult();
						return new RequireIDMultPanel("conditionPanel",
								new PropertyModel<RequireIDMult>(ConditionBuilder.this, "condition"), database);
					} else if (RequireIDXMult.class.equals(getCondition())) {
						condition = new RequireIDXMult();
						return new RequireIDXMultPanel("conditionPanel",
								new PropertyModel<RequireIDXMult>(ConditionBuilder.this, "condition"), database);
					} else  {
						condition = new ItemType();
						return new ItemTypePanel("conditionPanel",
								new PropertyModel<ItemType>(ConditionBuilder.this, "condition"));
					}
				}
			}.setRequired(true).setOutputMarkupId(true));
		}
	}

	public CraftDefsPage() {
		super("entityId", String.class);
	}

	@Override
	protected void buildForm(Form<CraftDef> form) {
		form.add(new TextArea<String>("comment"));
		form.add(new ConditionBuilder("conditions", new PropertyModel<>(this, "database")));
	}

	protected void buildColumns(List<IColumn<CraftDef, String>> columns) {
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(CraftDefsPage.class, "CraftDefsPage.css")));
	}

	@Override
	protected CraftDef createNewInstance() {
		return new CraftDef(getDatabase());
	}

	@Override
	public CraftDefs getEntityDatabase() {
		return getDatabase().getCraftDefs();
	}
}
