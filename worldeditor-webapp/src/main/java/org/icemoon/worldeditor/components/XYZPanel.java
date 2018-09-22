package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.XYZ;

@SuppressWarnings("serial")
public class XYZPanel extends FormComponentPanel<XYZ> {

	private TextField<Long> xText;
	private TextField<Long> yText;
	private TextField<Long> zText;
	private Long x, y, z;

	public XYZPanel(final String id) {
		super(id);
	}

	public XYZPanel(final String id, IModel<XYZ> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		setType(XYZ.class);
		super.onInitialize();
		xText = new TextField<Long>("x", new PropertyModel<Long>(this, "x"), Long.class);
		add(xText);
		yText = new TextField<Long>("y", new PropertyModel<Long>(this, "y"), Long.class);
		add(yText);
		zText = new TextField<Long>("z", new PropertyModel<Long>(this, "z"), Long.class);
		add(zText);
	}

	@Override
	protected void onBeforeRender() {
		XYZ total = getModelObject();
		x = total == null ? 0 : total.getX();
		y = total == null ? 0 : total.getY();
		z = total == null ? 0 : total.getZ();
		xText.clearInput();
		yText.clearInput();
		zText.clearInput();
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {

		final Long nx = xText.getConvertedInput();
		final Long ny = yText.getConvertedInput();
		final Long nz = zText.getConvertedInput();
		XYZ loc = getConvertedInput();
		if(loc == null) {
			loc = new XYZ(0, 0, 0);
		}
		loc.setX(nx == null ? 0 : nx);
		loc.setY(ny == null ? 0 : ny);
		loc.setZ(nz == null ? 0 : nz);
		setConvertedInput(loc);
	}
}