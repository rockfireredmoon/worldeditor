package org.icemoon.worldeditor.components;

import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Condition;
import org.icemoon.eartheternal.common.Util;

@SuppressWarnings("serial")
public abstract class AbstractConditionDetailsPanel<T extends Condition> extends FormComponentPanel<T> {

	private Class<T> condition;

	public AbstractConditionDetailsPanel(final String id, IModel<T> propertyModel) {
		super(id, propertyModel);
	}

	@Override
	protected void onBeforeRender() {
		// Long total = getModelObject();
		// if(total == null)
		// total = 0l;
		// gold = (int) (total / 10000);
		// silver = (int) ((total - (gold * 10000)) / 100);
		// copper = (int) ((total - ((gold * 10000) + (silver * 100))));
		// goldText.setConvertedInput(gold);
		// silverText.setConvertedInput(silver);
		// copperText.setConvertedInput(copper);
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		// long coin = (10000l * goldText.getConvertedInput()) + (100l *
		// silverText.getConvertedInput())
		// + copperText.getConvertedInput();
		// setConvertedInput(Long.valueOf(coin));
		super.convertInput();
	}
}