package org.icemoon.worldeditor.player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.GroveTemplate;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.MapDef;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.eartheternal.common.ZoneDefs;
import org.icemoon.eartheternal.common.AbstractMultiINIFileEntities.IDType;
import org.icemoon.eartheternal.common.Account.Build;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.DynamicMapPage;
import org.icemoon.worldeditor.FixedAutocompleteComponent;
import org.icemoon.worldeditor.ZoneDefsPage;
import org.icemoon.worldeditor.components.ConfirmDialog;
import org.icemoon.worldeditor.model.EntityAvatarModel;
import org.icemoon.worldeditor.model.GameCharacterListModel;
import org.icemoon.worldeditor.model.PlayerInventoryModel;
import org.icemoon.worldeditor.model.UniqueListModel;
import org.icemoon.worldeditor.search.DoSearchPage;

@SuppressWarnings("serial")
public class HomePage extends AbstractUserPage {
	private String newPassword;
	private String confirmNewPassword;
	private Long recipient;
	private Long item;
	private String searchText;
	private String groveName;
	private GroveTemplate groveTemplate;
	private final IModel<Build> deleteGroveModel = new Model<Build>();
	private final IModel<GameCharacter> deleteCharacterModel = new Model<GameCharacter>();
	private WebMarkupContainer noGrove;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		// Search
		addSearch();
		addChangePassword();
		addCharacters();
		addMaps();
		addMail();
		addGroves();
	}

	private void addSearch() {
		Form<?> searchForm = new Form<Object>("searchForm");
		searchForm.add(new TextField<String>("searchText", new PropertyModel<String>(this, "searchText")).setRequired(true));
		searchForm.add(new Button("search") {
			@Override
			public void onSubmit() {
				setResponsePage(DoSearchPage.class, new PageParameters().add("searchText", searchText));
			}
		});
		searchForm.add(new Button("lucky") {
			@Override
			public void onSubmit() {
				setResponsePage(DoSearchPage.class, new PageParameters().add("lucky", true).add("searchText", searchText));
			}
		});
		add(searchForm);
	}

	private void addChangePassword() {
		// Change password
		Form<?> form = new Form<Object>("changePasswordForm");
		form.add(new FeedbackPanel("changePasswordFeedback"));
		form.setOutputMarkupId(true);
		form.add(new PasswordTextField("newPassword", new PropertyModel<String>(this, "newPassword")).setRequired(true));
		form.add(new PasswordTextField("confirmNewPassword", new PropertyModel<String>(this, "confirmNewPassword"))
				.setRequired(true));
		form.add(new AjaxButton("submitNewPassword") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (!newPassword.equals(confirmNewPassword)) {
					error("Passwords must be the same.");
				} else {
					Account account = getAccount();
					account.setAuth(newPassword);
					Application.getApp().getUserData().getAccounts().save(account);
					info("Saved new password");
				}
				target.add(form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		add(form);
	}

	private void addMail() {
		IModel<String> recipientModel = new IModel<String>() {
			@Override
			public void detach() {
			}

			@Override
			public String getObject() {
				return recipient == null ? null
						: Application.getApp().getUserData().getCharacters().get(recipient).getDisplayName();
			}

			@Override
			public void setObject(String object) {
				recipient = Util.isNullOrEmpty(object) ? null
						: Application.getApp().getUserData().getCharacters().getByName(object, true).getEntityId();
			}
		};
		IModel<String> itemModel = new IModel<String>() {
			@Override
			public void detach() {
			}

			@Override
			public String getObject() {
				return item == null ? null : getDatabase().getItems().get(item).getDisplayName();
			}

			@Override
			public void setObject(String object) {
				recipient = Util.isNullOrEmpty(object) ? null : getDatabase().getItems().getByName(object, true).getEntityId();
			}
		};
		// Mail components
		final Form<?> mailForm = new Form<Object>("mailForm");
		mailForm.add(new FeedbackPanel("mailFeedback"));
		mailForm.setOutputMarkupId(true);
		mailForm.add(new FixedAutocompleteComponent<String>("recipient", recipientModel,
				new UniqueListModel<String, GameCharacter>(new GameCharacterListModel(), "displayName")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return null;
			}
		});
		mailForm.add(new FixedAutocompleteComponent<String>("item", itemModel,
				new UniqueListModel<String, GameItem>(
						new PlayerInventoryModel(Application.getAppSession(getRequestCycle()).getAccount(), false, false,
								new PropertyModel<IDatabase>(this, "database"), new PropertyModel<IUserData>(this, "userData")),
						"displayName")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return null;
			}
		});
		mailForm.add(new AjaxButton("sendMail") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.add(mailForm);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		});
		add(mailForm);
	}

	private void addMaps() {
		final WebMarkupContainer mapsContainer = new WebMarkupContainer("mapsContainer");
		final PropertyModel<List<MapDef>> mapsModel = new PropertyModel<List<MapDef>>(this, "maps");
		final ListView<MapDef> mapsList = new ListView<MapDef>("maps", mapsModel) {
			@Override
			protected void populateItem(final ListItem<MapDef> item) {
				final Link<String> viewLink = new Link<String>("viewMap") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("id", item.getModelObject().getEntityId());
						setResponsePage(DynamicMapPage.class,
								new PageParameters().add("id", item.getModelObject().getEntityId()).add("characters", "true"));
					}
				};
				viewLink.add(new Label("mapName", new Model<String>(item.getModelObject().getName())));
				item.add(viewLink);
			}
		};
		mapsList.setReuseItems(false);
		mapsContainer.add(mapsList);
		add(mapsContainer);
	}

	private void addCharacters() {
		final WebMarkupContainer charactersContainer = new WebMarkupContainer("yourCharactersContainer");
		charactersContainer.setOutputMarkupId(true);
		final ConfirmDialog<GameCharacter> deleteCharacterDialog = new ConfirmDialog<GameCharacter>("confirmDeleteCharacterDialog",
				deleteCharacterModel, new Model<String>("Confirm Character Deletion"), new Model<String>() {
					public String getObject() {
						return deleteCharacterModel.getObject() == null ? null
								: MessageFormat.format(getLocalizer().getString("confirmDeleteCharacter", HomePage.this),
										String.valueOf(Application.getApp().getUserData().getCharacters()
												.get(deleteCharacterModel.getObject().getEntityId())));
					}
				}) {
			@Override
			protected void onConfirm(GameCharacter object, AjaxRequestTarget target) {
				Account acc = getAccount();
				acc.getCharacters().remove(object.getEntityId());
				Application.getApp().getUserData().getCharacters().delete(object);
				target.add(charactersContainer);
			}
		};
		add(deleteCharacterDialog);
		final PropertyModel<List<Long>> charactersModel = new PropertyModel<List<Long>>(this, "account.characters");
		final ListView<Long> characterList = new ListView<Long>("yourCharacters", charactersModel) {
			@Override
			protected void populateItem(final ListItem<Long> item) {
				final Link<String> viewLink = new Link<String>("viewCharacter") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("id", item.getModelObject());
						setResponsePage(CharacterPage.class, params);
					}
				};
				GameCharacter character = Application.getApp().getUserData().getCharacters().get(item.getModelObject());
				viewLink.add(new Label("characterName", new Model<String>(
						character == null ? "<Missing character " + item.getModelObject() + ">" : character.getDisplayName())));
				item.add(new Label("characterDetails", new Model<String>(character == null ? ""
						: "Level " + character.getLevel() + " " + Util.toEnglish(character.getProfession(), true))));
				item.add(viewLink);
				item.add(new AjaxLink<String>("removeCharacter") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						GameCharacter character = Application.getApp().getUserData().getCharacters().get(item.getModelObject());
						deleteCharacterModel.setObject(character);
						deleteCharacterDialog.open(target);
					}
				});
				item.add(new NonCachingImage("avatar", new EntityAvatarModel(new Model<GameCharacter>() {
					@Override
					public GameCharacter getObject() {
						return Application.getApp().getUserData().getCharacters().get(item.getModelObject());
					}
				})));
			}
		};
		characterList.setReuseItems(false);
		charactersContainer.add(characterList);
		add(charactersContainer);
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		Account account = getAccount();
		noGrove.setVisible(account != null && account.getBuilds().size() == 0);
	}

	public List<MapDef> getMaps() {
		return MapUtil.getMapsForAccount(getDatabase(), Application.getApp().getUserData(),
				Application.getAppSession(getRequestCycle()).getAccount());
	}

	public Account getAccount() {
		return Application.getAppSession(getRequestCycle()).getAccount();
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(HomePage.class, "HomePage.css")));
	}

	protected void addGroves() {
		// Grove
		final Form<Object> groveForm = new Form<Object>("groveForm");
		final FeedbackPanel groveFeedback = new FeedbackPanel("groveFeedback");
		groveFeedback.setOutputMarkupId(true);
		groveForm.add(groveFeedback);
		final ConfirmDialog<Build> deleteGroveDialog = new ConfirmDialog<Build>("confirmDeleteGroveDialog", deleteGroveModel,
				new Model<String>("Confirm Grove Deletion"), new Model<String>() {
					public String getObject() {
						return deleteGroveModel.getObject() == null ? null
								: MessageFormat.format(getLocalizer().getString("confirmDeleteGrove", HomePage.this), String
										.valueOf(getDatabase().getZoneDefs().get(deleteGroveModel.getObject().getEntityId())));
					}
				}) {
			@Override
			protected void onConfirm(Build object, AjaxRequestTarget target) {
				Account acc = getAccount();
				acc.getBuilds().remove(object);
				Application.getApp().getUserData().getAccounts().save(acc);
				ZoneDef instance = getDatabase().getZoneDefs().get(object.getEntityId());
				if (instance != null) {
					getDatabase().getZoneDefs().delete(instance);
				}
				noGrove.setVisible(acc.getBuilds().size() == 0);
				target.add(groveForm);
			}
		};
		groveForm.add(deleteGroveDialog);
		final PropertyModel<List<Build>> grovesModel = new PropertyModel<List<Build>>(this, "account.builds");
		final ListView<Build> groveList = new ListView<Build>("groves", grovesModel) {
			@Override
			protected void populateItem(final ListItem<Build> item) {
				final Link<String> viewLink = new Link<String>("viewGrove") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("id", item.getModelObject());
						setResponsePage(ZoneDefsPage.class, params);
					}
				};
				viewLink.add(new Label("groveName", new Model<String>() {
					@Override
					public String getObject() {
						ZoneDef instance = getDatabase().getZoneDefs().get(item.getModelObject().getEntityId());
						return instance == null ? "<Missing instance " + item.getModelObject() + ">" : instance.getName();
					}
				}));
				viewLink.setEnabled(false);
				item.add(viewLink);
				item.add(new AjaxLink<String>("removeGrove") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						deleteGroveModel.setObject(item.getModelObject());
						deleteGroveDialog.open(target);
					}
				});
			}
		};
		groveList.setReuseItems(false);
		groveForm.add(groveList);
		noGrove = new WebMarkupContainer("noGrove");
		groveForm.add(noGrove);
		groveForm.add(
				new TextField<String>("groveName", new PropertyModel<String>(this, "groveName"), String.class).setRequired(true));
		final ListChoice<GroveTemplate> listChoice = new ListChoice<GroveTemplate>("groveTemplate",
				new PropertyModel<GroveTemplate>(this, "groveTemplate"),
				new ArrayList<GroveTemplate>(getDatabase().getGroveTemplates().values()));
		groveForm.add(listChoice.setMaxRows(1).setNullValid(false).setRequired(true));
		groveForm.add(new AjaxButton("createGrove") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// form.process(this);
				final ZoneDefs instances = getDatabase().getZoneDefs();
				if (instances.getByName(groveName, true) != null) {
					error("A grove with that name already exists");
				} else {
					ZoneDef instance = new ZoneDef(getDatabase());
					instance.setEntityId(instances.getNextFreeId(1l, IDType.HIGHEST, null));
					instance.setName(groveName);
					instance.setEnvironmentType(groveTemplate.getEnvironment());
					instance.setTerrainConfig(groveTemplate.getTerrainConfig());
					instance.setShardName(Util.toEnglish(groveName, true));
					instance.setWarpName(groveName);
					instance.setLocation(groveTemplate.getDefaultLocation());
					instances.save(instance);
					Account acc = getAccount();
					acc.getBuilds().add(new Build(getUserData(), instance.getEntityId(), groveTemplate.getBuildBounds()));
					Application.getApp().getUserData().getAccounts().save(acc);
					noGrove.setVisible(acc.getBuilds().size() == 0);
					info("New grove created OK");
				}
				target.add(groveForm);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(groveFeedback);
			}
		});
		add(groveForm);
	}
}
