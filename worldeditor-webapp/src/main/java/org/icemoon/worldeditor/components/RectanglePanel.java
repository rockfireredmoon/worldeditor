package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Rectangle;

@SuppressWarnings("serial")
public class RectanglePanel extends FormComponentPanel<Rectangle> {

	private TextField<Long> topLeftXText;
	private TextField<Long> topLeftYText;
	private TextField<Long> bottomRightXText;
	private TextField<Long> bottomRightYText;

	public RectanglePanel(final String id) {
		super(id);
		setType(Rectangle.class);
		topLeftXText = new TextField<Long>("topLeftX", new PropertyModel<Long>(this, "modelObject.topLeft.x"), Long.class);
		topLeftXText.add(new RangeValidator<Long>(Long.MIN_VALUE, Long.MAX_VALUE));
		add(topLeftXText);
		topLeftYText = new TextField<Long>("topLeftY", new PropertyModel<Long>(this, "modelObject.topLeft.y"), Long.class);
		topLeftYText.add(new RangeValidator<Long>(Long.MIN_VALUE, Long.MAX_VALUE));
		add(topLeftYText);
		bottomRightXText = new TextField<Long>("bottomRightX", new PropertyModel<Long>(this, "modelObject.bottomRight.x"),
			Long.class);
		bottomRightXText.add(new RangeValidator<Long>(Long.MIN_VALUE, Long.MAX_VALUE));
		add(bottomRightXText);
		bottomRightYText = new TextField<Long>("bottomRightY", new PropertyModel<Long>(this, "modelObject.bottomRight.y"),
			Long.class);
		bottomRightYText.add(new RangeValidator<Long>(Long.MIN_VALUE, Long.MAX_VALUE));
		add(bottomRightYText);
	}

	@Override
	protected void convertInput() {
		setConvertedInput(new Rectangle(topLeftXText.getConvertedInput(), topLeftYText.getConvertedInput(),
			bottomRightXText.getConvertedInput(), bottomRightYText.getConvertedInput()));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(RectanglePanel.class, "RectanglePanel.css")));
	}

}