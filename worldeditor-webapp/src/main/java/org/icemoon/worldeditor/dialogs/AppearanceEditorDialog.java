package org.icemoon.worldeditor.dialogs;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.ThrottlingSettings;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Appearance;
import org.icemoon.eartheternal.common.AttachmentItem;
import org.icemoon.eartheternal.common.ClothingItem;
import org.icemoon.eartheternal.common.Color;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.ItemAppearance;
import org.icemoon.eartheternal.common.RGB;
import org.icemoon.eartheternal.common.Appearance.Body;
import org.icemoon.eartheternal.common.Appearance.ClothingType;
import org.icemoon.eartheternal.common.Appearance.Gender;
import org.icemoon.eartheternal.common.Appearance.Head;
import org.icemoon.eartheternal.common.Appearance.Name;
import org.icemoon.eartheternal.common.Appearance.Race;
import org.icemoon.eartheternal.common.Appearance.SkinElement;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.UnoptimizedDeepCopy;
import org.icemoon.worldeditor.components.AttachmentItemPanel;
import org.icemoon.worldeditor.components.ClothingItemPanel;
import org.icemoon.worldeditor.components.ColorField;
import org.icemoon.worldeditor.components.InternalForm;
import org.icemoon.worldeditor.components.SkinElementPanel;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.odlabs.wiquery.ui.tabs.Tabs;

@SuppressWarnings("serial")
public class AppearanceEditorDialog extends FormComponentPanel<Appearance> {
	private ListView<SkinElement> skinView;
	private NewSkin newSkin = new NewSkin();
	private NewClothes newClothes = new NewClothes();
	private ListView<ClothingItem> clothingView;
	private Dialog dialog;
	private List<SkinElement> skinElements = new ArrayList<SkinElement>();
	private List<ClothingItem> clothing = new ArrayList<ClothingItem>();
	private List<AttachmentItem> attachments = new ArrayList<AttachmentItem>();
	private List<ClothingType> availableTypes = new ArrayList<ClothingType>();
	private FeedbackPanel feedback;
	private Tabs tabs;
	private WebMarkupContainer clothingContainer;
	private DropDownChoice<ClothingType> newTypeField;
	private AjaxButton addNewButton;
	private AjaxButton addNewAttachmentButton;
	private ListView<AttachmentItem> attachmentsView;
	private WebMarkupContainer attachmentsContainer;
	private AttachmentItem newAttachmentItem;
	private TextArea<String> raw;
	private WebMarkupContainer skinContainer;
	private WebMarkupContainer rawContainer;
	private WebMarkupContainer appearanceContainer;
	private Name typeName;
	private Gender gender;
	private Race race;
	private Body body;
	private Head head;
	private double size;
	private String asset;
	private ListChoice<Name> nameChoice;
	private boolean saveAppearance;
	{
		setType(Appearance.class);
		setOutputMarkupId(true);
	}

	public AppearanceEditorDialog(final String id) {
		super(id);
	}

	public AppearanceEditorDialog(final String id, IModel<Appearance> model) {
		super(id, model);
	}

	public void reset() {
		saveAppearance = false;	
		typeName = null;
		gender = null;
		race = null;
		body = null;
		head = null;
		asset = null;
		size = 0;
		skinElements.clear();
		clothing.clear();
		attachments.clear();
	}

	public String getRaw() {
		Appearance modelObject = new Appearance();
		setToAppearance(modelObject);
		return modelObject.toPrettyString();
	}

