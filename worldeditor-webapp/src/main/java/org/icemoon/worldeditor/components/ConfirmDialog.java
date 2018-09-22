package org.icemoon.worldeditor.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Entity;
import org.odlabs.wiquery.ui.dialog.Dialog;

@SuppressWarnings("serial")
public abstract class ConfirmDialog<T> extends Panel {

	private Dialog deleteDialog;
	private IModel<String> titleModel;
	private IModel<String> textModel;
	private Label label;

	public ConfirmDialog(String id, IModel<String> titleModel, IModel<String> textModel) {
		super(id);
		this.titleModel = titleModel;
		this.textModel = textModel;
	}

	public ConfirmDialog(String id, IModel<T> model, IModel<String> titleModel, IModel<String> textModel) {
		super(id, model);
		this.titleModel = titleModel;
		this.textModel = textModel;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(ConfirmDialog.class, "ConfirmDialog.css")));
	}

	protected abstract void onConfirm(T object, AjaxRequestTarget target);

	@Override
	protected void onInitialize() {
		final IModel<T> model = (IModel<T>) getDefaultModel();
		Form<?> form = new Form<Object>("confirmDialogForm");
		label = new Label("confirmDialogText", textModel);
		label.setOutputMarkupId(true);
		label.setEscapeModelStrings(false);
		form.add(label);
		form.add(new AjaxButton("confirmDialogYes") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				onConfirm(model.getObject(), target);
				deleteDialog.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		form.add(new AjaxButton("confirmDialogNo") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				deleteDialog.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));

		deleteDialog = new Dialog("confirmDialog");
		deleteDialog.setTitle(titleModel);
		deleteDialog.add(form);
		
		add(deleteDialog);
		
		super.onInitialize();
	}

	public final IModel<String> getTitleModel() {
		return titleModel;
	}

	public final IModel<String> getTextModel() {
		return textModel;
	}

	public void open(AjaxRequestTarget target) {
		target.add(label);
		deleteDialog.open(target);
	}
}
