package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Abilities;
import org.icemoon.eartheternal.common.Ability;
import org.icemoon.eartheternal.common.GameIcon;
import org.icemoon.eartheternal.common.GameIcons;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.Ability.AbilityClass;
import org.icemoon.eartheternal.common.Ability.BuffType;
import org.icemoon.eartheternal.common.Ability.TargetStatus;
import org.icemoon.worldeditor.components.GameIconPanel;
import org.icemoon.worldeditor.components.SelectionBuilder;
import org.icemoon.worldeditor.model.AbilitiesModel;
import org.icemoon.worldeditor.model.UniqueListModel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class AbilitiesPage extends AbstractEntityPage<Ability, Integer, String, Abilities, IDatabase> {
	private GameIconPanel iconPreview;
	private GameIconPanel icon1Preview;
	private GameIconPanel icon2Preview;

	public AbilitiesPage() {
		super("entityId", Integer.class);
	}

	@Override
	protected void buildForm(final Form<Ability> form) {
		form.get("entityId").setOutputMarkupId(true);
		addIdType(form);
		List<String> files = new ArrayList<String>();
		for (String s : Arrays.asList("Data/AbilityTable.txt", "Data/AbilityTableAdmin.txt")) {
			FileObject serverDirectory = Application.getAppSession(getRequestCycle()).getDatabase().getServerDirectory();
			try {
				files.add(serverDirectory.resolveFile(s).getName().getURI());
			} catch (FileSystemException e) {
			}
		}
		addFiles(form, files);
		// General
		form.add(new TextField<String>("name").setRequired(true));
		form.add(new DropDownChoice<Integer>("hostility", Arrays.asList(-1, 0, 1)).setRequired(true));
		form.add(new TextField<Long>("warmupTime").setType(Long.class));
		form.add(new TextField<Long>("interval").setType(Long.class));
		form.add(new FixedAutocompleteComponent<String>("cooldownCategory", new PropertyModel<String>(this, "selected.cooldownCategory"),
				new UniqueListModel<String, Ability>(new AbilitiesModel(new PropertyModel<IDatabase>(this, "database")),
						"cooldownCategory")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		});
		form.add(new TextField<Long>("cooldownTime").setRequired(true));
		form.add(new DropDownChoice<Integer>("tier", Arrays.asList(1, 2, 3, 4, 5, 6)).setRequired(true));
		FixedAutocompleteComponent<Integer> autocompleteComponent = new FixedAutocompleteComponent<Integer>("groupId",
				new PropertyModel<Integer>(this, "selected.groupId"), new UniqueListModel<Integer, Ability>(
						new AbilitiesModel(new PropertyModel<IDatabase>(this, "database")), "groupId")) {
			@Override
			public Integer getValueOnSearchFail(String input) {
				try {
					return Integer.parseInt(input);
				} catch (Exception e) {
					return 0;
				}
			}
		};
		form.add(autocompleteComponent);
		form.add(new TextArea<String>("description"));
		// Purchase
		form.add(new TextField<Integer>("level").setRequired(true));
		form.add(new TextField<Integer>("crossCost").setRequired(true));
		form.add(new TextField<Integer>("classCost").setRequired(true));
		form.add(new CheckBox("mage"));
		form.add(new CheckBox("knight"));
		form.add(new CheckBox("druid"));
		form.add(new CheckBox("rogue"));
		form.add(new SelectionBuilder<Integer, Ability, String, Abilities, Long, IDatabase>("requiredAbilities", Integer.class, Ability.class,
				AbilitiesPage.class, new PropertyModel<Abilities>(this, "entityDatabase"), "name"));
		// Visual
		form.add(new FixedAutocompleteComponent<String>("warmupCue", new PropertyModel<String>(this, "selected.warmupCue"),
				new UniqueListModel<String, Ability>(new AbilitiesModel(new PropertyModel<IDatabase>(this, "database")),
						"warmupCue")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		});
		form.add(new FixedAutocompleteComponent<String>("visualCue", new PropertyModel<String>(this, "selected.visualCue"),
				new UniqueListModel<String, Ability>(new AbilitiesModel(new PropertyModel<IDatabase>(this, "database")),
						"visualCue")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		});
		form.add(new FixedAutocompleteComponent<String>("category", new PropertyModel<String>(this, "selected.category"),
				new UniqueListModel<String, Ability>(new AbilitiesModel(new PropertyModel<IDatabase>(this, "database")),
						"category")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		});
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
		form.add(new TextField<Integer>("x").setType(Integer.class).add(new RangeValidator<Integer>(0, 1024)));
		form.add(new TextField<Integer>("y").setType(Integer.class).add(new RangeValidator<Integer>(0, 1024)));
		// Activation
		form.add(new DropDownChoice<TargetStatus>("targetStatus", Arrays.asList(TargetStatus.values())));
		form.add(new TextArea<String>("activationCriteria"));
		form.add(new TextArea<String>("activationActions"));
		// Buff
		form.add(new DropDownChoice<BuffType>("buffType", Arrays.asList(BuffType.values())));
		form.add(new FixedAutocompleteComponent<String>("buffCategory", new PropertyModel<String>(this, "selected.buffCategory"),
				new UniqueListModel<String, Ability>(new AbilitiesModel(new PropertyModel<IDatabase>(this, "database")),
						"buffCategory")) {
			@Override
			public String getValueOnSearchFail(String input) {
				return input;
			}
		});
		form.add(new TextField<String>("buffTitle"));
		// Other
		form.add(new CheckBox("secondaryChannel"));
		form.add(new CheckBox("unbreakableChannel"));
		form.add(new CheckBox("allowDeadState"));
		form.add(new DropDownChoice<AbilityClass>("abilityClass", Arrays.asList(AbilityClass.values())));
		form.add(new DropDownChoice<Integer>("useType", Arrays.asList(0, 4)));
	}


	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(AbilitiesPage.class, "AbilitiesPage.css")));
		super.onRenderEntityHead(response);
	}

	@Override
	protected Ability createNewInstance() {
		return new Ability(getDatabase());
	}

	@Override
	public Abilities getEntityDatabase() {
		return getDatabase().getAbilities();
	}

	@Override
	protected boolean entityMatches(Ability object, Ability filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getName(), filter.getName())) {
			return false;
		}
		if (Util.notMatches(object.getLevel(), filter.getLevel())) {
			return false;
		}
		if (Util.notMatches(object.getTier(), filter.getTier())) {
			return false;
		}
		if (Util.notMatches(object.getGroupId(), filter.getGroupId())) {
			return false;
		}
		if (Util.notMatches(object.getCategory(), filter.getCategory())) {
			return false;
		}
		if (Util.notMatches(object.getCooldownCategory(), filter.getCooldownCategory())) {
			return false;
		}
		return true;
	}

	@Override
	protected void buildColumns(List<IColumn<Ability, String>> columns) {
		columns.add(
				new TextFilteredClassedPropertyColumn<Ability, String>(new ResourceModel("column.name"), "name", "name", "name"));
		columns.add(new TextFilteredClassedPropertyColumn<Ability, Integer>(new ResourceModel("column.level"), "level", "level",
				"level"));
		columns.add(
				new TextFilteredClassedPropertyColumn<Ability, Integer>(new ResourceModel("column.tier"), "tier", "tier", "tier"));
		columns.add(new TextFilteredClassedPropertyColumn<Ability, String>(new ResourceModel("column.groupId"), "groupId",
				"groupId", "groupId"));
		columns.add(new TextFilteredClassedPropertyColumn<Ability, String>(new ResourceModel("column.category"), "category",
				"category", "category"));
		columns.add(new TextFilteredClassedPropertyColumn<Ability, String>(new ResourceModel("column.cooldownCategory"),
				"cooldownCategory", "cooldownCategory", "cooldownCategory"));
	}
}
