package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Size;
import org.icemoon.eartheternal.common.XY;

@SuppressWarnings("serial")
public class PageSizePanel extends FormComponentPanel<Size> {

	private TextField<Double> xText;
	private TextField<Double> yText;

	public PageSizePanel(final String id) {
		super(id);
		setType(XY.class);
		xText = new TextField<Double>("x", new PropertyModel<Double>(this, "modelObject.x"), Double.class);
		xText.add(new RangeValidator<Double>(0.0, Double.MAX_VALUE));
		add(xText);
		yText = new TextField<Double>("y", new PropertyModel<Double>(this, "modelObject.y"), Double.class);
		yText.add(new RangeValidator<Double>(0.0, Double.MAX_VALUE));
		add(yText);
	}

	@Override
	protected void convertInput() {
		setConvertedInput(new Size(xText.getConvertedInput(), yText.getConvertedInput()));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(PageSizePanel.class, "PageSizePanel.css")));
	}
}