package org.icemoon.worldeditor.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.icemoon.eartheternal.common.AttachmentItem;
import org.icemoon.eartheternal.common.ClothingItem;
import org.icemoon.eartheternal.common.Color;
import org.icemoon.eartheternal.common.RGB;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.FixedAutocompleteComponent;
import org.odlabs.wiquery.ui.core.CoreUIJavaScriptResourceReference;

@SuppressWarnings("serial")
public abstract class AbstractAttachmentPanel<T extends AttachmentItem> extends FormComponentPanel<T> {

	protected List<RGB> colours = new ArrayList<RGB>();
	protected String assetName;
	protected FixedAutocompleteComponent<String> autocomplete;
	private AjaxButton removeButton;
	private AjaxButton addColourButton;

	public AbstractAttachmentPanel(final String id, IModel<T> model) {
		super(id, model);
		addColourButton = new AjaxButton("addColour") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				colours.add(new Color(255, 255, 255));
				target.add(AbstractAttachmentPanel.this);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

		};
		removeButton = new AjaxButton("remove") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				onRemoveItem(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

		};
	}

	public AbstractAttachmentPanel(final String id) {
		super(id);
	}

	protected void onRemoveItem(AjaxRequestTarget target) {
	}

	public List<RGB> getColoursInput() {
		return colours;
	}

	public String getAssetInput() {
		return autocomplete.getConvertedInput();
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(CoreUIJavaScriptResourceReference.get()));
	}

	@Override
	public void onInitialize() {
		super.onInitialize();
		setType(ClothingItem.class);
		setOutputMarkupId(true);

		try {
			FileListModel fileListModel = new FileListModel(Application.getAppSession(getRequestCycle()).getDatabase()
					.getServerDirectory().resolveFile("asset")
				.resolveFile("Release").resolveFile("Media").getName().getURI());
			autocomplete = new FixedAutocompleteComponent<String>("asset", new PropertyModel<String>(this, "assetName"), fileListModel) {
				@Override
				public String getValueOnSearchFail(String input) {
					return input;
				}
			};
			autocomplete.setChoiceRenderer( new ChoiceRenderer<String>());

			add(autocomplete);
			add(new ListView<RGB>("colours", new PropertyModel<List<RGB>>(this, "colours")) {
				@Override
				protected void populateItem(final ListItem<RGB> item) {
					item.add(new ColorField("colour", item.getModel()));
				}
			});
			add(removeButton);
			add(addColourButton);
		} catch (FileSystemException fse) {
			throw new RuntimeException(fse);
		}
	}

	public void setAllowRemove(boolean allowRemove) {
		removeButton.setVisible(allowRemove);
	}

	public void setAllowAddColour(boolean allowAddColour) {
		addColourButton.setVisible(allowAddColour);
	}

	@Override
	protected void onBeforeRender() {
		final T modelObject = getModelObject();
		colours = modelObject == null ? new ArrayList<RGB>() : modelObject.getColors();
		assetName = modelObject == null ? null : modelObject.getAsset();
		autocomplete.clearInput();
		super.onBeforeRender();
	}
}