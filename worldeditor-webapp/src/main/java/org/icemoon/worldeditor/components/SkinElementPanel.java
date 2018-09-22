package org.icemoon.worldeditor.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.RGB;
import org.icemoon.eartheternal.common.Appearance.SkinElement;
import org.odlabs.wiquery.ui.core.CoreUIJavaScriptResourceReference;

@SuppressWarnings("serial")
public class SkinElementPanel extends Panel {

	public SkinElementPanel(final String id, IModel<SkinElement> model) {
		super(id, model);
	}

	public SkinElementPanel(final String id) {
		super(id);
	}

	protected void onRemoveItem(AjaxRequestTarget target) {
	}

	@Override
	public void onInitialize() {
		super.onInitialize();
		setOutputMarkupId(true);
		add(new TextField<String>("name", new PropertyModel<String>(this, "defaultModel.object.name")));
		add(new ColorField("colour", new PropertyModel<RGB>(this, "defaultModelObject.color")));
		add(new AjaxButton("remove") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				onRemoveItem(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

		}.setDefaultFormProcessing(false));
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(CoreUIJavaScriptResourceReference.get()));
	}
}