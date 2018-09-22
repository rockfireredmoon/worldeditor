package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.AIScript;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.CreatureLoot;
import org.icemoon.eartheternal.common.Creatures;
import org.icemoon.eartheternal.common.DropProfile;
import org.icemoon.eartheternal.common.Hint;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.eartheternal.common.Rarity;
import org.icemoon.eartheternal.common.Scenery;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.components.InternalForm;
import org.icemoon.worldeditor.model.AIScriptsModel;
import org.icemoon.worldeditor.model.DropProfilesModel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class CreaturesPage extends AbstractCreaturesPage<Creature, Creatures, IDatabase> {
	private Form<Object> spawnsForm;
	private Form<Object> lootForm;

	@Override
	public Creatures getEntityDatabase() {
		return getDatabase().getCreatures();
	}

	@Override
	protected Creature configureFilterObject(Creature obj) {
		obj.setLevel(0);
		return obj;
	}

	@Override
	protected Creature createNewInstance() {
		return new Creature(getDatabase());
	}

	@Override
	protected boolean entityMatches(Creature object, Creature filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getSubName(), filter.getSubName())) {
			return false;
		}
		return true;
	}

	@Override
	protected void itemChanged(AjaxRequestTarget target) {
		super.itemChanged(target);
		target.add(spawnsForm);
	}

	@Override
	protected void onBuildColumns(List<IColumn<Creature, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<Creature, String>(new ResourceModel("subName"), "subName", "subName",
				"subName"));
	}

	@Override
	protected void onBuildForm(final Form<Creature> form) {
		addIdType(form);
		addFiles(form, Arrays.asList(getDatabase().getPackageFiles("Packages/CreaturePack.txt", null)));
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
				setResponsePage(CreaturePage.class, params);
			}
		}.setDefaultFormProcessing(false));
		form.add(new TextField<String>("subName").setLabel(new Model<String>("subName")));
		form.add(new TextField<Float>("totalSize", Float.class).add(new RangeValidator<Float>(0f, Float.MAX_VALUE)));
		form.add(new TextField<Integer>("baseDamageMelee", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("baseHealth", Integer.class).setRequired(true).add(new RangeValidator<Integer>(0, (int)Short.MAX_VALUE)));
		form.add(new DropDownChoice<Rarity>("rarity", Arrays.asList(Rarity.values())));
		form.add(new SelectorPanel<String, AIScript, String, IDatabase>("aiPackage", new Model<String>("AI Script"), new AIScriptsModel(this),
				"entityId", AIScript.class, String.class, AIScriptsPage.class).setShowClear(true).setShowLabel(true));
		form.add(new SelectorPanel<String, DropProfile, String, IDatabase>("dropRateProfile", new Model<String>("Drop Rate Profile"), new DropProfilesModel(this),
				"entityId", DropProfile.class, String.class, DropProfilesPage.class).setShowClear(true).setShowLabel(true));
		form.add(new TextField<Float>("dropRateMult", Float.class).add(new RangeValidator<Float>(0f, Float.MAX_VALUE)));
		form.add(new CheckBox("aggroPlayers"));
		form.add(new CheckBox("namedMob"));
		form.add(new CheckBoxMultipleChoice<Hint>("hints", Arrays.asList(Hint.values())));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		addSpawns();
		addLoot();
	}

	@Override
	protected void onRenderCreatureHead(IHeaderResponse response) {
		super.onRenderCreatureHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(CreaturesPage.class, "CreaturesPage.css")));
	}

	// TODO Why?
//	protected Long peekNewId() {
//		return null;
//	}

	private void addLoot() {
		lootForm = new InternalForm<Object>("lootForm");
		lootForm.setOutputMarkupId(true);
		lootForm.add(new FeedbackPanel("lootFeedback"));
		final ListModel<String> lootModel = new ListModel<String>() {
			@Override
			public List<String> getObject() {
				Creature selected = getSelected();
				if (selected == null) {
					return null;
				}
				final Long entityId = selected.getEntityId();
				CreatureLoot cl = entityId == null ? null : getDatabase().getCreatureLoots().get(entityId);
				return cl == null ? null : cl.getPackages();
			}
		};
		final ListView<String> lootList = new ListView<String>("loot", lootModel) {
			@Override
			protected void populateItem(final ListItem<String> item) {
				final Link<String> viewLink = new Link<String>("viewLoot") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("id", item.getModelObject());
						setResponsePage(LootPackagesPage.class, params);
					}
				};
				viewLink.add(new Label("lootName", new Model<String>() {
					@Override
					public String getObject() {
						return item.getModelObject().toString();
					}
				}));
				item.add(viewLink);
			}
		};
		lootList.setReuseItems(false);
		lootForm.add(lootList);
		add(lootForm);
	}

	private void addSpawns() {
		spawnsForm = new InternalForm<Object>("spawnsForm");
		spawnsForm.setOutputMarkupId(true);
		spawnsForm.add(new FeedbackPanel("spawnsFeedback"));
		final ListModel<Scenery> spawnsModel = new ListModel<Scenery>() {
			@Override
			public List<Scenery> getObject() {
				return new ArrayList<Scenery>(MapUtil.getSpawnsForCreature(getDatabase(), getSelected()));
			}
		};
		final ListView<Scenery> spawnsList = new ListView<Scenery>("spawns", spawnsModel) {
			@Override
			protected void onAfterRender() {
				// TODO Auto-generated method stub
				super.onAfterRender();
			}

			@Override
			protected void populateItem(final ListItem<Scenery> item) {
				final Link<String> viewLink = new Link<String>("viewSpawn") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("zoneId", item.getModelObject().getInstanceId());
						params.add("id", item.getModelObject().getEntityId());
						setResponsePage(SceneryPage.class, params);
					}
				};
				viewLink.add(new Label("spawnName", new Model<String>() {
					@Override
					public String getObject() {
						return item.getModelObject().toString();
					}
				}));
				item.add(viewLink);
			}
		};
		spawnsList.setReuseItems(false);
		spawnsForm.add(spawnsList);
		add(spawnsForm);
	}

	class AIScriptsProvider implements ITreeProvider<String> {
		public AIScriptsProvider() {
		}

		@Override
		public void detach() {
		}

		@Override
		public Iterator<? extends String> getChildren(String object) {
			// Only have roots
			throw new IllegalStateException();
		}

		@Override
		public Iterator<? extends String> getRoots() {
			return getDatabase().getAIScripts().keySet().iterator();
		}

		@Override
		public boolean hasChildren(String object) {
			return false;
		}

		@Override
		public IModel<String> model(String object) {
			return new Model<String>(object);
		}
	}
}
