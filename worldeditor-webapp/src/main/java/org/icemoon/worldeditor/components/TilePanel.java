package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Tile;

@SuppressWarnings("serial")
public class TilePanel extends FormComponentPanel<Tile> {

	private TextField<Integer> xText;
	private TextField<Integer> yText;
	private int x, y;

	public TilePanel(final String id) {
		super(id);
		setType(Tile.class);
		xText = new TextField<Integer>("x", new PropertyModel<Integer>(this, "x"), Integer.class);
		xText.add(new RangeValidator<Integer>(0, 999));
		add(xText);
		yText = new TextField<Integer>("y", new PropertyModel<Integer>(this, "y"), Integer.class);
		yText.add(new RangeValidator<Integer>(0, 999));
		add(yText);
	}

	@Override
	protected void convertInput() {
		setConvertedInput(new Tile(xText.getConvertedInput(), yText.getConvertedInput()));
	}

	@Override
	protected void onBeforeRender() {
		Tile total = getModelObject();
		x = total == null ? 0 : total.getX();
		y = total == null ? 0 : total.getY();
		xText.clearInput();
		yText.clearInput();
		super.onBeforeRender();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(TilePanel.class, "TilePanel.css")));
	}
}