package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.CraftDef;
import org.icemoon.eartheternal.common.CraftDefs;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.CraftDef.AbstractCraftCondition;
import org.icemoon.worldeditor.components.ConditionPanel;
import org.icemoon.worldeditor.components.ItemBuilder;

@SuppressWarnings("serial")
public class CraftDefsPage extends AbstractEntityPage<CraftDef, String, String, CraftDefs, IDatabase> {
	private static final class ConditionBuilder extends ItemBuilder<AbstractCraftCondition> {
		private ConditionBuilder(String id) {
			super(id);
		}

		@Override
		protected AbstractCraftCondition getItem() {
			return new CraftDef.RequireID();
		}

		@Override
		protected void onEdit(AjaxRequestTarget target, AbstractCraftCondition newItem) {
			// comment = newItem.getComment();
		}

		@Override
		protected void buildForm(Form<AbstractCraftCondition> newItemForm) {
			super.buildForm(newItemForm);
			newItemForm.add(new ConditionPanel<AbstractCraftCondition>("condition", new PropertyModel<AbstractCraftCondition>(this, "item")) {
				@Override
				public List<Class<? extends AbstractCraftCondition>> getConditions() {
					return new ArrayList<Class<? extends AbstractCraftCondition>>(CraftDef.getSupportedConditions().values());
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
		form.add(new ConditionBuilder("conditions"));
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