	@Override
	public void onInitialize() {
		super.onInitialize();
		// Dialog
		dialog = new Dialog("dialog");
		dialog.setTitle(new Model<String>("Appearance Editor"));
		dialog.setWidth(880);
		dialog.setHeight(560);
		// Form
		InternalForm<?> appearanceForm = new InternalForm<Appearance>("appearanceForm");
		appearanceContainer = new WebMarkupContainer("appearanceContainer");
		appearanceContainer.setOutputMarkupId(true);
		appearanceForm.add(new AjaxButton("saveButton") {
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				final Appearance objective = AppearanceEditorDialog.this.getModelObject();
				if (objective != null) {
					objective.setClothing(clothing);
					objective.setAttachments(attachments);
					objective.setSkinElements(skinElements);
				}
				appearanceSaved(target);
				dialog.close(target);
			}
		});
		appearanceForm.add(appearanceContainer);
		dialog.add(appearanceForm);
		//
		appearanceForm.add(feedback = new FeedbackPanel("appearanceFeedback"));
		feedback.setOutputMarkupId(true);
		// Gender
		appearanceContainer.add(
				new DropDownChoice<Gender>("gender", new PropertyModel<Gender>(this, "gender"), Arrays.asList(Gender.values())) {
					@Override
					public boolean isVisible() {
						return AppearanceEditorDialog.this.getModelObject() != null && Name.C2.equals(typeName);
					}
				}.setNullValid(false).add(createRawUpdateBehavior()));
		appearanceContainer
				.add(new DropDownChoice<Race>("race", new PropertyModel<Race>(this, "race"), Arrays.asList(Race.values())) {
					@Override
					public boolean isVisible() {
						return AppearanceEditorDialog.this.getModelObject() != null && Name.C2.equals(typeName);
					}
				}.setNullValid(false).add(createRawUpdateBehavior()));
		appearanceContainer
				.add(new DropDownChoice<Body>("body", new PropertyModel<Body>(this, "body"), Arrays.asList(Body.values())) {
					@Override
					public boolean isVisible() {
						return AppearanceEditorDialog.this.getModelObject() != null && Name.C2.equals(typeName);
					}
				}.setNullValid(true).add(createRawUpdateBehavior()));
		appearanceContainer
				.add(new DropDownChoice<Head>("head", new PropertyModel<Head>(this, "head"), Arrays.asList(Head.values())) {
					@Override
					public boolean isVisible() {
						return AppearanceEditorDialog.this.getModelObject() != null && Name.C2.equals(typeName);
					}
				}.setNullValid(false).add(createRawUpdateBehavior()));
		appearanceContainer.add(new TextField<Double>("size", new PropertyModel<Double>(this, "size"), Double.class)
				.add(new RangeValidator<Double>(0d, 10d)).add(createSizeKeyEventBehavior()));
		appearanceContainer.add(new TextField<String>("asset", new PropertyModel<String>(this, "asset"), String.class) {
			@Override
			public boolean isVisible() {
				return AppearanceEditorDialog.this.getModelObject() != null && !Name.C2.equals(typeName);
			}
		}.add(createTemplateKeyEventBehavior()));
		// Name - Dont know what this is - name is not the right, erm, name
		// either
		nameChoice = new ListChoice<Name>("name", new PropertyModel<Name>(this, "typeName"), Arrays.asList(Name.values()),
				new IChoiceRenderer<Name>() {
					@Override
					public Object getDisplayValue(Name object) {
						return getLocalizer().getString("name." + object, nameChoice);
					}

					@Override
					public String getIdValue(Name object, int index) {
						return object.name();
					}
				});
		nameChoice.setNullValid(true);
		nameChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(appearanceContainer);
				target.add(tabs);
			}
		});
		appearanceContainer.add(nameChoice.setMaxRows(1));
		// Tabs
		tabs = new Tabs("tabs");
		tabs.setOutputMarkupId(true);
		// <li wicket:id="clothingTabHeader"><a href="#tab1">Clothing</a></li>
		tabs.add(new WebMarkupContainer("clothingTabHeader") {
			@Override
			public boolean isVisible() {
				return Name.C2.equals(typeName);
			}
		});
		tabs.add(new WebMarkupContainer("attachmentsTabHeader") {
			@Override
			public boolean isVisible() {
				return !Name.P1.equals(typeName);
			}
		});
		tabs.add(new WebMarkupContainer("skinTabHeader") {
			@Override
			public boolean isVisible() {
				return !Name.P1.equals(typeName);
			}
		});
		appearanceForm.add(tabs);
		addClothing();
		addAttachments();
		addSkin();
		addRaw();
		// Selector
		Button button = new AjaxButton("choose") {
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				open(target);
			}
		};
		button.setDefaultFormProcessing(false);
		add(button);
		add(dialog);
		setupElements();
	}

	public void open(AjaxRequestTarget target) {
		saveAppearance = true;
		target.add(appearanceContainer);
		target.add(tabs);
		setupElements();
		// appearanceForm.clearInput();
		rebuildAvailableTypes();
		formChanged(target);
		dialog.open(target);
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		response.render(
				CssHeaderItem.forReference(new CssResourceReference(AppearanceEditorDialog.class, "AppearanceEditorDialog.css")));
		super.renderHead(response);
	}

	public void setRaw(String raw) throws ParseException {
		Appearance app = new Appearance(raw);
		setFromAppearance(app);
	}

	@SuppressWarnings("unchecked")
	protected void setFromAppearance(Appearance app) {
		typeName = app.getName();
		gender = app.getGender();
		race = app.getRace();
		body = app.getBody();
		head = app.getHead();
		asset = app.getAsset();
		size = app.getSize();
		skinElements.clear();
		skinElements = (List<SkinElement>) UnoptimizedDeepCopy.copy(app.getSkinElements());
		clothing = (List<ClothingItem>) UnoptimizedDeepCopy.copy(app.getClothing());
		attachments = (List<AttachmentItem>) UnoptimizedDeepCopy.copy(app.getAttachments());
	}

	protected void addAttachments() {
		// Add new item panel
		Form<?> newAttachments = new InternalForm<AttachmentItem>("newAttachmentForm") {
			@Override
			public boolean isVisible() {
				return !Name.P1.equals(typeName);
			}
		};
		tabs.add(newAttachments);
		final AttachmentItemPanel attachmentItemPanel = new AttachmentItemPanel("newAttachmentItem",
				new PropertyModel<AttachmentItem>(this, "newAttachmentItem"));
		attachmentItemPanel.setAllowAddColour(false);
		attachmentItemPanel.setAllowRemove(false);
		newAttachments.add(attachmentItemPanel);
		addNewAttachmentButton = new AjaxButton("addNewAttachment") {
			@Override
			public boolean isEnabled() {
				return availableTypes.size() > 0;
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// newClothingForm.process(this);
				addNewAttachment(target);
			}
		};
		addNewAttachmentButton.setOutputMarkupId(true);
		newAttachments.add(addNewAttachmentButton);
		// Clothes
		attachmentsContainer = new WebMarkupContainer("attachmentsContainer") {
			@Override
			public boolean isVisible() {
				return !Name.P1.equals(typeName);
			}
		};
		attachmentsContainer.setOutputMarkupId(true);
		tabs.add(attachmentsContainer);
		attachmentsView = new ListView<AttachmentItem>("attachment", new PropertyModel<List<AttachmentItem>>(this, "attachments")) {
			@Override
			protected void populateItem(final ListItem<AttachmentItem> item) {
				item.add(new AttachmentItemPanel("attachmentItem", item.getModel(), true) {
					@Override
					protected void onRemoveItem(AjaxRequestTarget target) {
						super.onRemoveItem(target);
						final AttachmentItem modelObject = item.getModelObject();
						modelObject.setAsset(getAssetInput());
						modelObject.setColors(getColoursInput());
						removeAttachment(target, modelObject);
					}
				});
			}
		};
		attachmentsView.setReuseItems(true);
		attachmentsContainer.add(attachmentsView);
	}

	protected void addClothing() {
		InternalForm<NewClothes> newClothingForm = new InternalForm<NewClothes>("newClothingForm",
				new CompoundPropertyModel<NewClothes>(new PropertyModel<NewClothes>(this, "newClothes"))) {
			@Override
			public boolean isVisible() {
				return Name.C2.equals(typeName);
			}
		};
		// Add new item panel
		addNewButton = new AjaxButton("addNew") {
			@Override
			public boolean isEnabled() {
				return availableTypes.size() > 0;
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// newClothingForm.process(this);
				addNewClothingItem(target);
			}
		};
		addNewButton.setOutputMarkupId(true);
		newClothingForm.add(addNewButton);
		newTypeField = new DropDownChoice<ClothingType>("newType", new PropertyModel<List<ClothingType>>(this, "availableTypes")) {
			@Override
			public boolean isEnabled() {
				return availableTypes.size() > 0;
			}
		};
		newTypeField.setOutputMarkupId(true);
		newClothingForm.add(newTypeField.setNullValid(false).setRequired(true));
		tabs.add(newClothingForm);
		// Clothes
		clothingContainer = new WebMarkupContainer("clothingContainer");
		clothingContainer.setOutputMarkupId(true);
		tabs.add(clothingContainer);
		clothingView = new ListView<ClothingItem>("clothing", new PropertyModel<List<ClothingItem>>(this, "clothing")) {
			@Override
			protected void populateItem(final ListItem<ClothingItem> item) {
				item.add(new ClothingItemPanel("clothingItem", item.getModel()) {
					@Override
					protected void onCopyItem(AjaxRequestTarget target) {
						copyToOthers(item.getModelObject(), target);
					}

					@Override
					protected void onRemoveItem(AjaxRequestTarget target) {
						super.onRemoveItem(target);
						final ClothingItem modelObject = item.getModelObject();
						modelObject.setAsset(getAssetInput());
						modelObject.setColors(getColoursInput());
						removeItem(target, modelObject);
					}
				});
			}
		};
		clothingContainer.add(clothingView);
	}

	protected void addNewAttachment(AjaxRequestTarget target) {
		createDefaultColours();
		attachments.add(newAttachmentItem);
		target.add(rawContainer);
		target.add(attachmentsContainer);
		target.add(addNewAttachmentButton);
		target.add(feedback);
		newAttachmentItem = null;
	}

	protected void addNewClothingItem(AjaxRequestTarget target) {
		List<RGB> colors = new ArrayList<RGB>();
		if (clothing.size() > 0) {
			int s = 0;
			for (ClothingItem t : clothing) {
				s = Math.max(t.getColors().size(), s);
			}
			for (int i = 0; i < s; i++) {
				colors.add(new Color(0xff, 0xff, 0xff));
			}
		}
		final ClothingItem newItem = new ClothingItem(newClothes.newType, "", null, colors);
		clothing.add(newItem);
		rebuildAvailableTypes();
		target.add(clothingContainer);
		target.add(rawContainer);
		target.add(addNewButton);
		target.add(newTypeField);
		target.add(feedback);
	}

	protected void addRaw() {
		// Raw
		rawContainer = new WebMarkupContainer("rawContainer");
		rawContainer.setOutputMarkupId(true);
		tabs.add(rawContainer);
		raw = new TextArea<String>("raw", new PropertyModel<String>(this, "raw"));
		rawContainer.add(raw);
		AjaxButton apply = new AjaxButton("apply") {
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// try {
				// setRaw(((TextArea<String>)getParent().get("raw")).getConvertedInput());
				// } catch (ParseException e) {
				// }
				// setupElements();
				target.add(skinContainer);
				target.add(clothingContainer);
				target.add(appearanceContainer);
				target.add(attachmentsContainer);
				target.add(getParent());
				skinView.removeAll();
			}
		};
		rawContainer.add(apply);
	}

	protected void addSkin() {
		// Skin
		skinContainer = new WebMarkupContainer("skinContainer") {
			@Override
			public boolean isVisible() {
				return !Name.P1.equals(typeName);
			}
		};
		skinContainer.setOutputMarkupId(true);
		tabs.add(skinContainer);
		skinView = new ListView<SkinElement>("skin", new PropertyModel<List<SkinElement>>(this, "skinElements")) {
			@Override
			protected void populateItem(final ListItem<SkinElement> item) {
				item.add(new SkinElementPanel("skinElement", item.getModel()) {
					@Override
					protected void onRemoveItem(AjaxRequestTarget target) {
						super.onRemoveItem(target);
						skinElements.remove(item.getModelObject());
						target.add(skinContainer);
						target.add(rawContainer);
					}
				});
			}
		};
		skinContainer.add(skinView);
		// Add new skin element
		final Form<NewSkin> newSkinForm = new InternalForm<NewSkin>("newSkinForm",
				new CompoundPropertyModel<NewSkin>(new PropertyModel<NewSkin>(this, "newSkin"))) {
			@Override
			public boolean isVisible() {
				return !Name.P1.equals(typeName);
			}
		};
		newSkinForm.setOutputMarkupId(true);
		newSkinForm.add(new TextField<String>("name").setRequired(true).setLabel(new Model<String>("Skin Element Name")));
		newSkinForm.add(new ColorField("colour").setRequired(true).setLabel(new Model<String>("Skin Element Colour")));
		newSkinForm.add(new AjaxButton("submitNew") {
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				newSkinForm.process(this);
				skinElements.add(new SkinElement(newSkin.name, newSkin.colour));
				target.add(skinContainer);
				target.add(rawContainer);
				target.add(feedback);
			}
		});
		tabs.add(newSkinForm);
	}

	protected void appearanceSaved(AjaxRequestTarget target) {
	}

	@Override
	protected void convertInput() {
		final Appearance modelObject = getModelObject();
		if (modelObject != null) {
			setToAppearance(modelObject);
		}
		super.convertInput();
		setConvertedInput(modelObject);
	}

	protected void setToAppearance(final Appearance modelObject) {
		if (saveAppearance) {
			modelObject.setName(typeName);
			modelObject.setAsset(asset);
			modelObject.setSize(size);
			if (Name.C2.equals(typeName)) {
				modelObject.setGender(gender);
				modelObject.setHead(head);
				modelObject.setRace(race);
				modelObject.setBody(body);
			}
			if (Name.C2.equals(typeName) || Name.N4.equals(typeName)) {
				modelObject.setAttachments(attachments);
				modelObject.setSkinElements(skinElements);
				modelObject.setClothing(clothing);
			}
		}
	}

	protected void copyToOthers(ClothingItem item, AjaxRequestTarget target) {
		List<ClothingItem> l = new ArrayList<ClothingItem>();
		for (ClothingItem it : clothing) {
			if (!it.getType().equals(item.getType())) {
				it.setColors(item.getColors());
				it.setAsset(item.getAsset());
			}
			l.add(it);
		}
		clothingView.removeAll();
		clothing = l;
		rebuildAvailableTypes();
		target.add(clothingContainer);
		target.add(addNewButton);
		target.add(newTypeField);
	}

	@SuppressWarnings("unchecked")
	protected void createDefaultColours() {
		for (GameItem gi : Application.getAppSession(getRequestCycle()).getDatabase().getItems().values()) {
			ItemAppearance ia = gi.getAppearance();
			if (ia != null) {
				for (AttachmentItem ai : ia.getAttachments()) {
					if (ai.getAsset().equals(newAttachmentItem.getAsset())) {
						newAttachmentItem.setColors((List<RGB>) UnoptimizedDeepCopy.copy(ai.getColors()));
						return;
					}
				}
			}
		}
	}

	protected AjaxEventBehavior createSizeKeyEventBehavior() {
		return new AjaxEventBehavior("keyup") {
			@SuppressWarnings("unchecked")
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				try {
					size = Double.parseDouble(((TextField<Double>) getComponent()).getInput());
					target.add(rawContainer);
				} catch (Exception e) {
					error("Failed to parse. " + e.getMessage());
					target.add(feedback);
				}
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.setThrottlingSettings(new ThrottlingSettings("typing", Duration.ONE_SECOND, true));
			}
		};
	}

	protected AjaxEventBehavior createTemplateKeyEventBehavior() {
		return new AjaxEventBehavior("keyup") {
			@SuppressWarnings("unchecked")
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				try {
					asset = ((TextField<Double>) getComponent()).getInput();
					target.add(rawContainer);
				} catch (Exception e) {
					error("Failed to parse. " + e.getMessage());
					target.add(feedback);
				}
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.setThrottlingSettings(new ThrottlingSettings("typing", Duration.ONE_SECOND, true));
			}
		};
	}

	protected void formChanged(AjaxRequestTarget target) {
		target.add(appearanceContainer);
		target.add(rawContainer);
	}

	protected void rebuildAvailableTypes() {
		availableTypes.clear();
		availableTypes.addAll(Arrays.asList(ClothingType.values()));
		for (ClothingItem item : clothing) {
			availableTypes.remove(item.getType());
		}
	}

	protected void removeAttachment(AjaxRequestTarget target, final AttachmentItem modelObject) {
		attachments.remove(modelObject);
		target.add(rawContainer);
		target.add(attachmentsContainer);
		target.add(addNewAttachmentButton);
	}

	protected void removeItem(AjaxRequestTarget target, final ClothingItem modelObject) {
		clothing.remove(modelObject);
		rebuildAvailableTypes();
		target.add(rawContainer);
		target.add(clothingContainer);
		target.add(addNewButton);
		target.add(newTypeField);
	}

	protected void setupElements() {
		final Appearance act = getModelObject();
		if (act != null) {
			setFromAppearance(act);
			;
		} else {
			skinElements = new ArrayList<SkinElement>();
			clothing = new ArrayList<ClothingItem>();
			attachments = new ArrayList<AttachmentItem>();
			typeName = Name.P1;
			gender = null;
			race = null;
			body = null;
			head = null;
			size = 1d;
			asset = null;
		}
		Collections.sort(clothing);
		Collections.sort(skinElements);
		Collections.sort(attachments);
	}

	private AjaxFormComponentUpdatingBehavior createRawUpdateBehavior() {
		return new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(rawContainer);
			}
		};
	}

	class NewClothes implements Serializable {
		private ClothingType newType;
	}

	class NewSkin implements Serializable {
		private String name = "";
		private RGB colour = new Color(0, 0, 0);

		@Override
		public String toString() {
			return name + " " + colour;
		}
	}
}