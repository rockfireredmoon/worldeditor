package org.icemoon.worldeditor.components;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.icemoon.eartheternal.common.Color;
import org.icemoon.eartheternal.common.RGB;
import org.icemoon.eartheternal.common.Util;
import org.odlabs.wiquery.ui.core.CoreUIJavaScriptResourceReference;
import org.odlabs.wiquery.ui.position.PositionJavaScriptResourceReference;

@SuppressWarnings("serial")
public class ColorField extends FormComponentPanel<RGB> {

	private TextField<String> xText;
	private WebMarkupContainer icon;
	private String rgb;
	private Label pickerFragment;

	public ColorField(final String id) {
		super(id);
	}

	public ColorField(final String id, IModel<RGB> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		setType(RGB.class);
		super.onInitialize();
		xText = new TextField<String>("rgb", new PropertyModel<String>(this, "rgb"), String.class);
		xText.add(new AjaxFormComponentUpdatingBehavior("onkeyup") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				try {
					rgb = Util.toHexString(new Color(xText.getConvertedInput()));
				} catch (Exception e) {
				}
				final String javascript = "$('#" + icon.getMarkupId() + "').css('background-color','" + rgb + "');";
				target.appendJavaScript(javascript);
				target.appendJavaScript("hideColorFieldPicker('#" + xText.getMarkupId() + "');");
				System.out.println("Javascript: " + javascript);
			}
		});
		xText.setOutputMarkupId(true);
		add(xText);

		pickerFragment = new Label("pickerFragment") {
			public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
				final String string = markupStream.get().toString();
				replaceComponentTagBody(markupStream, openTag,
					string.replace("rgbField", xText.getMarkupId()).replace("rgbPicker", icon.getMarkupId()));
			}
		};
		pickerFragment.setEscapeModelStrings(false);
		add(pickerFragment);
		icon = new WebMarkupContainer("picker");
		icon.setOutputMarkupId(true);
		icon.add(new AjaxEventBehavior("onclick") {
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.appendJavaScript("showColorFieldPicker('#" + xText.getMarkupId() + "');");
			}
		});
		add(icon);
	}

	@Override
	protected void onBeforeRender() {
		RGB total = getModelObject();
		rgb = total == null ? null : Util.toHexString(total);
		xText.clearInput();
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		String rgbIn = xText.getConvertedInput();
		setConvertedInput(new Color(rgbIn));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(ColorField.class, "colorpicker.css")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(ColorField.class, "ColorField.css")));
		response.render(JavaScriptHeaderItem.forReference(CoreUIJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem.forReference(PositionJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ColorField.class, "colorpicker.js")));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ColorField.class, "eye.js")));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ColorField.class, "utils.js")));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ColorField.class, "ColorField.js")));
	}
}