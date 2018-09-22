package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;

@SuppressWarnings("serial")
public class CoinPanel extends FormComponentPanel<Long> {

	private TextField<Integer> goldText;
	private TextField<Integer> silverText;
	private TextField<Integer> copperText;
	private int gold, silver, copper;

	public CoinPanel(final String id) {
		super(id);
		goldText = new TextField<Integer>("gold", new PropertyModel<Integer>(this, "gold"), Integer.class);
		goldText.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE));
		add(goldText);
		silverText = new TextField<Integer>("silver", new PropertyModel<Integer>(this, "silver"), Integer.class);
		silverText.add(new RangeValidator<Integer>(0, 99));
		add(silverText);
		copperText = new TextField<Integer>("copper", new PropertyModel<Integer>(this, "copper"), Integer.class);
		copperText.add(new RangeValidator<Integer>(0, 99));
		add(copperText);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(CoinPanel.class, "CoinViewPanel.css")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(CoinPanel.class, "CoinPanel.css")));
	}

	@Override
	protected void onBeforeRender() {
		Long total = getModelObject();
		if(total == null)
			total = 0l;
		gold = (int) (total / 10000);
		silver = (int) ((total - (gold * 10000)) / 100);
		copper = (int) ((total - ((gold * 10000) + (silver * 100))));
		goldText.setConvertedInput(gold);
		silverText.setConvertedInput(silver);
		copperText.setConvertedInput(copper);
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		long coin = (10000l * goldText.getConvertedInput()) + (100l * silverText.getConvertedInput())
			+ copperText.getConvertedInput();
		setConvertedInput(Long.valueOf(coin));
	}
}