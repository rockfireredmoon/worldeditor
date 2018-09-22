package org.icemoon.worldeditor.dialogs;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.icemoon.eartheternal.common.Appearance;
import org.icemoon.eartheternal.common.Color;
import org.icemoon.eartheternal.common.ItemAppearance;
import org.icemoon.eartheternal.common.RGB;
import org.icemoon.eartheternal.common.Appearance.ClothingType;
import org.icemoon.worldeditor.components.InternalForm;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.odlabs.wiquery.ui.tabs.Tabs;

@SuppressWarnings("serial")
public class ItemAppearanceEditorDialog extends FormComponentPanel<ItemAppearance> {
	private Form<Appearance> appearanceForm;
	private Dialog dialog;
	private FeedbackPanel feedback;
	private Tabs tabs;
	public String appearanceString;

	public ItemAppearanceEditorDialog(final String id, IModel<ItemAppearance> model) {
		super(id, model);
		setType(Appearance.class);
		setOutputMarkupId(true);
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		response.render(CssHeaderItem
				.forReference(new PackageResourceReference(ItemAppearanceEditorDialog.class, "ItemAppearanceEditorDialog.css")));
		super.renderHead(response);
	}

	public void onInitialize() {
		super.onInitialize();
		// Dialog
		dialog = new Dialog("dialog");
		dialog.setTitle(new Model<String>("Item Appearance Editor"));
		dialog.setWidth(880);
		dialog.setHeight(560);
		// Form
		appearanceForm = new InternalForm<Appearance>("itemAppearanceForm");
		appearanceForm.setOutputMarkupId(true);
		appearanceForm.add(new AjaxButton("saveButton") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				appearanceSaved(target);
				dialog.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}
		});
		dialog.add(appearanceForm);
		//
		appearanceForm.add(feedback = new FeedbackPanel("itemAppearanceFeedback"));
		feedback.setOutputMarkupId(true);
		// Tabs
		tabs = new Tabs("tabs");
		appearanceForm.add(tabs);
		// <div wicket:id="rawContainer">
		// <textarea wicket:id="raw" rows="24" cols="802">
		// </textarea>
		// <input wicket:id="apply" type="submit" value="Apply" />
		// </div>
		// <div wicket:id="rawContainer">
		// <textarea wicket:id="raw" rows="24" cols="802">
		// </textarea>
		// <input wicket:id="apply" type="submit" value="Apply" />
		// </div>
		WebMarkupContainer rawContainer = new WebMarkupContainer("rawContainer");
		TextArea<String> raw = new TextArea<String>("raw", new PropertyModel<String>(this, "appearanceString"));
		Button apply = new AjaxButton("apply") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// try {
				// } catch (ParseException e) {
				// error("Failed to parse appearance.");
				// target.add(feedback);
				// }
			}
		};
		rawContainer.add(apply);
		rawContainer.add(raw);
		tabs.add(rawContainer);
		// Selector
		add(dialog);
	}

	public String getAppearanceString() {
		final ItemAppearance app = getModelObject();
		return app == null ? "" : app.toString();
	}

	public void setAppearanceString(String str) {
		try {
			setModelObject(new ItemAppearance(str));
		} catch (Exception e) {
			error("Failed to parse appearance string. " + e.getMessage());
		}
	}

	protected void appearanceSaved(AjaxRequestTarget target) {
	}

	@SuppressWarnings("unchecked")
	public void open(AjaxRequestTarget target) {
		final ItemAppearance act = getModelObject();
		// appearanceForm.clearInput();
		formChanged(target);
		dialog.open(target);
	}

	protected void formChanged(AjaxRequestTarget target) {
		target.add(appearanceForm);
	}

	@Override
	protected void convertInput() {
		final ItemAppearance modelObject = getModelObject();
		// if (modelObject != null) {
		// modelObject.setSkinElements(skinElements);
		// modelObject.setClothing(clothing);
		// }
		super.convertInput();
		setConvertedInput(modelObject);
	}

	class NewClothes implements Serializable {
		private ClothingType newType;
	}

	class NewSkin implements Serializable {
		private String name = "";
		private RGB colour = new Color(0, 0, 0);

		public String toString() {
			return name + " " + colour;
		}
	}
}