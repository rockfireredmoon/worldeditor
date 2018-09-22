package org.icemoon.worldeditor.player;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameCharacters;
import org.icemoon.eartheternal.common.GameIcon;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GameItems;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.INIWriter;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.ItemAppearance;
import org.icemoon.eartheternal.common.ItemQuality;
import org.icemoon.eartheternal.common.MapDef;
import org.icemoon.eartheternal.common.MapPoint;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.eartheternal.common.SetItem;
import org.icemoon.eartheternal.common.AbstractMultiINIFileEntities.IDType;
import org.icemoon.eartheternal.common.GameItem.ArmourType;
import org.icemoon.eartheternal.common.GameItem.BindingType;
import org.icemoon.worldeditor.AJAXDownload;
import org.icemoon.worldeditor.AbstractCreaturePage;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.IconChooser;
import org.icemoon.worldeditor.QuestPage;
import org.icemoon.worldeditor.components.CharacterEquipmentPanel;
import org.icemoon.worldeditor.components.CharacterInventoryPanel;
import org.icemoon.worldeditor.components.CharacterSessionDetailsPanel;
import org.icemoon.worldeditor.components.CoinViewPanel;
import org.icemoon.worldeditor.components.ConfirmDialog;
import org.icemoon.worldeditor.components.GameIconPanel;
import org.icemoon.worldeditor.components.MapPanel;

