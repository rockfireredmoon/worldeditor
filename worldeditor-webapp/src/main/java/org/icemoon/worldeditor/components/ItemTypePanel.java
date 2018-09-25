package org.icemoon.worldeditor.components;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.CraftDef.ItemType;
import org.icemoon.eartheternal.common.CraftDef.RequireID;
import org.icemoon.eartheternal.common.GameItem;

public class ItemTypePanel extends AbstractConditionDetailsPanel<ItemType> {
	
	private GameItem.Type itemType;

	public ItemTypePanel(String id, IModel<ItemType> propertyModel) {
		super(id, propertyModel);
		add(new DropDownChoice<GameItem.Type>("itemType", new PropertyModel<GameItem.Type>(this, "itemType"),
				Arrays.asList(GameItem.Type.values())));
	}

	@Override
	protected void onBeforeRender() {
		itemType = getModelObject().getItemType();
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		setConvertedInput(new ItemType(itemType));
		super.convertInput();
	}
}
