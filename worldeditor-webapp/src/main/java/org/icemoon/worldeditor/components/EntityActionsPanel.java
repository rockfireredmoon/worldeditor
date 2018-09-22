package org.icemoon.worldeditor.components;

import java.io.OutputStream;
import java.io.StringWriter;
import java.text.MessageFormat;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.AbstractINIFileEntity;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.INIWriter;
import org.icemoon.eartheternal.common.Util;
import org.odlabs.wiquery.ui.dialog.Dialog;

@SuppressWarnings("serial")
public class EntityActionsPanel<T extends Entity<?>> extends Panel {

	public static class RawModel<T extends Entity<?>> implements IModel<String> {

		private IModel<T> model;

		public RawModel(IModel<T> model) {
			this.model = model;
		}

		@Override
		public void detach() {
		}

		@Override
		public String getObject() {
			Entity<?> e = model.getObject();
			if (e instanceof AbstractINIFileEntity) {
				StringWriter s = new StringWriter();
				((AbstractINIFileEntity<?,?>) e).write(new INIWriter(s));
				return s.toString();
			}
			return "";
		}

		@Override
		public void setObject(String object) {
		}

	}

	private TextArea<String> textArea;

	public EntityActionsPanel(final IModel<T> model) {
		super("entityActions");

		final Dialog dialog = new Dialog("rawDialog");
		dialog.setWidth(630);
		dialog.setModal(true);
		dialog.setTitle("Raw Data Editor");
		dialog.add(textArea = new TextArea<String>("rawText", new RawModel<T>(model)));
		textArea.setOutputMarkupId(true);
		dialog.add(new AjaxButton("saveRaw") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				dialog.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setEnabled(false));

		Form<?> form = new Form<Object>("entityActionsForm");
		form.add(new Label("entity", new Model<String>() {
			@Override
			public String getObject() {
				return MessageFormat.format(getPage().getString("entity"), Util.decamel(getPage().getClass().getSimpleName().replaceFirst("Page$", "")), true);
			}
		}));
		form.add(new Button("newEntity") {
			@Override
			public void onSubmit() {
				onNew();
			}
		});

		class ExportResource extends AbstractResource {

			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
				ResourceResponse resourceResponse = new ResourceResponse();
				resourceResponse.setContentType("text/plain");
				resourceResponse.setContentDisposition(ContentDisposition.ATTACHMENT);
				resourceResponse.setFileName("export.dat");
				resourceResponse.setWriteCallback(new WriteCallback() {
					@Override
					public void writeData(Attributes attributes) {
						onExportList(attributes.getResponse().getOutputStream());
					}
				});
				return resourceResponse ;
			};
		}

		form.add(new ResourceLink<Object>("exportList", new ExportResource()));
		form.add(new AjaxButton("raw") {

			public boolean isEnabled() {
				return model.getObject() != null;
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.add(textArea);
				dialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		form.add(dialog);
		addAdditionalActions(form);

		add(form);
	}

	protected void addAdditionalActions(Form<?> form) {

	}

	protected void onNew() {
	}

	protected void onExportList(OutputStream outputStream) {
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(EntityActionsPanel.class, "EntityActionsPanel.css")));
	}
}
