package org.icemoon.worldeditor.components;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.AttachmentItem;
import org.icemoon.eartheternal.common.EquipType;

@SuppressWarnings("serial")
public class AttachmentItemPanel extends AbstractAttachmentPanel<AttachmentItem> {
	
	private boolean showNode;
	private String node;

	public AttachmentItemPanel(final String id, IModel<AttachmentItem> model) {
		this(id, model, false);
	}
	public AttachmentItemPanel(final String id, IModel<AttachmentItem> model, boolean showNode) {
		super(id, model);
		this.showNode = showNode;
	}

	public AttachmentItemPanel(final String id) {
		super(id);
	}

	@Override
	public void onInitialize() {
		super.onInitialize();
		add(new TextField<String>("node", new PropertyModel<String>(this, "node"), String.class).setVisible(showNode));
	}

	@Override
	protected void onBeforeRender() {
		final AttachmentItem  modelObject = getModelObject();
		node = modelObject == null ? null : modelObject.getNode();
		super.onBeforeRender();
	}
	@Override
	protected void convertInput() {
		final String asset = autocomplete.getConvertedInput();
		setConvertedInput(new AttachmentItem(null, asset, null, colours, node));
	}
}