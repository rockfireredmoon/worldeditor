package org.icemoon.worldeditor.player;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameCharacters;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GameItems;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.worldeditor.AbstractCreaturesPage;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.SelectorPanel;
import org.icemoon.worldeditor.ZoneDefsPage;
import org.icemoon.worldeditor.components.CharacterEquipmentPanel;
import org.icemoon.worldeditor.components.CharacterInventoryPanel;
import org.icemoon.worldeditor.components.CoinPanel;
import org.icemoon.worldeditor.components.XYZPanel;
import org.icemoon.worldeditor.dialogs.SelectorDialog;
import org.icemoon.worldeditor.model.FilterableSortableEntitiesDataProvider;
import org.icemoon.worldeditor.model.ZoneDefsModel;

@SuppressWarnings("serial")
public class CharactersPage extends AbstractCreaturesPage<GameCharacter, GameCharacters, IUserData> {
	final static String DEFAULT_ICON = "Icon-32-Icon_Holder2.png";
	private boolean showInventory;
	private boolean showEquipment;
	private CharacterInventoryPanel characterInventoryPanel;
	private WebMarkupContainer equipmentContainer;

	@Override
	protected Long peekNewId() {
		return Application.getApp().getUserData().getSessionVars().peekId(GameCharacters.NEXT_CHARACTER_ID);
	}

	protected Long processNewId(Long displayed) {
		return Application.getApp().getUserData().getSessionVars().nextId(GameCharacters.NEXT_CHARACTER_ID,
				GameCharacters.CHARACTER_ID_INCREMENT);
	}

	@Override
	protected void additionalValidation() {
		if (!editing && getEntityDatabase().getByDisplayName(getSelected().getDisplayName()) != null) {
			form.error("The character name " + getSelected().getDisplayName() + " already exists");
		}
	}

	protected GameCharacter configureFilterObject(GameCharacter obj) {
		obj.setLevel(0);
		return obj;
	}

	protected void addIdField() {
		form.add(new TextField<Long>("entityId", idClass) {
			@Override
			public boolean isEnabled() {
				return false;
			}
		}.setRequired(true));
	}

	@Override
	protected void onBuildForm(final Form<GameCharacter> form) {
		form.add(new TextField<String>("subName", String.class));
		form.add(new AjaxButton("view") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				PageParameters params = new PageParameters();
				params.set("id", getSelected().getEntityId());
				setResponsePage(CharacterPage.class, params);
			}

			@Override
			public boolean isEnabled() {
				final GameCharacter selected = getSelected();
				return selected != null && selected.getEntityId() != null;
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		form.add(new SelectorPanel<Long, ZoneDef, String, IDatabase>("zone", new Model<String>("Zone"), new ZoneDefsModel(this), "warpName",
				"entityId", new PropertyModel<Long>(this, "selected.zone"), ZoneDef.class, Long.class, ZoneDefsPage.class) {
			protected void onEntitySelected(AjaxRequestTarget target, ZoneDef entity) {
				getSelected().setLocation(entity.getLocation());
				target.add(getParent().get("location"));
			}
		}.setShowLabel(true));
		form.add(new TextField<Long>("instance", Long.class));
		form.add(new TextField<Integer>("order", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("heroism", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new CoinPanel("copper"));
		form.add(new TextField<Long>("credits", Long.class).add(new RangeValidator<Long>(0l, Long.MAX_VALUE)));
		form.add(new TextField<Integer>("maxSideKicks", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<String>("statusText", String.class));
		form.add(new XYZPanel("location").setOutputMarkupId(true));
		// Inventory
		characterInventoryPanel = new CharacterInventoryPanel("inventory", new PropertyModel<GameCharacter>(this, "selected"));
		form.add(characterInventoryPanel);
		equipmentContainer = new WebMarkupContainer("equipmentContainer");
		equipmentContainer.add(new CharacterEquipmentPanel("equipment", new PropertyModel<GameCharacter>(this, "selected"), false));
		equipmentContainer.add(new CharacterEquipmentPanel("bags", new PropertyModel<GameCharacter>(this, "selected"), true));
		final IModel<GameItems> model = new Model<GameItems>(getDatabase().getItems());
		FilterableSortableEntitiesDataProvider<GameItem, Long, String, IDatabase> provider = new FilterableSortableEntitiesDataProvider<GameItem, Long, String, IDatabase>(
				"displayName", model, new GameItem(getDatabase()));
		final SelectorDialog<Long, GameItem, String, IDatabase> newEquipmentDialog = new SelectorDialog<Long, GameItem, String, IDatabase>(
				"equipmentSelector", new Model<String>("Select Item"), provider, "displayName") {
			@Override
			protected void onSelectEntity(AjaxRequestTarget target, GameItem newEntity) {
				// charactersModel.getObject().add(newEntity.getId());
				// if(target != null) {
				// target.add(charactersContainer);
				// }
				super.onSelectEntity(target, newEntity);
			}
		};
		equipmentContainer.add(newEquipmentDialog);
		equipmentContainer.add(new AjaxButton("addEquipment") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				newEquipmentDialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		form.add(equipmentContainer);
		form.add(new AjaxLink<String>("toggleEquipment") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				showEquipment = !showEquipment;
				setPanesVisible();
				target.add(form);
			}
		}.add(new AttributeModifier("class", new Model<String>() {
			public String getObject() {
				return showEquipment ? "collapseAction" : "expandAction";
			}
		})));
		form.add(new AjaxLink<String>("toggleInventory") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				showInventory = !showInventory;
				setPanesVisible();
				target.add(form);
			}
		}.add(new AttributeModifier("class", new Model<String>() {
			public String getObject() {
				return showInventory ? "collapseAction" : "expandAction";
			}
		})));
	}

	@Override
	protected void onBeforeRender() {
		setPanesVisible();
		super.onBeforeRender();
	}

	protected void setPanesVisible() {
		equipmentContainer.setVisible(showEquipment);
		characterInventoryPanel.setVisible(showInventory);
	}

	@Override
	protected void onRenderCreatureHead(IHeaderResponse response) {
		super.onRenderCreatureHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(CharactersPage.class, "CharactersPage.css")));
	}

	@Override
	protected GameCharacter createNewInstance() {
		return new GameCharacter(getUserData());
	}

	@Override
	public GameCharacters getEntityDatabase() {
		return Application.getApp().getUserData().getCharacters();
	}

	@Override
	protected void onBuildColumns(List<IColumn<GameCharacter, String>> columns) {
	}
}
