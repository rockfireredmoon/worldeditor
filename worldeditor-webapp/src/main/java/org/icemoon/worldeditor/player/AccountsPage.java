package org.icemoon.worldeditor.player;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.Accounts;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameCharacters;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.Account.Permission;
import org.icemoon.worldeditor.AbstractEntityPage;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.dialogs.SelectorDialog;
import org.icemoon.worldeditor.model.FilterableSortableEntitiesDataProvider;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class AccountsPage extends AbstractEntityPage<Account, Long, String, Accounts, IUserData> {
	public AccountsPage() {
		super(Long.class);
	}

	public long getSuspendDuration() {
		return getSelected().isSuspended() ? getSelected().getSuspendDuration() : 0;
	}

	public void setSuspendDuration(long v) {
		getSelected().setSuspendDuration(v);
	}

	@Override
	protected Long peekNewId() {
		return Application.getApp().getUserData().getSessionVars().peekId(Accounts.NEXT_ACCOUNT_ID);
	}

	protected Long processNewId(Long displayed) {
		return Application.getApp().getUserData().getSessionVars().nextId(Accounts.NEXT_ACCOUNT_ID);
	}

	@Override
	protected void additionalValidation() {
		if (!editing && getEntityDatabase().getAccount(getSelected().getName()) != null) {
			form.error("The user name " + getSelected().getName() + " already exists");
		}
		if (StringUtils.isNotBlank(getSelected().getGroveName())) {
			final Account accountByGrove = getEntityDatabase().getAccountByGrove(getSelected().getGroveName());
			if (accountByGrove != null
					&& ((editing && !accountByGrove.getEntityId().equals(getSelected().getEntityId())) || !editing)) {
				form.error("The grove name " + getSelected().getGroveName() + " is already in use");
			}
		}
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
	protected void buildForm(Form<Account> form) {
		form.add(new TextField<Long>("suspendDuration", new PropertyModel<Long>(this, "suspendDuration"))
				.add(new RangeValidator<Long>(0l, Long.MAX_VALUE)));
		form.add(new Label("suspendInfo"));
		form.add(new TextField<String>("name") {
			@Override
			public boolean isEnabled() {
				return !editing;
			}
		}.setLabel(new Model<String>("Name")).setRequired(true));
		form.add(new TextField<String>("groveName").setLabel(new Model<String>("Grove Name")));
		form.add(new PasswordTextField("auth").setResetPassword(false).setRequired(true).setLabel(new Model<String>("Password")));
		form.add(new CheckBoxMultipleChoice<Permission>("permissions", Arrays.asList(Permission.values())));
		final WebMarkupContainer charactersContainer = new WebMarkupContainer("charactersContainer");
		final PropertyModel<List<Long>> charactersModel = new PropertyModel<List<Long>>(this, "selected.characters");
		final ListView<Long> characterList = new ListView<Long>("characters", charactersModel) {
			@Override
			protected void populateItem(final ListItem<Long> item) {
				final Link<String> viewLink = new Link<String>("viewCharacter") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("id", item.getModelObject());
						setResponsePage(CharactersPage.class, params);
					}
				};
				viewLink.add(new Label("characterName", new Model<String>() {
					@Override
					public String getObject() {
						final GameCharacter character = Application.getApp().getUserData().getCharacters()
								.get(item.getModelObject());
						return character == null ? "<Missing character " + item.getModelObject() + ">" : character.getDisplayName();
					}
				}));
				item.add(viewLink);
				item.add(new AjaxLink<String>("removeCharacter") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						charactersModel.getObject().remove(item.getModelObject());
						target.add(charactersContainer);
					}
				});
			}
		};
		characterList.setReuseItems(false);
		charactersContainer.setOutputMarkupId(true);
		charactersContainer.add(characterList);
		form.add(charactersContainer);
		FilterableSortableEntitiesDataProvider<GameCharacter, Long, String, IUserData> provider = new FilterableSortableEntitiesDataProvider<GameCharacter, Long, String, IUserData>(
				"displayName", new Model<GameCharacters>(Application.getApp().getUserData().getCharacters()),
				new GameCharacter(getUserData()));
		final SelectorDialog<Long, GameCharacter, String, IUserData> newItemDialog = new SelectorDialog<Long, GameCharacter, String, IUserData>(
				"characterSelector", new Model<String>("Select Character"), provider, "displayName") {
			@Override
			protected void onSelectEntity(AjaxRequestTarget target, GameCharacter newEntity) {
				charactersModel.getObject().add(newEntity.getEntityId());
				if (target != null) {
					target.add(charactersContainer);
				}
				super.onSelectEntity(target, newEntity);
			}
		};
		charactersContainer.add(newItemDialog);
		charactersContainer.add(new AjaxButton("newCharacter") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				newItemDialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(AccountsPage.class, "AccountsPage.css")));
	}

	@Override
	protected Account createNewInstance() {
		return new Account(getUserData());
	}

	@Override
	public Accounts getEntityDatabase() {
		return Application.getApp().getUserData().getAccounts();
	}

	@Override
	protected void buildColumns(List<IColumn<Account, String>> columns) {
		columns.add(
				new TextFilteredClassedPropertyColumn<Account, String>(new ResourceModel("column.name"), "name", "name", "name"));
	}
}
