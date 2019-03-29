package org.icemoon.worldeditor.components;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.ItemQuality;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.CraftDef.ItemType;
import org.icemoon.eartheternal.common.CraftDef.Quality;

public class ItemQualityPanel extends AbstractConditionDetailsPanel<Quality> {

	private ItemQuality itemQuality;

	public ItemQualityPanel(String id, IModel<Quality> propertyModel) {
		super(id, propertyModel);
		add(new DropDownChoice<ItemQuality>("itemQuality", new PropertyModel<ItemQuality>(propertyModel, "itemQuality"),
				Arrays.asList(ItemQuality.values())));
	}

	@Override
	protected void onBeforeRender() {
		itemQuality  = getModelObject().getQuality();
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		setConvertedInput(new Quality(itemQuality));
		super.convertInput();
	}

}
