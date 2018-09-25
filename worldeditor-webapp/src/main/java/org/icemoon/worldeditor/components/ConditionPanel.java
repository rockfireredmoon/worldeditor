package org.icemoon.worldeditor.components;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Condition;
import org.icemoon.eartheternal.common.Util;

@SuppressWarnings("serial")
public abstract class ConditionPanel<T extends Condition> extends FormComponentPanel<T> {

	private Class<? extends T> condition;
	private Component panelComponent;

	public ConditionPanel(final String id, IModel<T> propertyModel) {
		super(id, propertyModel);
		add(new DropDownChoice<Class<T>>("condition", new PropertyModel<Class<T>>(this, "condition"),
				new PropertyModel<List<Class<T>>>(this, "conditions")) {
			@Override
			protected void onSelectionChanged(Class<T> newSelection) {
				getParent().remove(panelComponent);
				getParent().add(panelComponent = createPanel());
				super.onSelectionChanged(newSelection);
			}

			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}

		}.setChoiceRenderer(new IChoiceRenderer<Class<T>>() {
			@Override
			public Object getDisplayValue(Class<T> object) {
				return Util.decamel(object.getSimpleName());
			}

			@Override
			public String getIdValue(Class<T> object, int index) {
				return object.toString();
			}
		}));
		add(panelComponent = createPanel());
	}

	public Class<? extends T> getCondition() {
		return condition;
	}

	public void setCondition(Class<? extends T> condition) {
		this.condition = condition;
	}

	public abstract List<Class<? extends T>> getConditions();

	protected abstract AbstractConditionDetailsPanel<? extends T> createPanel();

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(
				CssHeaderItem.forReference(new CssResourceReference(ConditionPanel.class, "ConditionPanel.css")));
	}

	@Override
	protected void onBeforeRender() {
		if (getModelObject() == null)
			this.condition = null;
		else
			this.condition = (Class<? extends T>) getModelObject().getClass();
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
//		setConvertedInput(convertedInput);
		// long coin = (10000l * goldText.getConvertedInput()) + (100l *
		// silverText.getConvertedInput())
		// + copperText.getConvertedInput();
		// setConvertedInput(Long.valueOf(coin));
		super.convertInput();
	}
}