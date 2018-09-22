package org.icemoon.worldeditor;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.odlabs.wiquery.ui.autocomplete.AutocompleteComponent;

@SuppressWarnings("serial")
public abstract class FixedAutocompleteComponent<T> extends AutocompleteComponent<T> {
	public FixedAutocompleteComponent(String id, IModel<T> model, final IModel<? extends List<? extends T>> list) {
		super(id, model, list);
		setChoiceRenderer(new IChoiceRenderer<T>() {
			@Override
			public Object getDisplayValue(T object) {
				return String.valueOf(object);
			}

			@Override
			public String getIdValue(T object, int index) {
				return String.valueOf(object);
			}
		});
	}

	public FixedAutocompleteComponent(String id, IModel<T> model, final IModel<? extends List<? extends T>> list,
			IChoiceRenderer<T> choiceRenderer) {
		super(id, model, list, choiceRenderer);
	}
}