@SuppressWarnings("serial")
public class CharacterPage extends AbstractCreaturePage<GameCharacter, GameCharacters, IUserData> {
	private final IModel<Quest> abandonQuestModel = new Model<Quest>();
	private String setName;
	private ArmourType armourType;
	private boolean suffixSetName;
	private boolean wearSet;
	private boolean storeSet;
	private boolean exportSet;
	private boolean addSet = true;
	private String backgroundIcon;
	private List<SetItem> setItems = new ArrayList<SetItem>();
	private AJAXDownload download;
	private String exported;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		tabs.add(new CharacterInventoryPanel("inventory", getModel()));
		tabs.add(new CharacterEquipmentPanel("equipment", getModel(), false));
		tabs.add(new CharacterEquipmentPanel("bags", getModel(), true));
		addStatus();
		addPrivacy();
		add(new CharacterSessionDetailsPanel("session", getModel()));
		addTweak();
		addMaps();
	}

	protected void addMaps() {
		// Map
		final IModel<MapDef> mapDefModel = new Model<MapDef>();
		// {
		// public MapDef getObject() {
		// Account account =
		// Application.getAppSession(getRequestCycle()).getAccount();
		// if (account != null) {
		// final List<MapDef> mapsForAccount =
		// MapUtil.getMapsForAccount(account);
		// if (mapsForAccount.size() > 0) {
		// return mapsForAccount.get(0);
		// }
		// }
		// return null;
		// }
		// };
		IModel<? extends List<MapPoint>> points = new ListModel<MapPoint>() {
			public List<MapPoint> getObject() {
				// final MapDef object = mapDefModel.getObject();
				// if (object == null) {
				// return null;
				// }
				return MapUtil.getPointsForCharacter(getDatabase(), getModelObject());
			}
		};
		tabs.add(new MapPanel("mapPanel", mapDefModel, points).setWidth(400));
	}

	protected void addQuests() {
		final Form<Object> questsForm = new Form<Object>("questsForm");
		final WebMarkupContainer questsContainer = new WebMarkupContainer("questsContainer");
		questsContainer.setOutputMarkupId(true);
		final ConfirmDialog<Quest> abandonQuestDialog = new ConfirmDialog<Quest>("confirmAbandonQuestDialog", abandonQuestModel,
				new Model<String>("Confirm Abandon Quest"), new Model<String>() {
					public String getObject() {
						return abandonQuestModel.getObject() == null ? null
								: MessageFormat.format(getLocalizer().getString("confirmAbandonQuest", CharacterPage.this),
										abandonQuestModel.getObject().toString());
					}
				}) {
			@Override
			protected void onConfirm(Quest object, AjaxRequestTarget target) {
				getModelObject().getActiveQuests().remove(object.getEntityId());
				getEntityDatabase().save(getModelObject());
				target.add(questsForm);
			}
		};
		questsForm.add(abandonQuestDialog);
		final ListView<Long> questsList = new QuestList("quests", new PropertyModel<List<Long>>(this, "activeQuests"),
				new PropertyModel<IDatabase>(this, "database")) {
			protected void addActions(final Quest quest, ListItem<Long> item) {
				item.add(new AjaxLink<String>("abandonQuest") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						abandonQuestModel.setObject(quest);
						abandonQuestDialog.open(target);
					}
				});
			}
		};
		questsList.setReuseItems(false);
		questsContainer.add(questsList);
		final ListView<Long> finishedList = new QuestList("completeQuests", new PropertyModel<List<Long>>(this, "completeQuests"),
				new PropertyModel<IDatabase>(this, "database"));
		finishedList.setReuseItems(false);
		questsContainer.add(finishedList);
		questsForm.add(questsContainer);
		tabs.add(questsForm);
	}

	public List<Long> getCompleteQuests() {
		if (getModelObject() == null) {
			return null;
		}
		List<Long> l = new ArrayList<Long>(getModelObject().getCompleteQuests());
		Collections.sort(l, createCompleteQuestComparator());
		return l;
	}

	public List<Long> getActiveQuests() {
		if (getModelObject() == null) {
			return null;
		}
		List<Long> l = new ArrayList<Long>(getModelObject().getActiveQuests().keySet());
		Collections.sort(l, createActiveQuestComparator());
		return l;
	}

	protected Comparator<Long> createActiveQuestComparator() {
		final Comparator<Long> c = new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				Quest q1 = getDatabase().getQuests().get(o1);
				Quest q2 = getDatabase().getQuests().get(o1);
				int i = Integer.valueOf(q1.getLevel()).compareTo(Integer.valueOf(q2.getLevel()));
				if (i == 0) {
					i = q1.getTitle().compareTo(q2.getTitle());
				}
				return i;
			}
		};
		return c;
	}

	protected Comparator<Long> createCompleteQuestComparator() {
		final Comparator<Long> c = new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				Quest q1 = getDatabase().getQuests().get(o1);
				Quest q2 = getDatabase().getQuests().get(o1);
				if (q2.getRequires() != null && q2.getRequires().equals(q1.getEntityId())) {
					return 0;
				}
				int i = Integer.valueOf(q1.getLevel()).compareTo(Integer.valueOf(q2.getLevel()));
				if (i == 0) {
					i = q1.getTitle().compareTo(q2.getTitle());
				}
				return i;
			}
		};
		return c;
	}

	protected void addStatus() {
		final Form<?> form = new Form<Object>("statusForm");
		form.setOutputMarkupId(true);
		form.add(new FeedbackPanel("statusFeedback"));
		form.add(new TextField<String>("newStatus", new PropertyModel<String>(getModel(), "statusText")).setRequired(true));
		form.add(new AjaxButton("submitNewStatus") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				final GameCharacter character = CharacterPage.this.getModelObject();
				getEntityDatabase().save(character);
				info("Saved new status");
				target.add(form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		add(form);
	}

	protected void addTweak() {
		Form<Object> tweakForm = new Form<Object>("tweakForm");
		download = new AJAXDownload("export.dat") {
			@Override
			protected IResourceStream getResourceStream() {
				return new StringResourceStream(exported, "text/plain");
			}
		};
		tweakForm.add(download);
		tweakForm.setOutputMarkupId(true);
		final FeedbackPanel feedbackPanel = new FeedbackPanel("tweakFeedback");
		feedbackPanel.setOutputMarkupId(true);
		tweakForm.add(feedbackPanel);
		final WebMarkupContainer setContainer = new WebMarkupContainer("setItemNamesContainer");
		setContainer.setOutputMarkupId(true);
		tweakForm.add(setContainer);
		final IModel<String> backgroundIconModel = new PropertyModel<String>(this, "backgroundIcon");
		ListView<SetItem> itemNames = new ListView<SetItem>("setItemNames", new PropertyModel<List<SetItem>>(this, "setItems")) {
			@Override
			protected void populateItem(ListItem<SetItem> item) {
				final Model<String> label = new Model<String>(item.getModelObject().getEquip().toString());
				item.add(new Label("setItemNameLabel", label));
				item.add(new TextField<String>("setItemName", new PropertyModel<String>(item.getModel(), "name")).setRequired(true)
						.setLabel(label));
				IModel<String> iconModel = new PropertyModel<String>(item.getModel(), "icon");
				final GameIconPanel itemIconPreview = new GameIconPanel("setItemIconPreview", iconModel, backgroundIconModel, 24);
				item.add(itemIconPreview);
				item.add(new IconChooser("setItemIcon", new Model<String>("Background Icon"), iconModel) {
					@Override
					protected void onEntitySelected(AjaxRequestTarget target, GameIcon entity) {
						// target.add(itemIconPreview);
						target.add(setContainer);
					}
				}.setShowClear(false).setShowLabel(false));
			}
		};
		setContainer.add(itemNames);
		tweakForm.add(new TextField<String>("setName", new PropertyModel<String>(this, "setName")).setRequired(true)
				.add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						redoSetItemNames();
						target.add(setContainer);
					}
				}));
		final GameIconPanel backgroundIconPreview = new GameIconPanel("backgroundIconPreview", backgroundIconModel, 32);
		tweakForm.add(backgroundIconPreview);
		backgroundIconPreview.setOutputMarkupId(true);
		tweakForm.add(new IconChooser("backgroundIcon", new Model<String>("Background Icon"), backgroundIconModel) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, GameIcon entity) {
				target.add(backgroundIconPreview);
				target.add(setContainer);
			}
		}.setShowLabel(false));
		tweakForm.add(
				new CheckBox("suffixSetName", new PropertyModel<Boolean>(this, "suffixSetName")).add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						redoSetItemNames();
						target.add(setContainer);
					}
				}));
		final CheckBox wearSetCheckBox = new CheckBox("wearSet", new PropertyModel<Boolean>(this, "wearSet")) {
			@Override
			public boolean isEnabled() {
				return addSet;
			}
		};
		wearSetCheckBox.setOutputMarkupId(true);
		tweakForm.add(wearSetCheckBox);
		final CheckBox storeSetCheckBox = new CheckBox("storeSet", new PropertyModel<Boolean>(this, "storeSet")) {
			@Override
			public boolean isEnabled() {
				return addSet;
			}
		};
		tweakForm.add(storeSetCheckBox);
		storeSetCheckBox.setOutputMarkupId(true);
		tweakForm.add(new CheckBox("exportSet", new PropertyModel<Boolean>(this, "exportSet")));
		tweakForm.add(new CheckBox("addSet", new PropertyModel<Boolean>(this, "addSet")).add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				redoSetItemNames();
				target.add(storeSetCheckBox);
				target.add(wearSetCheckBox);
			}
		}));
		tweakForm.add(new DropDownChoice<ArmourType>("armourType", new PropertyModel<ArmourType>(this, "armourType"),
				Arrays.asList(ArmourType.values())).setNullValid(false).setRequired(true));
		tweakForm.add(new AjaxButton("submitTweak") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				doTweak(feedbackPanel, target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedbackPanel);
			}
		});
		tabs.add(tweakForm);
	}

	protected void redoSetItemNames() {
		for (SetItem i : setItems) {
			if (suffixSetName) {
				i.setName(i.getDefaultLabel() + " " + setName);
			} else {
				i.setName(setName + " " + i.getDefaultLabel());
			}
		}
	}

	protected void addPrivacy() {
		final Form<?> form = new Form<Object>("privacyForm");
		form.setOutputMarkupId(true);
		final FeedbackPanel feedbackPanel = new FeedbackPanel("privacyFeedback");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);
		for (String perm : new String[] { "publishMyStats", "publishMyLocation", "publishMyEquipment", "publishMyBags",
				"publishMyQuests" }) {
			form.add(new AjaxCheckBox(perm, new BooleanPreferenceModel("website." + perm)) {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					final GameCharacter character = CharacterPage.this.getModelObject();
					getEntityDatabase().save(character);
					info("Privacy settings updated");
					target.add(feedbackPanel);
				}
			});
		}
		add(form);
	}

	protected void doTweak(final FeedbackPanel feedbackPanel, AjaxRequestTarget target, Form<?> form) {
		final GameCharacter character = CharacterPage.this.getModelObject();
		List<GameItem> items = new ArrayList<GameItem>();
		final GameItems gameItems = getDatabase().getItems();
		boolean errors = false;
		exported = null;
		for (SetItem item : setItems) {
			if (addSet && gameItems.getByName(item.getName(), true) != null) {
				error("Item named " + item.getName() + " already exists. Choose a different name for the "
						+ item.getEquip().toString());
				errors = true;
			} else {
				GameItem gi = new GameItem(getDatabase());
				gi.setType(GameItem.Type.ARMOUR);
				gi.setDisplayName(item.getName());
				gi.setIcon1(item.getEquip().getDefaultIcon());
				gi.setIcon2(backgroundIcon);
				gi.setLevel(character.getLevel());
				gi.setBindingType(BindingType.NORMAL);
				gi.setEquipType(item.getEquip());
				gi.setQuality(ItemQuality.EPIC);
				gi.setArmourType(armourType);
				ItemAppearance app = new ItemAppearance();
				if (item.getClothingItem() != null) {
					app.setClothingColor(item.getClothingItem().getColors());
					app.setClothingAsset(item.getClothingItem().getAsset());
				}
				if (item.getAttachmentItems() != null) {
					// for(AttachmentItem item) {
					//
					// }
					// AttachmentItem newAi = new
					// AttachmentItem(item.attachmentItem.getAsset(),
					// item.attachmentItem.getColors(),
					// item.attachmentItem.getNode());
					// List<AttachmentItem> i = new ArrayList<AttachmentItem>();
					// i.add(newAi);
					// if (item.equip.equals(EquipType.SHOULDERS)) {
					// newAi.setNode("left_shoulder");
					// AttachmentItem newAi2 = new
					// AttachmentItem(item.attachmentItem.getAsset(),
					// item.attachmentItem.getColors(),
					// "right_shoulder");
					// i.add(newAi2);
					// }
					app.setAttachments(item.getAttachmentItems());
				}
				gi.setAppearance(app);
				try {
					FileObject f = VFS.getManager().resolveFile(gameItems.getFile());
					gi.setFile(f.getParent().resolveFile("ItemDef_Custom.txt").getName().getURI());
				} catch (FileSystemException e) {
					throw new RuntimeException(e);
				}
				items.add(gi);
			}
		}
		if (!errors) {
			Long lastId = 1l;
			StringWriter sw = new StringWriter();
			INIWriter w = new INIWriter(sw);
			for (GameItem item : items) {
				if (addSet) {
					item.setEntityId(lastId = gameItems.getNextFreeId(lastId, IDType.HIGHEST, null));
					gameItems.save(item);
					info("Saved new item " + item.getDisplayName() + " (" + item.getEntityId() + ")");
					if (storeSet) {
						try {
							character.addToInventory(item.getEntityId());
						} catch (Exception e) {
							warn("Failed to store. " + e.getMessage());
						}
					}
					if (wearSet) {
						try {
							character.equip(item);
						} catch (Exception e) {
							warn("Failed to equip. " + e.getMessage());
						}
					}
				} else {
					item.setEntityId(lastId);
					lastId++;
				}
				item.write(w);
				w.println();
			}
			if (exportSet) {
				exported = sw.toString();
				download.initiate(target);
			}
		}
		getEntityDatabase().save(character);
		target.add(feedbackPanel);
		form.clearInput();
	}

	protected void addYou() {
		super.addYou();
		add(new CoinViewPanel("copper", new PropertyModel<Long>(getModel(), "copper")));
		add(new Label("exp", new PropertyModel<Long>(getModel(), "exp")));
	}

	@Override
	protected void onBeforeRender() {
		setItems = createSetItems();
		super.onBeforeRender();
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		super.onRenderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(CharacterPage.class, "CharacterPage.css")));
	}

	@Override
	protected GameCharacters getEntityDatabase() {
		return Application.getApp().getUserData().getCharacters();
	}

	private static class QuestList extends ListView<Long> {
		private IModel<IDatabase> database;

		private QuestList(String id, IModel<? extends List<? extends Long>> model, IModel<IDatabase> database) {
			super(id, model);
			this.database = database;
		}

		@Override
		protected void populateItem(final ListItem<Long> item) {
			final Quest quest = database.getObject().getQuests().get(item.getModelObject());
			final Link<String> viewLink = new Link<String>("viewQuest") {
				@Override
				public void onClick() {
					PageParameters params = new PageParameters();
					params.add("id", quest.getEntityId());
					setResponsePage(QuestPage.class, params);
				}
			};
			viewLink.add(new Label("questName", new Model<String>() {
				@Override
				public String getObject() {
					return quest == null ? "<Unknown " + item.getModelObject() + ">" : quest.getTitle();
				}
			}));
			// If this quest's parent is above this one in the list, indent a
			// bit
			if (quest != null) {
				Quest rq = quest;
				Long req = quest.getRequires();
				int idx = item.getIndex();
				int indent = 0;
				while (req != null) {
					rq = database.getObject().getQuests().get(req);
					if (rq != null) {
						if (idx > 0 && req.equals(getModelObject().get(idx - 1))) {
							indent += 1;
						}
						idx--;
					}
					req = rq == null ? null : rq.getRequires();
				}
				viewLink.add(new AttributeModifier("style", "margin-left:" + (indent * 4) + "px"));
			}
			item.add(viewLink);
			addActions(quest, item);
			item.add(new Label("partySize", new Model<Integer>() {
				@Override
				public Integer getObject() {
					return quest == null ? 0 : quest.getPartySize();
				}
			}));
			item.add(new Label("level", new Model<Integer>() {
				@Override
				public Integer getObject() {
					return quest == null ? 0 : quest.getLevel();
				}
			}));
		}

		protected void addActions(Quest quest, ListItem<Long> item) {
		}
	}

	protected List<SetItem> createSetItems() {
		GameCharacter c = getModelObject();
		if (c == null) {
			return null;
		}
		List<SetItem> l = SetItem.createSetItems(c.getAppearance());
		redoSetItemNames();
		return l;
	}

	class BooleanPreferenceModel extends Model<Boolean> {
		private String key;

		BooleanPreferenceModel(String key) {
			this.key = key;
		}

		public Boolean getObject() {
			return Boolean.valueOf(getModelObject().getPrefs().getProperty(key, "false"));
		}

		public void setObject(Boolean object) {
			getModelObject().getPrefs().setProperty(key, object.toString());
		}
	}
}
