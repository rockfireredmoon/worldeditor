package org.icemoon.worldeditor.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.ClothingItem;

@SuppressWarnings("serial")
public class ClothingItemPanel extends AbstractAttachmentPanel<ClothingItem> {

	public ClothingItemPanel(final String id, IModel<ClothingItem> model) {
		super(id, model);
	}

	public ClothingItemPanel(final String id) {
		super(id);
	}

	@Override
	public void onInitialize() {
		super.onInitialize();
		add(new Label("type", new PropertyModel<String>(this, "modelObject.type")));
		add(new AjaxButton("copy") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				onCopyItem(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

		});
	}

	protected void onCopyItem(AjaxRequestTarget target) {
	}

	@Override
	protected void convertInput() {
		final String asset = autocomplete.getConvertedInput();
		setConvertedInput(new ClothingItem(getModelObject().getType(), asset, null, colours));
	}
}