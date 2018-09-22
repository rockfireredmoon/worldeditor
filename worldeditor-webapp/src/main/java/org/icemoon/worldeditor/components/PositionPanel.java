package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Location;
import org.icemoon.eartheternal.common.Position;

@SuppressWarnings("serial")
public class PositionPanel extends FormComponentPanel<Position> {
	private TextField<Double> xText;
	private TextField<Double> yText;
	private TextField<Double> zText;
	private Double x, y, z;
	private boolean allowClear;

	public PositionPanel(final String id) {
		super(id);
	}

	public PositionPanel(final String id, IModel<Position> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);
		setType(Location.class);
		// Location - dependent on instance
		xText = new TextField<Double>("x", new PropertyModel<Double>(this, "x"), Double.class);
		xText.setOutputMarkupId(true);
		xText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(xText);
		yText = new TextField<Double>("y", new PropertyModel<Double>(this, "y"), Double.class);
		yText.setOutputMarkupId(true);
		yText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(yText);
		zText = new TextField<Double>("z", new PropertyModel<Double>(this, "z"), Double.class);
		zText.setOutputMarkupId(true);
		zText.add(new RangeValidator<Double>(0d, Double.MAX_VALUE));
		add(zText);
		super.onInitialize();
	}

	@Override
	protected void onBeforeRender() {
		if (!hasErrorMessage()) {
			Position total = getModelObject();
			x = total == null ? 0 : total.getX();
			y = total == null ? 0 : total.getY();
			z = total == null ? 0 : total.getZ();
			xText.clearInput();
			yText.clearInput();
			zText.clearInput();
		}
		super.onBeforeRender();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(PositionPanel.class, "PositionPanel.css")));
	}

	@Override
	protected void convertInput() {
		final Double nx = xText.getConvertedInput();
		final Double ny = yText.getConvertedInput();
		final Double nz = zText.getConvertedInput();
		Position loc = getConvertedInput();
		if (loc == null) {
			loc = new Position(0, 0, 0);
		}
		loc.setX(nx == null ? 0 : nx);
		loc.setY(ny == null ? 0 : ny);
		loc.setZ(nz == null ? 0 : nz);
		setConvertedInput(loc);
	}

	public void setAllowClear(boolean allowClear) {
		this.allowClear = allowClear;
	}
}