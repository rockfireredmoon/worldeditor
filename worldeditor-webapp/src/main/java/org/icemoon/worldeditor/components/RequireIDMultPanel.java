package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.CraftDef.RequireID;
import org.icemoon.eartheternal.common.CraftDef.RequireIDMult;
import org.icemoon.worldeditor.ItemsPage;
import org.icemoon.worldeditor.SelectorPanel;
import org.icemoon.worldeditor.model.GameItemsModel;

public class RequireIDMultPanel extends AbstractConditionDetailsPanel<RequireIDMult> {

	private long itemId;
	private int itemCount;

	public RequireIDMultPanel(String id, IModel<RequireIDMult> propertyModel, IModel<? extends IDatabase> database) {
		super(id, propertyModel);
		add(new SelectorPanel<Long, GameItem, String, IDatabase>("itemId", new Model<String>("Required Item"),
				new GameItemsModel(database), "displayName", new PropertyModel<Long>(this, "itemId"), GameItem.class,
				Long.class, ItemsPage.class).setOutputMarkupId(true));
		add(new TextField<Integer>("itemCount", Integer.class).setRequired(true)
				.setLabel(new Model<String>("Item Count")).add(new RangeValidator<Integer>(1, (int) Short.MAX_VALUE)));
	}

	@Override
	protected void onBeforeRender() {
		itemId = getModelObject().getItemId();
		itemCount = getModelObject().getItemCount();
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		setConvertedInput(new RequireIDMult(itemId, itemCount));
		super.convertInput();
	}

}
