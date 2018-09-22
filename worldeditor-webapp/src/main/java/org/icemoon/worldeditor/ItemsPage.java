package org.icemoon.worldeditor;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Book;
import org.icemoon.eartheternal.common.ClothingItem;
import org.icemoon.eartheternal.common.EquipType;
import org.icemoon.eartheternal.common.GameIcon;
import org.icemoon.eartheternal.common.GameIcons;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GameItems;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.ItemAppearance;
import org.icemoon.eartheternal.common.ItemQuality;
import org.icemoon.eartheternal.common.SpecialItemType;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.WeaponType;
import org.icemoon.eartheternal.common.Appearance.ClothingType;
import org.icemoon.eartheternal.common.GameItem.ArmourType;
import org.icemoon.eartheternal.common.GameItem.BindingType;
import org.icemoon.eartheternal.common.GameItem.Type;
import org.icemoon.worldeditor.components.CoinPanel;
import org.icemoon.worldeditor.components.GameIconPanel;
import org.icemoon.worldeditor.dialogs.ItemAppearanceEditorDialog;
import org.icemoon.worldeditor.dialogs.SelectorDialog;
import org.icemoon.worldeditor.model.BooksModel;
import org.icemoon.worldeditor.model.GameItemsModel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class ItemsPage extends AbstractEntityPage<GameItem, Long, String, GameItems, IDatabase> {
	protected Long newCraftItemId;
	private WebMarkupContainer resistance;
	private WebMarkupContainer weaponPlan;
	private WebMarkupContainer charmMods;
	private WebMarkupContainer weaponTypeContainer;
	private WebMarkupContainer armourTypeContainer;
	private WebMarkupContainer specialItemTypeContainer;
	private WebMarkupContainer bagContainer;
	private WebMarkupContainer bonuses;
	private WebMarkupContainer damageContainer;
	private GameIconPanel iconPreview;
	private GameIconPanel icon1Preview;
	private GameIconPanel icon2Preview;
	private WebMarkupContainer appearance;
	private FormComponent<String> subText;
	private FormComponent<String> flavorText;
	protected List<ItemQuality> qualityChoices = Arrays.asList(ItemQuality.values());
	protected List<EquipType> equipTypeChoices = Arrays.asList(EquipType.values());
	protected List<ArmourType> typeChoices = Arrays.asList(ArmourType.values());
	private ItemAppearanceEditorDialog editorDialog;

	public ItemsPage() {
		super("displayName", Long.class);
	}

	public ClothingItem getClothingItem() {
		GameItem sel = getSelected();
		if (sel == null) {
			return null;
		}
		final ClothingType clothingItemType = sel.getEquipType().toClothingItemType();
		if (clothingItemType == null) {
			// Not a clothing item
			return null;
		}
		ItemAppearance app = sel.getAppearance();
		if (app == null) {
			app = new ItemAppearance();
		}
		ClothingItem ci = new ClothingItem(clothingItemType, app.getClothingType(), null, app.getClothingColor());
		return ci;
	}

	@Override
	protected void onBeforeFormRender() {
		super.onBeforeFormRender();
		resistance.setVisible(
				getSelected() != null && !Arrays.asList(Type.CONTAINER, Type.QUEST_ITEMS).contains(getSelected().getType()));
		bonuses.setVisible(
				getSelected() != null && !Arrays.asList(Type.CONTAINER, Type.QUEST_ITEMS).contains(getSelected().getType()));
		weaponPlan.setVisible(getSelected() != null && Type.RECIPE.equals(getSelected().getType()));
		damageContainer.setVisible(getSelected() != null && Type.WEAPON.equals(getSelected().getType()));
		charmMods.setVisible(getSelected() != null && Type.CHARM.equals(getSelected().getType()));
		weaponTypeContainer.setVisible(getSelected() != null && Type.WEAPON.equals(getSelected().getType()));
		armourTypeContainer.setVisible(getSelected() != null && Type.ARMOUR.equals(getSelected().getType()));
		specialItemTypeContainer.setVisible(getSelected() != null && Type.SPECIAL.equals(getSelected().getType()));
		bagContainer.setVisible(getSelected() != null && Type.CONTAINER.equals(getSelected().getType()));
		appearance.setVisible(getSelected() != null && (Arrays.asList(Type.ARMOUR, Type.WEAPON).contains(getSelected().getType())));
		subText.setVisible(getSelected() != null && Type.QUEST_ITEMS.equals(getSelected().getType()));
		flavorText.setVisible(getSelected() != null);
	}

	@Override
	protected void buildForm(final Form<GameItem> form) {
		form.add(new AjaxButton("view") {
			@Override
			public boolean isEnabled() {
				return editing && getSelected().getEntityId() != null;
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				PageParameters params = new PageParameters();
				params.set("id", getSelected().getEntityId());
				setResponsePage(ItemPage.class, params);
			}
		}.setDefaultFormProcessing(false));
		addFiles(form, Arrays.asList(getDatabase().getPackageFiles("Packages/ItemPack.txt", null)));
		addIdType(form);
		form.add(new TextField<String>("displayName").setRequired(true).setLabel(new Model<String>("displayName")));
		form.add(subText = new TextField<String>("subText").setRequired(true).setLabel(new Model<String>("subText")));
		form.add(flavorText = new TextField<String>("flavorText").setLabel(new Model<String>("flavorText")));
		form.add(new TextField<Integer>("level", Integer.class) {
			public boolean isVisible() {
				return getSelected() != null && !Type.QUEST_ITEMS.equals(getSelected().getType());
			}
		}.setRequired(true).setLabel(new Model<String>("Level")).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("minUseLevel", Integer.class) {
			public boolean isVisible() {
				return getSelected() != null && !Type.QUEST_ITEMS.equals(getSelected().getType());
			}
		}.setLabel(new Model<String>("Min. Use Level")).setRequired(true).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new CheckBox("ownershipRestriction"));
		// Types
		final ListChoice<Type> typeField = new ListChoice<Type>("type", Arrays.asList(Type.values()));
		typeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(form);
			}
		});
		form.add(typeField.setMaxRows(1));
		form.add(new SelectorPanel<Long, Book, String, IDatabase>("book", new Model<String>("Book"), new BooksModel(this), "title",
				new Model<Long>() {
					@Override
					public Long getObject() {
						return Long.valueOf(getSelected().getIvMax1());
					}

					@Override
					public void setObject(Long l) {
						getSelected().setIvMax1(l.intValue());
					}
				}, Book.class, Long.class, BooksPage.class) {
			@Override
			public boolean isVisible() {
				return getSelected() != null && Type.SPECIAL.equals(getSelected().getType()) && getSelected().getIvType1() == 11;
			}
		}.setShowLabel(true).setShowClear(true));
		form.add(new TextField<Integer>("bookPage", new Model<Integer>() {
			@Override
			public void setObject(Integer i) {
				getSelected().setIvMax2(i == null ? 0 : i);
			}

			public Integer getObject() {
				return getSelected() == null ? 0 : getSelected().getIvMax2();
			}
		}, Integer.class) {
			@Override
			public boolean isRequired() {
				return getSelected() != null && Type.SPECIAL.equals(getSelected().getType()) && getSelected().getIvType1() == 11;
			}
		}.add(new RangeValidator<Integer>(1, Integer.MAX_VALUE)));
		form.add(new DropDownChoice<BindingType>("bindingType", Arrays.asList(BindingType.values())) {
			public boolean isVisible() {
				return getSelected() != null && !Type.RECIPE.equals(getSelected().getType())
						&& !Type.QUEST_ITEMS.equals(getSelected().getType());
			}
		});
		final DropDownChoice<EquipType> equipTypeField = new DropDownChoice<EquipType>("equipType",
				Arrays.asList(EquipType.values())) {
			public boolean isVisible() {
				return getSelected() != null && !Type.SPECIAL.equals(getSelected().getType())
						&& !Type.RECIPE.equals(getSelected().getType()) && !Type.CONSUMABLE.equals(getSelected().getType())
						&& !Type.QUEST_ITEMS.equals(getSelected().getType()) && ( !Type.SPECIAL.equals(getSelected().getType()) || getSelected().getIvType1() != 11 );
			}
		};
		equipTypeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(form);
			}
		});
		form.add(equipTypeField);
		form.add(new DropDownChoice<ItemQuality>("quality", Arrays.asList(ItemQuality.values())));
		form.add(new CoinPanel("value"));
		// Bag stuff
		bagContainer = new WebMarkupContainer("bagContainer");
		form.add(bagContainer);
		bagContainer.add(new TextField<Integer>("containerSlots", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(1, Integer.MAX_VALUE)));
		// Armour type
		armourTypeContainer = new WebMarkupContainer("armourTypeContainer");
		form.add(armourTypeContainer);
		armourTypeContainer.add(new ListChoice<ArmourType>("armourType", Arrays.asList(ArmourType.values())).setMaxRows(1));
		// Weapon type
		weaponTypeContainer = new WebMarkupContainer("weaponTypeContainer");
		form.add(weaponTypeContainer);
		weaponTypeContainer.add(new ListChoice<WeaponType>("weaponType", Arrays.asList(WeaponType.values())).setMaxRows(1));
		// Weapon type
		specialItemTypeContainer = new WebMarkupContainer("specialItemTypeContainer");
		form.add(specialItemTypeContainer);
		specialItemTypeContainer
				.add(new ListChoice<SpecialItemType>("specialItemType", Arrays.asList(SpecialItemType.values())).setMaxRows(1));
		addResistance(form);
		addDamage(form);
		addBonuses(form);
		addCharmMods(form);
		addPlans(form);
		addAppearance(form);
		addIcons(form);
	}

	protected void addIcons(final Form<GameItem> form) {
		final IModel<String> icon1Model = new PropertyModel<String>(this, "selected.icon1");
		final IModel<String> icon2Model = new PropertyModel<String>(this, "selected.icon2");
		IModel<GameIcons> gameIconsModel = new GameIconsModel();
		final IconChooser iconChooser1 = new IconChooser("icon1", new Model<String>("Icon 1"), gameIconsModel, icon1Model) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, GameIcon entity) {
				if (target != null) {
					target.add(iconPreview);
					target.add(icon1Preview);
				}
			}
		};
		iconChooser1.setOutputMarkupId(true);
		form.add(iconChooser1);
		form.add(iconPreview = new GameIconPanel("iconPreview", icon1Model, icon2Model, 32));
		iconPreview.setOutputMarkupId(true);
		form.add(icon1Preview = new GameIconPanel("icon1Preview", icon1Model, null));
		icon1Preview.setOutputMarkupId(true);
		form.add(icon2Preview = new GameIconPanel("icon2Preview", icon2Model, null));
		icon2Preview.setOutputMarkupId(true);
		final IconChooser iconChooser2 = new IconChooser("icon2", new Model<String>("Icon 2"), gameIconsModel, icon2Model) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, GameIcon entity) {
				if (target != null) {
					target.add(iconPreview);
					target.add(icon2Preview);
				}
			}
		};
		iconChooser2.setOutputMarkupId(true);
		form.add(iconChooser2);
		form.add(new AjaxLink<String>("swap") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				String o2 = icon2Model.getObject();
				icon2Model.setObject(icon1Model.getObject());
				icon1Model.setObject(o2);
				target.add(icon1Preview);
				target.add(iconChooser1);
				target.add(icon2Preview);
				target.add(iconChooser2);
				target.add(iconPreview);
			}
		});
	}

	protected void addResistance(final Form<GameItem> form) {
		resistance = new WebMarkupContainer("resistance");
		resistance.add(
				new TextField<Integer>("armourResistMelee", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		resistance.add(
				new TextField<Integer>("armourResistFire", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		resistance.add(
				new TextField<Integer>("armourResistFrost", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		resistance.add(
				new TextField<Integer>("armourResistMystic", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		resistance.add(
				new TextField<Integer>("armourResistDeath", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(resistance);
	}

	protected void addDamage(final Form<GameItem> form) {
		damageContainer = new WebMarkupContainer("damage");
		form.add(damageContainer);
		damageContainer.add(new TextField<Integer>("weaponDamageMin", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		damageContainer.add(new TextField<Integer>("weaponDamageMax", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
	}

	protected void addBonuses(final Form<GameItem> form) {
		bonuses = new WebMarkupContainer("bonuses");
		form.add(bonuses);
		bonuses.add(new TextField<Integer>("bonusDexterity", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		bonuses.add(new TextField<Integer>("bonusConstitution", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		bonuses.add(new TextField<Integer>("bonusPsyche", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		bonuses.add(new TextField<Integer>("bonusSpirit", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		bonuses.add(new TextField<Integer>("bonusStrength", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
	}

	protected void addCharmMods(final Form<GameItem> form) {
		charmMods = new WebMarkupContainer("charmMods");
		form.add(charmMods);
		charmMods.add(new TextField<Integer>("healingMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("meleeHitMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("meleeCritMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("regenHealthMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("castSpeedMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("attackSpeedMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("blockMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("parryMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("runSpeedMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("magicHitMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		charmMods.add(new TextField<Integer>("magicCritMod", Integer.class).setRequired(true)
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
	}

	protected void addPlans(final Form<GameItem> form) {
		weaponPlan = new WebMarkupContainer("weaponPlan");
		form.add(weaponPlan);
		IModel<GameItems> itemsModel = new GameItemsModel(this);
		weaponPlan.add(new SelectorPanel<Long, GameItem, String, IDatabase>("resultItemId", new Model<String>("Result"), itemsModel,
				"displayName", GameItem.class, Long.class, ItemsPage.class).setShowClear(false));
		weaponPlan.add(new SelectorPanel<Long, GameItem, String, IDatabase>("keyComponentId", new Model<String>("Key Component"), itemsModel,
				"displayName", GameItem.class, Long.class, ItemsPage.class).setShowClear(false));
		final WebMarkupContainer craftItemsListContainer = new WebMarkupContainer("craftItemIdsContainer");
		final PropertyModel<List<Long>> craftItemIdsModel = new PropertyModel<List<Long>>(this, "selected.craftItemIds");
		final ListView<Long> craftItemsList = new ListView<Long>("craftItemIds", craftItemIdsModel) {
			@Override
			protected void populateItem(final ListItem<Long> item) {
				item.add(new Label("craftItemName", new Model<String>() {
					@Override
					public String getObject() {
						return getEntityDatabase().get(item.getModelObject()).getDisplayName();
					}
				}));
				item.add(new AjaxLink<String>("removeCraftItem") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						craftItemIdsModel.getObject().remove(item.getModelObject());
						target.add(craftItemsListContainer);
					}
				});
			}
		};
		craftItemsList.setReuseItems(false);
		craftItemsListContainer.setOutputMarkupId(true);
		craftItemsListContainer.add(craftItemsList);
		weaponPlan.add(craftItemsListContainer);
		final SelectorDialog<Long, GameItem, String, IDatabase> newItemDialog = new SelectorDialog<Long, GameItem, String, IDatabase>(
				"newCraftItemSelector", new Model<String>("New Craft Item"), createModel(), "displayName") {
			@Override
			protected void onSelectEntity(AjaxRequestTarget target, GameItem newEntity) {
				craftItemIdsModel.getObject().add(newEntity.getEntityId());
				if (target != null) {
					target.add(craftItemsListContainer);
				}
				super.onSelectEntity(target, newEntity);
			}
		};
		craftItemsListContainer.add(newItemDialog);
		craftItemsListContainer.add(new AjaxButton("newCraftItem") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				newItemDialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
	}

	protected void addAppearance(final Form<GameItem> form) {
		editorDialog = new ItemAppearanceEditorDialog("appearanceEditorDialog",
				new PropertyModel<ItemAppearance>(this, "selected.appearance")) {
			@Override
			protected void appearanceSaved(AjaxRequestTarget target) {
				super.appearanceSaved(target);
				target.add(getParent().get("form"));
				System.out.println("SEL: " + getSelected().getAppearance());
			}
		};
		add(editorDialog);
		appearance = new WebMarkupContainer("appearance");
		appearance.setOutputMarkupId(true);
		appearance.add(new TextArea<String>("appearanceString", new PropertyModel<String>(this, "appearanceString"))
				.setLabel(new Model<String>("appearanceString")));
		appearance.add(new AjaxButton("editAppearance") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				editorDialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		form.add(appearance);
	}

	public String getAppearanceString() {
		final ItemAppearance app = getSelected().getAppearance();
		return app == null ? "" : app.toString();
	}

	public void setAppearanceString(String str) {
		try {
			getSelected().setAppearance(StringUtils.isBlank(str) ? new ItemAppearance() : new ItemAppearance(str));
		} catch (Exception e) {
			error("Failed to parse appearance string. " + e.getMessage());
		}
	}

	@Override
	protected boolean entityMatches(GameItem object, GameItem filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getDisplayName(), filter.getDisplayName())) {
			return false;
		}
		if (Util.notMatches(object.getLevel(), filter.getLevel())) {
			return false;
		}
		if (Util.notMatches(object.getQuality(), filter.getQuality())) {
			return false;
		}
		if (Util.notMatches(object.getArmourType(), filter.getArmourType())) {
			return false;
		}
		if (Util.notMatches(object.getEquipType(), filter.getEquipType())) {
			return false;
		}
		return true;
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(ItemsPage.class, "ItemsPage.css")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(ItemsPage.class, "jquery.qtip.css")));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ItemsPage.class, "jquery.qtip.js")));
	}

	@Override
	protected GameItem createNewInstance() {
		return new GameItem(getDatabase());
	}

	@Override
	public GameItems getEntityDatabase() {
		return getDatabase().getItems();
	}

	@Override
	protected void buildColumns(List<IColumn<GameItem, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<GameItem, String>(new ResourceModel("column.level"), "level", "level",
				"level"));
		IModel<List<? extends ArmourType>> typeModel = new PropertyModel<List<? extends ArmourType>>(this, "typeChoices");
		columns.add(new ChoiceFilteredPropertyColumn<GameItem, ArmourType, String>(new ResourceModel("column.type"), "type", "type",
				typeModel));
		IModel<List<? extends EquipType>> equipTypeModel = new PropertyModel<List<? extends EquipType>>(this, "equipTypeChoices");
		columns.add(new ChoiceFilteredPropertyColumn<GameItem, EquipType, String>(new ResourceModel("column.equipType"),
				"equiptype", "equipType", equipTypeModel));
		IModel<List<? extends ItemQuality>> listModel = new PropertyModel<List<? extends ItemQuality>>(this, "qualityChoices");
		columns.add(new ChoiceFilteredPropertyColumn<GameItem, ItemQuality, String>(new ResourceModel("column.quality"), "quality",
				"quality", listModel) {
			protected IChoiceRenderer<ItemQuality> getChoiceRenderer() {
				return new IChoiceRenderer<ItemQuality>() {
					@Override
					public Object getDisplayValue(ItemQuality object) {
						return String.valueOf(object.name().charAt(0));
					}

					@Override
					public String getIdValue(ItemQuality object, int index) {
						return object.name();
					}
				};
			}

			public void populateItem(Item<ICellPopulator<GameItem>> cellItem, String componentId, IModel<GameItem> model) {
				ItemQuality quality = model.getObject().getQuality();
				if (quality != null) {
					cellItem.add(new AttributeModifier("class", "quality quality_" + quality.name().toLowerCase()));
				}
				Label label = new Label(componentId, new Model<String>(Util.toEnglish(quality, true)));
				cellItem.add(label);
			}
		});
		columns.add(new TextFilteredClassedPropertyColumn<GameItem, String>(new ResourceModel("column.displayName"), "displayName",
				"displayName", "displayName"));
	}

	class ImagePanel extends Panel {
		public ImagePanel(String id, IModel<ResourceReference> model) {
			super(id, model);
			add(new Image("image", model));
		}
	}
}
