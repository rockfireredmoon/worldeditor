package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.AbstractEntity;
import org.icemoon.eartheternal.common.Act;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.Entity;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Location;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.eartheternal.common.Objective;
import org.icemoon.eartheternal.common.ObjectiveType;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.eartheternal.common.Quests;
import org.icemoon.eartheternal.common.Reward;
import org.icemoon.eartheternal.common.Scenery;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.worldeditor.components.CoinPanel;
import org.icemoon.worldeditor.components.CreatureChooser;
import org.icemoon.worldeditor.components.LocationPanel;
import org.icemoon.worldeditor.dialogs.ActDialog;
import org.icemoon.worldeditor.dialogs.ChooseLocationDialog;
import org.icemoon.worldeditor.dialogs.ObjectiveDialog;
import org.icemoon.worldeditor.model.LinkedHashSetState;
import org.icemoon.worldeditor.model.QuestsModel;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;
import org.odlabs.wiquery.ui.widget.WidgetJavaScriptResourceReference;

@SuppressWarnings("serial")
public class QuestsPage extends AbstractEntityPage<Quest, Long, String, Quests, IDatabase> {
	private Button removeActOrObjective;
	private Button editActOrObjective;
	private Button newObjective;
	private Button newAct;
	private WebMarkupContainer toolbar;
	private QuestTree tree;
	private Act editingAct;
	private Objective editingObjective;
	private ChooseLocationDialog<IDatabase> autoFillGiverDialog;
	private FormComponent<Location> giverLocation;
	private ChooseLocationDialog<IDatabase> autoFillEnderDialog;
	private FormComponent<Location> enderLocation;
	private FormComponent<Long> enderId;
	private FormComponent<Long> giverId;
	private Button moveDown;
	private Button moveUp;

	public QuestsPage() {
		super("title", Long.class);
	}

	@Override
	protected void onNew() {
		super.onNew();
		editingAct = null;
		editingObjective = null;
	}

	protected void afterSave(Quest entity) {
		editingObjective = null;
		editingAct = null;
		((ObjectiveDialog) get("form").get("objectiveDialog")).clear();
	}

	@Override
	protected void selectModelObjectRow(Quest t, int selectedIndex) {
		super.selectModelObjectRow(t, selectedIndex);
		// Expand all acts
		for (Act act : t.getActs()) {
			tree.expand(act);
		}
		editingAct = null;
		editingObjective = null;
	}

	@Override
	protected void itemChanged(AjaxRequestTarget target) {
		super.itemChanged(target);
		tree.select(null, target);
	}

