package org.icemoon.worldeditor.dialogs;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.IRoot;
import org.icemoon.eartheternal.common.Location;
import org.icemoon.eartheternal.common.Scenery;
import org.icemoon.worldeditor.components.LocationViewPanel;
import org.odlabs.wiquery.ui.dialog.Dialog;

@SuppressWarnings("serial")
public abstract class ChooseLocationDialog<R extends IRoot> extends Panel {

	private Dialog chooseLocationDialog;
	private IModel<String> titleModel;
	private IModel<String> textModel;
	private Label label;
	private IModel<? extends List<Scenery<R>>> mapPoints;
	private Form<Object> form;
	private WebMarkupContainer spawnsContainer;

	public ChooseLocationDialog(String id, IModel<String> titleModel, IModel<String> textModel,
			IModel<? extends List<Scenery<R>>> mapPoints) {
		super(id);
		this.titleModel = titleModel;
		this.textModel = textModel;
		this.mapPoints = mapPoints;
	}

	public ChooseLocationDialog(String id, IModel<Scenery<R>> model, IModel<String> titleModel, IModel<String> textModel,
			IModel<? extends List<Scenery<R>>> mapPoints) {
		super(id, model);
		this.titleModel = titleModel;
		this.textModel = textModel;
		this.mapPoints = mapPoints;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem
			.forReference(new CssResourceReference(ChooseLocationDialog.class, "ChooseLocationDialog.css")));
	}

	protected abstract void onChoose(Scenery<R> object, AjaxRequestTarget target);

	@Override
	protected void onInitialize() {
		final IModel<Scenery<R>> model = (IModel<Scenery<R>>) getDefaultModel();
		form = new Form<Object>("chooseLocationDialogForm");
		label = new Label("chooseLocationDialogText", textModel);
		label.setOutputMarkupId(true);
		label.setEscapeModelStrings(false);
		form.add(label);
		form.add(new AjaxButton("chooseLocationDialogClose") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				chooseLocationDialog.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));

		spawnsContainer = new WebMarkupContainer("spawnsContainer");
		spawnsContainer.setOutputMarkupId(true);
		ListView<Scenery<R>> spawns = new ListView<Scenery<R>>("spawns", mapPoints) {
			@Override
			protected void populateItem(final ListItem<Scenery<R>> item) {
				final AjaxLink<String> ajaxLink = new AjaxLink<String>("useSpawn") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						onChoose(item.getModelObject(), target);
						chooseLocationDialog.close(target);
					}
				};
				item.add(ajaxLink);
				ajaxLink.add(new LocationViewPanel("spawn", new Model<Location>(item.getModelObject().getLocation())));
			}
		};
		spawns.setReuseItems(false);
		spawnsContainer.add(spawns);
		form.add(spawnsContainer);

		chooseLocationDialog = new Dialog("chooseLocationDialog");
		chooseLocationDialog.setTitle(titleModel);
		chooseLocationDialog.add(form);
		chooseLocationDialog.setWidth(400);

		add(chooseLocationDialog);

		super.onInitialize();
	}

	public void open(AjaxRequestTarget target) {
		target.add(spawnsContainer);
		target.add(label);
		chooseLocationDialog.open(target);
	}
}
