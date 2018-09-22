package org.icemoon.worldeditor;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
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
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.Creatures;
import org.icemoon.eartheternal.common.Flag;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.SpawnEntry;
import org.icemoon.eartheternal.common.SpawnPackage;
import org.icemoon.eartheternal.common.SpawnPackages;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.SpawnPackage.DivideOrient;
import org.icemoon.worldeditor.model.CreaturesModel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class SpawnPackagesPage extends AbstractEntityPage<SpawnPackage, String, String, SpawnPackages, IDatabase> {
	private Integer newChance;
	private Long newSpawnCreatureId;
	private String newComment;
	private int editingSpawn = -1;
	private boolean creatingSpawn;

	public SpawnPackagesPage() {
		super("entityId", String.class);
	}

	@Override
	public void select(SpawnPackage selected, int selectedIndex) {
		super.select(selected, selectedIndex);
		resetSpawnEdit();
	}

	protected void resetSpawnEdit() {
		editingSpawn = -1;
		newChance = 0;
		newSpawnCreatureId = 0l;
		newComment = null;
		creatingSpawn = false;
	}

	@Override
	protected void buildForm(final Form<SpawnPackage> form) {
		final Creatures creaturesDb = getDatabase().getCreatures();
		final PropertyModel<List<SpawnEntry>> spawnsModel = new PropertyModel<List<SpawnEntry>>(this, "selected.spawns");
		// New Spawn Form
		Form<?> newSpawnForm = new Form<Object>("newSpawnForm");
		newSpawnForm.add(new SelectorPanel<Long, Creature, String, IDatabase>("spawnSelector", new Model<String>("Spawn Character"),
				new CreaturesModel(this), "displayName", new PropertyModel<Long>(this, "newSpawnCreatureId"), Creature.class,
				Long.class, CreaturesPage.class) {
			@Override
			public boolean isVisible() {
				return editingSpawn != -1 || creatingSpawn;
			}
		}.setShowLabel(true));
		newSpawnForm.add(new TextField<Integer>("newChance", new PropertyModel<Integer>(this, "newChance"), Integer.class));
		newSpawnForm.add(new TextField<String>("newComment", new PropertyModel<String>(this, "newComment")));
		newSpawnForm.add(new AjaxButton("newSpawn") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				newChance = 0;
				newSpawnCreatureId = 0l;
				newComment = null;
				editingSpawn = -1;
				creatingSpawn = true;
				target.add(getParent().getParent());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getParent().getParent());
			}

			@Override
			public boolean isVisible() {
				return !creatingSpawn && editingSpawn == -1;
			}
		}.setDefaultFormProcessing(false));
		newSpawnForm.add(new AjaxButton("saveSpawn") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				final SpawnEntry spawnEntry = getSelected().getSpawns().get(editingSpawn);
				spawnEntry.setChance(newChance);
				spawnEntry.setComment(newComment);
				spawnEntry.setCreatureId(newSpawnCreatureId);
				resetSpawnEdit();
				target.add(getParent().getParent());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getParent().getParent());
			}

			@Override
			public boolean isVisible() {
				return editingSpawn != -1;
			}
		});
		newSpawnForm.add(new AjaxButton("createSpawn") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				getSelected().getSpawns().add(new SpawnEntry(newSpawnCreatureId, newChance, newComment));
				resetSpawnEdit();
				target.add(getParent().getParent());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getParent().getParent());
			}

			@Override
			public boolean isVisible() {
				return creatingSpawn;
			}
		});
		// Characters
		WebMarkupContainer spawnsContainer = new WebMarkupContainer("spawnsContainer");
		spawnsContainer.setOutputMarkupId(true);
		spawnsContainer.add(new ListView<SpawnEntry>("spawns", spawnsModel) {
			@Override
			protected void populateItem(final ListItem<SpawnEntry> item) {
				final Link<String> viewLink = new Link<String>("viewSpawn") {
					@Override
					public void onClick() {
						PageParameters params = new PageParameters();
						params.add("id", item.getModelObject().getCreatureId());
						setResponsePage(CreaturePage.class, params);
					}
				};
				viewLink.add(new Label("spawnName", new Model<String>() {
					@Override
					public String getObject() {
						final Creature creature = creaturesDb.get(item.getModelObject().getCreatureId());
						return creature == null ? "<Missing creature " + item.getModelObject() + ">" : creature.getDisplayName();
					}
				}));
				item.add(viewLink);
				item.add(new AjaxLink<String>("removeSpawn") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						spawnsModel.getObject().remove(item.getModelObject());
						target.add(getParent().getParent().getParent());
					}
				});
				item.add(new AjaxLink<String>("editSpawn") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						newChance = item.getModelObject().getChance();
						newSpawnCreatureId = item.getModelObject().getCreatureId();
						newComment = item.getModelObject().getComment();
						editingSpawn = item.getIndex();
						creatingSpawn = false;
						target.add(getParent().getParent().getParent());
					}
				});
				item.add(new Label("spawnNumber", new Model<Integer>() {
					@Override
					public Integer getObject() {
						return item.getModelObject().getChance();
					}
				}));
				item.add(new Label("spawnComment", new Model<String>() {
					@Override
					public String getObject() {
						return item.getModelObject().getComment();
					}
				}));
			}
		}.setReuseItems(true));
		spawnsContainer.add(new TextField<Integer>("shares", new PropertyModel<Integer>(this, "shares"))
				.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)).setEnabled(false));
		spawnsContainer.add(newSpawnForm);
		// Form
		form.add(spawnsContainer);
		form.add(new CheckBoxMultipleChoice<Flag>("flags", Arrays.asList(Flag.values())));
		addFiles(form, Arrays.asList(getDatabase().getPackageFiles("SpawnPackages/SpawnPackageList.txt", "SpawnPackages")));
		form.add(new CheckBox("scriptCall"));
		form.add(new CheckBox("sequential"));
		form.add(new TextField<Integer>("uniquePoints", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("loyaltyRadius", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("spawnDelay", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("wanderRadius", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("divide", Integer.class) {
			@Override
			public boolean isEnabled() {
				return getSelected().getDivideOrient() != null;
			}
		}.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("divideShareThreshold", Integer.class) {
			@Override
			public boolean isEnabled() {
				return getSelected().getDivideOrient() != null;
			}
		}.add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new ListChoice<DivideOrient>("divideOrient", Arrays.asList(DivideOrient.values())) {
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
		}.setMaxRows(1).setNullValid(true).setRequired(false));
	}

	public int getShares() {
		int v = 0;
		for (SpawnEntry se : getSelected().getSpawns()) {
			v += se.getChance();
		}
		return v;
	}

	@Override
	protected boolean entityMatches(SpawnPackage object, SpawnPackage filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getPackage(), filter.getPackage())) {
			return false;
		}
		return true;
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(SpawnPackagesPage.class, "SpawnPackagesPage.css")));
		super.onRenderEntityHead(response);
	}

	@Override
	protected SpawnPackage createNewInstance() {
		return new SpawnPackage(getDatabase());
	}

	@Override
	public SpawnPackages getEntityDatabase() {
		return getDatabase().getSpawnPackages();
	}

	@Override
	protected void buildColumns(List<IColumn<SpawnPackage, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<SpawnPackage, String>(new ResourceModel("column.package"), "package",
				"package", "package"));
	}
}