	@Override
	protected void buildForm(final Form<Quest> form) {
		addFiles(form, Arrays.asList(getDatabase().getPackageFiles("Packages/QuestPack.txt", null)));
		addIdType(form);
		// Confirm auto-fill of start/end character locations/instance
		final AutoFillFromCharacterModel autoFillFromCharacterModel = new AutoFillFromCharacterModel(
				new PropertyModel<Long>(this, "selected.giverId"), new IModel<IDatabase>() {
					@Override
					public IDatabase getObject() {
						return getDatabase();
					}

					@Override
					public void detach() {
					}

					@Override
					public void setObject(IDatabase object) {
					}
				});
		autoFillGiverDialog = new ChooseLocationDialog<IDatabase>("autoFillGiverDialog",
				new Model<String>("Autofill giver location"),
				new Model<String>("You can choose a spawn of this creature as the Quest Starter. Just Close if you want "
						+ "to enter your own location (not recommended). The Quest Ender will also be filled in with"
						+ "the same creature if it is not set."),
				autoFillFromCharacterModel) {
			@Override
			protected void onChoose(Scenery object, AjaxRequestTarget target) {
				final Quest selected = getSelected();
				selected.setGiverLocation(object.getLocation());
				if (selected.getEnderId() == null || selected.getEnderId() == 0) {
					selected.setEnderId(selected.getGiverId());
					target.add(enderId);
					;
					setupEnder(object, target);
				}
				target.add(giverLocation);
			}
		};
		autoFillGiverDialog.setOutputMarkupId(true);
		form.add(autoFillGiverDialog);
		autoFillEnderDialog = new ChooseLocationDialog<IDatabase>("autoFillEnderDialog", new Model<Scenery<IDatabase>>() {
		}, new Model<String>("Autofill ender location"),
				new Model<String>(
						"You can choose a spawn of this creature as the Quest Ender. Just Close if you want to enter your own location (not recommended)."),
				new AutoFillFromCharacterModel(new PropertyModel<Long>(this, "selected.enderId"), new IModel<IDatabase>() {
					@Override
					public IDatabase getObject() {
						return getDatabase();
					}

					@Override
					public void detach() {
					}

					@Override
					public void setObject(IDatabase object) {
					}
				})) {
			@Override
			protected void onChoose(Scenery<IDatabase> object, AjaxRequestTarget target) {
				setupEnder(object, target);
			}
		};
		autoFillEnderDialog.setOutputMarkupId(true);
		form.add(autoFillEnderDialog);
		form.add(new AjaxButton("view") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(QuestPage.class,
						new PageParameters().add("id", getSelected().getEntityId()).add("id", getSelected().getEntityId()));
			}

			@Override
			public boolean isEnabled() {
				return isEditing();
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		form.add(new TextField<String>("title").setRequired(true).add(new LowASCIIValidator(false, false)));
		form.add(new TextArea<String>("bodyText").setRequired(true).add(new LowASCIIValidator()));
		form.add(new TextArea<String>("completeText").add(new LowASCIIValidator()));
		form.add(new TextField<Integer>("level", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("suggestedLevel", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new TextField<Integer>("maxLevel", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new ListChoice<Integer>("partySize", Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 })).setMaxRows(1));
		form.add(giverId = new CreatureChooser("giverId", new Model<String>("Start Character"),
				new PropertyModel<Long>(this, "selected.giverId"), new PropertyModel<IDatabase>(this, "database")) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, Creature entity) {
				super.onEntitySelected(target, entity);
				if (!autoFillFromCharacterModel.getObject().isEmpty()) {
					autoFillGiverDialog.open(target);
				}
			}
		}.setShowClear(false).setShowLabel(true).setRequired(true));
		form.add(giverLocation = new LocationPanel("giverLocation", new PropertyModel<Location>(this, "selected.giverLocation"),
				new PropertyModel<IDatabase>(this, "database")).setRequired(true));
		form.add(enderId = new CreatureChooser("enderId", new Model<String>("End Character"),
				new PropertyModel<Long>(this, "selected.enderId"), new PropertyModel<IDatabase>(this, "database")) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, Creature entity) {
				super.onEntitySelected(target, entity);
				// If there is a final talk objective, this must be the same as
				// the ender
				for (Act a : QuestsPage.this.getSelected().getActs()) {
					if (!a.getObjectives().isEmpty()) {
						Objective o = a.getObjectives().get(a.getObjectives().size() - 1);
						if (o.getType().equals(ObjectiveType.TALK)) {
							o.setCreatureId(entity.getEntityId());
						}
					}
				}
				if (!autoFillFromCharacterModel.getObject().isEmpty()) {
					autoFillEnderDialog.open(target);
				}
			}
		}.setShowClear(false).setShowLabel(true).setRequired(true));
		enderId.setOutputMarkupId(true);
		form.add(new TextField<Long>("exp", Long.class).add(new RangeValidator<Long>(0l, Long.MAX_VALUE)).setRequired(true));
		form.add(new TextField<Integer>("numRewards", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE)));
		form.add(new CoinPanel("coin"));
		form.add(new TextField<Integer>("heroism", Integer.class).add(new RangeValidator<Integer>(0, Integer.MAX_VALUE))
				.setRequired(true));
		form.add(new AjaxSubmitLink("copyGiverToEnder") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				getFeedbackMessages().clear();
				info("Copied quest starter details to quest ender");
				form.process(this);
				Long giverIdVal = giverId.getConvertedInput();
				if (giverIdVal == null) {
					giverIdVal = getSelected().getGiverId();
				}
				giverLocation.processInput();
				Location giverLocVal = giverLocation.getConvertedInput();
				if (giverLocVal == null) {
					giverLocVal = getSelected().getGiverLocation();
				}
				getSelected().setGiverId(giverIdVal);
				getSelected().setGiverLocation(giverLocVal);
				getSelected().setEnderId(giverIdVal);
				getSelected().setEnderLocation(giverLocVal);
				// If there is a final talk objective, this must be the same as
				// the ender
				for (Act a : QuestsPage.this.getSelected().getActs()) {
					if (!a.getObjectives().isEmpty()) {
						Objective o = a.getObjectives().get(a.getObjectives().size() - 1);
						if (o.getType().equals(ObjectiveType.TALK)) {
							o.setCreatureId(giverIdVal);
							o.setMarkerLocations(new ArrayList<Location>(Arrays.asList(giverLocVal)));
						}
					}
				}
				target.add(form);
				target.add(getMainFeedbackPanel());
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getMainFeedbackPanel());
			}
		}.setDefaultFormProcessing(false));
		form.add(enderLocation = new LocationPanel("enderLocation", new PropertyModel<Location>(this, "selected.enderLocation"),
				new PropertyModel<IDatabase>(this, "database")).setRequired(true));
		form.add(new CheckBox("accountQuest"));
		form.add(new CheckBox("unabandon"));
		form.add(new CheckBox("repeat").add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(form.get("repeatDelayTimeUnit"));
				target.add(form.get("repeatDelayAmount"));
			}
		}));
		form.add(new RadioChoice<TimeUnit>("repeatDelayTimeUnit", Arrays.asList(TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS)) {
			@Override
			protected boolean localizeDisplayValues() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return form.getModelObject().isRepeat();
			}
		}.setNullValid(true).setEnabled(false));
		form.add(new TextField<Long>("repeatDelayAmount", Long.class) {
			@Override
			public boolean isEnabled() {
				return form.getModelObject().isRepeat();
			}
		}.add(new RangeValidator<Long>(0l, 59l)).setOutputMarkupId(true));
		form.add(new SelectorPanel<Long, Quest, String, IDatabase>("requires", new Model<String>("Required Quest"),
				new QuestsModel(this), "title", Quest.class, Long.class, QuestsPage.class).setShowLabel(true).setShowClear(true));
		// Leads to
		final ListView<Quest> leadsTo = new ListView<Quest>("leadsTo", new ListModel<Quest>() {
			public List<Quest> getObject() {
				final Quest modelObject = getSelected();
				if (modelObject == null) {
					return null;
				}
				final List<Quest> leadsTo2 = getDatabase().getQuests().getLeadsTo(modelObject);
				return leadsTo2;
			}
		}) {
			@Override
			protected void populateItem(final ListItem<Quest> item) {
				final Link<String> leadsToLink = new Link<String>("leadsToLink") {
					@Override
					public void onClick() {
						setResponsePage(QuestsPage.class, new PageParameters().add("id", item.getModelObject().getEntityId()));
					}
				};
				leadsToLink.add(new Label("leadsToLabel", new PropertyModel<String>(item.getModel(), "title")));
				item.add(leadsToLink);
			}
		};
		WebMarkupContainer leadsToContainer = new WebMarkupContainer("leadsToContainer") {
			public boolean isVisible() {
				return leadsTo.getModelObject().size() > 0;
			}
		};
		leadsToContainer.add(leadsTo);
		form.add(new AjaxButton("newLeadsTo") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				setResponsePage(QuestsPage.class, new PageParameters().add("from", getSelected().getEntityId()));
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}

			public boolean isEnabled() {
				return isEditing();
			}
		}.setDefaultFormProcessing(false));
		form.add(leadsToContainer);
		addObjectiveTree(form);
	}

	protected void setupEnder(Scenery object, AjaxRequestTarget target) {
		/*
		 * If there is a final talk objective, this must be the same as the
		 * ender so we should set its marker locations too
		 */
		for (Act a : QuestsPage.this.getSelected().getActs()) {
			if (!a.getObjectives().isEmpty()) {
				Objective o = a.getObjectives().get(a.getObjectives().size() - 1);
				if (o.getType().equals(ObjectiveType.TALK)) {
					o.setMarkerLocations(new ArrayList<Location>(Arrays.asList(object.getLocation())));
				}
			}
		}
		getSelected().setEnderLocation(object.getLocation());
		target.add(enderLocation);
	}

	protected void addObjectiveTree(Form<Quest> form) {
		final IModel<Set<AbstractEntity<? extends Object, ?>>> state = new LinkedHashSetState<AbstractEntity<? extends Object, ?>>();
		// Act Editor Dialog
		final ActDialog actDialog = new ActDialog("actDialog", new Model<String>("Act"), new PropertyModel<Act>(this, "editingAct"),
				new PropertyModel<IDatabase>(this, "database")) {
			protected void actSaved(AjaxRequestTarget target) {
				if (editingAct.getEntityId() == null) {
					final Integer nextActId = editingAct.getQuest().getNextActId();
					editingAct.setEntityId(nextActId);
					editingAct.getQuest().getActs().add(editingAct);
					target.add(tree);
				} else {
					tree.updateNode(editingAct, target);
				}
				editingAct = null;
			}
		};
		actDialog.setOutputMarkupId(true);
		form.add(actDialog);
		// Objectives dialog
		final ObjectiveDialog objectiveDialog = new ObjectiveDialog("objectiveDialog", new Model<String>("Objective"),
				new PropertyModel<Objective>(this, "editingObjective"), new PropertyModel<IDatabase>(this, "database")) {
			protected void objectiveSaved(AjaxRequestTarget target) {
				Act act = editingObjective.getAct();
				if (!act.getObjectives().contains(editingObjective)) {
					act.getObjectives().add(editingObjective);
					target.add(tree);
				} else {
					tree.updateNode(editingObjective, target);
				}
			}

			protected void onDialogClose(AjaxRequestTarget target) {
			}
		};
		objectiveDialog.setOutputMarkupId(true);
		objectiveDialog.setOutputMarkupPlaceholderTag(true);
		form.add(objectiveDialog);
		// Act tools
		toolbar = new WebMarkupContainer("actsActions");
		toolbar.setOutputMarkupId(true);
		form.add(toolbar);
		// New Act
		toolbar.add(newAct = new AjaxButton("newAct") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				editingAct = new Act(getDatabase(), getSelected());
				actDialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		newAct.setOutputMarkupId(true);
		// New Objective
		toolbar.add(newObjective = new AjaxButton("newObjective") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				editingAct = null;
				editingObjective = ((Act) tree.getSelected().getObject()).newObjective();
				objectiveDialog.open(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		newObjective.setOutputMarkupId(true);
		// Edit Act Or Objective
		toolbar.add(editActOrObjective = new AjaxButton("editActOrObjective") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				Object sel = tree.getSelected().getObject();
				if (sel instanceof Act) {
					actDialog.open(target);
				} else if (sel instanceof Objective) {
					objectiveDialog.open(target);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		editActOrObjective.setOutputMarkupId(true);
		// Remove Act Or Objective
		toolbar.add(removeActOrObjective = new AjaxButton("removeActOrObjective") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				AbstractEntity<? extends Object, ?> selected = tree.getSelected().getObject();
				if (selected instanceof Act) {
					getSelected().getActs().remove(selected);
				} else {
					((Objective) selected).getAct().getObjectives().remove(selected);
				}
				target.add(tree);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		toolbar.add(moveUp = new AjaxButton("moveUp") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				AbstractEntity<? extends Object, ?> selected = tree.getSelected().getObject();
				List<Act> acts = getSelected().getActs();
				if (selected instanceof Act) {
					final int indexOf = acts.indexOf(selected);
					if (indexOf > 0) {
						acts.remove(selected);
						acts.add(indexOf - 1, (Act) selected);
					}
				} else {
					final List<Objective> objectives = ((Objective) selected).getAct().getObjectives();
					final int objectiveIndex = objectives.indexOf(selected);
					if (objectiveIndex > 0) {
						objectives.remove(selected);
						objectives.add(objectiveIndex - 1, (Objective) selected);
					} else {
						final Act act = ((Objective) selected).getAct();
						int actIndex = acts.indexOf(act);
						if (actIndex > 0) {
							objectives.remove(objectiveIndex);
							final Act newActForObjective = acts.get(actIndex - 1);
							newActForObjective.getObjectives().add((Objective) selected);
						}
					}
				}
				target.add(tree);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		toolbar.add(moveDown = new AjaxButton("moveDown") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				AbstractEntity<? extends Object, ?> selected = tree.getSelected().getObject();
				final List<Act> acts = getSelected().getActs();
				if (selected instanceof Act) {
					final int indexOf = acts.indexOf(selected);
					if (indexOf < acts.size() - 1) {
						acts.remove(selected);
						acts.add(indexOf + 1, (Act) selected);
					}
				} else {
					final List<Objective> objectives = ((Objective) selected).getAct().getObjectives();
					final int objectiveIndex = objectives.indexOf(selected);
					if (objectiveIndex < objectives.size() - 1) {
						objectives.remove(selected);
						objectives.add(objectiveIndex + 1, (Objective) selected);
					} else {
						final Act act = ((Objective) selected).getAct();
						int actIndex = acts.indexOf(act);
						if (actIndex < acts.size() - 1) {
							objectives.remove(objectiveIndex);
							final Act newActForObjective = acts.get(actIndex + 1);
							newActForObjective.getObjectives().add(0, (Objective) selected);
						}
					}
				}
				target.add(tree);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
			}
		}.setDefaultFormProcessing(false));
		removeActOrObjective.setOutputMarkupId(true);
		// Acts / Objectives tree
		tree = new QuestTree("acts", new ActProvider(), state) {
			@Override
			protected void select(AbstractEntity<? extends Object, ?> foo, AjaxRequestTarget target) {
				super.select(foo, target);
				if (foo instanceof Act) {
					editingAct = (Act) foo;
					editingObjective = null;
					target.add(objectiveDialog);
				} else if (foo instanceof Objective) {
					editingObjective = (Objective) foo;
					editingAct = null;
					target.add(objectiveDialog);
				}
				setAvailable(foo, target);
			}

			private void setAvailable(Entity<? extends Object> foo, AjaxRequestTarget target) {
				newObjective.setEnabled(foo != null && foo instanceof Act);
				removeActOrObjective.setEnabled(foo != null);
				editActOrObjective.setEnabled(foo != null);
				if (target != null) {
					target.add(newObjective);
					target.add(removeActOrObjective);
					target.add(editActOrObjective);
				}
			}
		};
		tree.setOutputMarkupId(true);
		tree.select(null, null);
		WebMarkupContainer tc = new WebMarkupContainer("treeContainer");
		tc.add(tree);
		tc.setOutputMarkupId(true);
		form.add(tc);
	}

	protected void doCreateNew() {
		super.doCreateNew();
		final StringValue fromId = getPageParameters().get("from");
		if (!fromId.isNull()) {
			Quest q = getEntityDatabase().get(fromId.toLongObject());
			if (q != null) {
				Quest sel = getSelected();
				sel.setGiverId(q.getEnderId());
				sel.setGiverLocation(q.getEnderLocation());
				sel.setRequires(q.getEntityId());
				sel.setLevel(q.getLevel());
			}
		}
	}

	@Override
	protected boolean entityMatches(Quest object, Quest filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getTitle(), filter.getTitle())) {
			return false;
		}
		if (filter.getLevel() > 0 && object.getLevel() != filter.getLevel()) {
			return false;
		}
		return true;
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new CssResourceReference(QuestsPage.class, "QuestsPage.css")));
		response.render(CssHeaderItem.forReference(new CssResourceReference(QuestsPage.class, "jquery.qtip.css")));
		response.render(JavaScriptHeaderItem.forReference(WidgetJavaScriptResourceReference.get()));
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(QuestsPage.class, "jquery.qtip.js")));
	}

	@Override
	protected void additionalValidation() {
		int selectRewards = getSelected().getNumRewards();
		int numRewards = 0;
		int optionalRewards = 0;
		for (Act a : getSelected().getActs()) {
			for (Reward r : a.getRewards()) {
				numRewards++;
				if (!r.isRequired())
					optionalRewards++;
			}
		}
		if (numRewards > 0 && selectRewards < optionalRewards) {
			form.error(
					"The number of optional rewards (i.e. those not 'required' must be at least the 'Number of Rewards' value of the quest.");
		} else 
			if (selectRewards > 0 && numRewards == 0) {
			form.error("You have 'Number Of Rewards' set, but no rewards defined.");
		}
	}

	@Override
	protected Quest createNewInstance() {
		return new Quest(getDatabase());
	}

	@Override
	public Quests getEntityDatabase() {
		return getDatabase().getQuests();
	}

	@Override
	protected void buildColumns(List<IColumn<Quest, String>> columns) {
		columns.add(
				new TextFilteredClassedPropertyColumn<Quest, String>(new ResourceModel("colum.title"), "title", "title", "title"));
		columns.add(
				new TextFilteredClassedPropertyColumn<Quest, Integer>(new ResourceModel("colum.level"), "level", "level", "level"));
	}

	private final static class AutoFillFromCharacterModel extends ListModel<Scenery<IDatabase>> {
		private IModel<Long> idModel;
		private IModel<IDatabase> dbModel;

		public AutoFillFromCharacterModel(IModel<Long> idModel, IModel<IDatabase> dbModel) {
			this.idModel = idModel;
			this.dbModel = dbModel;
		}

		public List<Scenery<IDatabase>> getObject() {
			Long id = idModel.getObject();
			if (id == null) {
				return null;
			}
			final List<Scenery<IDatabase>> spawnsForCreature = MapUtil.getSpawnsForCreature(dbModel.getObject(),
					dbModel.getObject().getCreatures().get(id));
			return spawnsForCreature;
		}
	}

	private class QuestTree extends NestedTree<AbstractEntity<? extends Object, ?>> {
		private IModel<AbstractEntity<? extends Object, ?>> selected;

		private QuestTree(String id, ITreeProvider<AbstractEntity<? extends Object, ?>> provider,
				IModel<Set<AbstractEntity<? extends Object, ?>>> state) {
			super(id, provider, state);
		}

		protected void onDetach() {
			if (selected != null) {
				selected.detach();
			}
			super.onDetach();
		}

		@Override
		public void renderHead(IHeaderResponse response) {
			// response.render(CssHeaderItem.forReference(new HumanTheme()));
		}

		public IModel<AbstractEntity<? extends Object, ?>> getSelected() {
			return selected;
		}

		protected boolean isSelected(AbstractEntity<? extends Object, ?> foo) {
			IModel<AbstractEntity<? extends Object, ?>> model = getProvider().model(foo);
			try {
				return selected != null && selected.equals(model);
			} finally {
				model.detach();
			}
		}

		protected void select(AbstractEntity<? extends Object, ?> foo, final AjaxRequestTarget target) {
			if (selected != null) {
				updateNode(selected.getObject(), target);
				selected.detach();
				selected = null;
			}
			selected = getProvider().model(foo);
			updateNode(foo, target);
		}

		@Override
		protected Component newContentComponent(String id, IModel<AbstractEntity<? extends Object, ?>> model) {
			return new Folder<AbstractEntity<? extends Object, ?>>(id, this, model) {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean isClickable() {
					return true;
				}

				@Override
				protected void onClick(AjaxRequestTarget target) {
					QuestTree.this.select(getModelObject(), target);
				}

				@Override
				protected boolean isSelected() {
					// return
					return QuestTree.this.isSelected(getModelObject());
				}
			};
		}
	}

	class ActProvider implements ITreeProvider<AbstractEntity<? extends Object, ?>> {
		@Override
		public void detach() {
		}

		@Override
		public boolean hasChildren(AbstractEntity<? extends Object, ?> object) {
			if (object instanceof Act) {
				return true;
			}
			return false;
		}

		@Override
		public Iterator<? extends AbstractEntity<? extends Object, ?>> getChildren(AbstractEntity<? extends Object, ?> object) {
			if (object instanceof Act) {
				return ((Act) object).getObjectives().iterator();
			}
			return null;
		}

		@Override
		public IModel<AbstractEntity<? extends Object, ?>> model(AbstractEntity<? extends Object, ?> object) {
			return new Model<AbstractEntity<? extends Object, ?>>(object);
		}

		@Override
		public Iterator<? extends AbstractEntity<? extends Object, ?>> getRoots() {
			Quest quest = getSelected();
			return quest.getActs().iterator();
		}
	}
}
