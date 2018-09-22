package org.icemoon.worldeditor;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Location;

@SuppressWarnings("serial")
public class AppearancePanel extends FormComponentPanel<Location> {

	private TextField<Double> xText;
	private TextField<Double> yText;
	private TextField<Double> zText;
	private TextField<Integer> directionText;

	public AppearancePanel(final String id) {
		super(id);
		xText = new TextField<Double>("x", new PropertyModel<Double>(this, "modelObject.x"));
		xText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(xText);
		yText = new TextField<Double>("y", new PropertyModel<Double>(this, "modelObject.y"));
		yText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(yText);
		zText = new TextField<Double>("z", new PropertyModel<Double>(this, "modelObject.z"));
		zText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(zText);
		directionText = new TextField<Integer>("direction", new PropertyModel<Integer>(this, "modelObject.direction"));
		directionText.add(new RangeValidator<Integer>(0, 360));
		add(directionText);
	}
}