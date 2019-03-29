package org.icemoon.worldeditor.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Location;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.eartheternal.common.Objective;
import org.icemoon.eartheternal.common.ObjectiveType;
import org.icemoon.eartheternal.common.Scenery;
import org.icemoon.eartheternal.common.XYZ;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.worldeditor.Application;
import org.icemoon.worldeditor.CreaturePage;
import org.icemoon.worldeditor.ItemsPage;
import org.icemoon.worldeditor.LowASCIIValidator;
import org.icemoon.worldeditor.SelectorPanel;
import org.icemoon.worldeditor.UnoptimizedDeepCopy;
import org.icemoon.worldeditor.components.CreatureChooser;
import org.icemoon.worldeditor.components.InternalForm;
import org.icemoon.worldeditor.components.LocationPanel;
import org.icemoon.worldeditor.components.LocationViewPanel;
import org.icemoon.worldeditor.components.XYZPanel;
import org.icemoon.worldeditor.model.CreaturesModel;
import org.icemoon.worldeditor.model.GameItemsModel;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 * Component to edit a single objective. The fields used will change depending
 * on the selected type.
 * 
 * @see Objective
 */
@SuppressWarnings("serial")
public class ObjectiveDialog extends FormComponentPanel<Objective> {
	private IModel<Long> newCreatures = new Model<Long>();
	private IModel<Location> newMarker = new Model<Location>(new Location());
	private List<Long> creatures = new ArrayList<Long>();
	private List<Location> markers = new ArrayList<Location>();
	private Dialog dialog;
	private IModel<String> dialogTitle;
	private FeedbackPanel feedback;
	private WebMarkupContainer markerContainer;
	private IModel<IDatabase> database;

	public ObjectiveDialog(final String id, IModel<String> dialogTitle, IModel<IDatabase> database) {
		super(id);
		this.database = database;
	}

	public ObjectiveDialog(final String id, IModel<String> dialogTitle, IModel<Objective> model,
			IModel<IDatabase> database) {
		super(id, model);
		this.database = database;
		this.dialogTitle = dialogTitle;
	}

	public final List<Location> getMarkers() {
		return markers;
	}

	public final void setMarkers(List<Location> markers) {
		this.markers = markers;
	}

	protected void onInitialize() {
		super.onInitialize();
		setType(Objective.class);
		add(dialog = new Dialog("objectiveDialogWidget") {
			@Override
			public void close(AjaxRequestTarget ajaxRequestTarget) {
				super.close(ajaxRequestTarget);
				onDialogClose(ajaxRequestTarget);
			}
		});
		dialog.setModal(true);
		dialog.setTitle(dialogTitle);
		dialog.setWidth(760);
		InternalForm<Objective> f = new InternalForm<Objective>("objectiveForm");
		f.setOutputMarkupId(true);
		dialog.add(f);
		addCreatures(f);
		addBasic(f);
		addMarker(f);
		addActivate(f);
		addTravel(f);
		f.add(new AjaxButton("saveObjective") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				final Objective objective = ObjectiveDialog.this.getModelObject();
				objective.setMarkerLocations(new ArrayList<Location>(markers));
				if (objective.getType().isActivateType()) {
					objective.setData1Long(new ArrayList<Long>(creatures));
				}
				objectiveSaved(target);
				dialog.close(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}
		});
		setVisibilityForState(f);
	}

	protected void onDialogClose(AjaxRequestTarget target) {
	}

	protected void objectiveSaved(AjaxRequestTarget target) {
	}

	protected void setVisibilityForState(final Form<?> f) {
		ObjectiveType type = getSelectedType();
		f.get("completeText").setVisibilityAllowed(type.isActivateType());
		f.get("creatureId").setVisibilityAllowed(ObjectiveType.TALK.equals(type));
		f.get("itemId").setVisibilityAllowed(type.isActivateType());
		f.get("quantity").setVisibilityAllowed(type.isActivateType());
		f.get("activateText")
				.setVisibilityAllowed(ObjectiveType.ACTIVATE.equals(type) || ObjectiveType.GATHER.equals(type));
		f.get("activateTime")
				.setVisibilityAllowed(ObjectiveType.ACTIVATE.equals(type) || ObjectiveType.GATHER.equals(type));
		f.get("distance").setVisibilityAllowed(ObjectiveType.TRAVEL.equals(type));
	}

	protected void addBasic(Form<?> f) {
		f.add(feedback = new FeedbackPanel("objectiveFeedback"));
		feedback.setOutputMarkupId(true);
		f.add(new TextArea<String>("description", new PropertyModel<String>(this, "modelObject.description"))
				.setRequired(true).setType(String.class).add(new LowASCIIValidator()).setOutputMarkupId(true));
		f.add(new CheckBox("complete", new PropertyModel<Boolean>(this, "modelObject.complete"))
				.setOutputMarkupId(true));
		DropDownChoice<ObjectiveType> type = new DropDownChoice<ObjectiveType>("type",
				new PropertyModel<ObjectiveType>(this, "modelObject.type"), Arrays.asList(ObjectiveType.values()));
		type.setOutputMarkupId(true);
		type.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				setVisibilityForState((Form<?>) getComponent().getParent());
				target.add(getComponent().getParent());
			}
		});
		f.add(type);
		f.add(new TextArea<String>("completeText", new PropertyModel<String>(this, "modelObject.completeText")) {
			@Override
			public boolean isRequired() {
				return isVisibleInHierarchy();
			}
		}.setType(String.class).add(new LowASCIIValidator()).setOutputMarkupId(true));
		f.add(new CreatureChooser("creatureId", new Model<String>("Creature To Talk To"), new Model<Long>() {

			public Long getObject() {
				Objective modelObject = ObjectiveDialog.this.getModelObject();
				if (ObjectiveType.TALK.equals(getSelectedType())) {
					return modelObject == null ? 0l : modelObject.getCreatureId();
				} else {
					// final List<Long> data2 = getModelObject().getData2Long();
					// return data2.isEmpty() ? null : data2.iterator().next();
					if (modelObject != null && modelObject.getAct() != null
							&& modelObject.getAct().getQuest() != null) {
						if (modelObject.getAct().getQuest().getEnderId() == null
								|| modelObject.getAct().getQuest().getEnderId() == 0)
							return getModelObject().getCreatureId();
						else
							return modelObject.getAct().getQuest().getEnderId();
					} else
						return 0l;
				}
			}

			public void setObject(Long creature) {
				Objective modelObject = ObjectiveDialog.this.getModelObject();
				if (ObjectiveType.TALK.equals(getSelectedType())) {
					modelObject.setCreatureId(creature);
					if (modelObject.getAct().getQuest().getEnderId() == null
							|| modelObject.getAct().getQuest().getEnderId() == 0)
						modelObject.getAct().getQuest().setEnderId(creature);
					// getMo
					// final List<Long> data2 = new ArrayList<Long>();
					// data2.add(creature);
					// getModelObject().setData2Long(data2);
				}
			}
		}, database) {
//			@Override
//			public boolean isEnabled() {
//				Objective modelObject = ObjectiveDialog.this.getModelObject();
//				return modelObject != null && modelObject.getAct() != null && modelObject.getAct().getQuest() != null
//						&& (modelObject.getAct().getQuest().getEnderId() == null
//								|| modelObject.getAct().getQuest().getEnderId() == 0);
//			}

			@Override
			public boolean isRequired() {
				return isVisibleInHierarchy();
			}

			@Override
			protected void onEntitySelected(AjaxRequestTarget target, Creature entity) {
				super.onEntitySelected(target, entity);
				checkMarkers(target, entity);
			}
		}.setShowLabel(true).setOutputMarkupId(true));
		f.add(new SelectorPanel<Long, GameItem, String, IDatabase>("itemId", new Model<String>("Given Item"),
				new GameItemsModel(database), "displayName", new PropertyModel<Long>(this, "modelObject.itemId"),
				GameItem.class, Long.class, ItemsPage.class).setOutputMarkupId(true));
	}

	protected void checkMarkers(AjaxRequestTarget target, Creature entity) {
		if (entity != null && markers.size() == 0) {
			List<Scenery<IDatabase>> l = MapUtil
					.getSpawnsForCreature(Application.getAppSession(getRequestCycle()).getDatabase(), entity);
			if (l.size() > 0) {
				markers.add(l.get(0).getLocation());
				info("Automatically added default marker positions. Adjust if required.");
				target.add(feedback);
				target.add(markerContainer);
			}
		}
	}

	protected void addActivate(final Form<?> f) {
		f.add(new TextField<Integer>("quantity", new Model<Integer>() {
			public void setObject(Integer object) {
				if (object != null) {
					final Objective objective = ObjectiveDialog.this.getModelObject();
					final ObjectiveType type = getSelectedType();
					if (objective != null && type.isActivateType()) {
						List<Long> data = new ArrayList<Long>();
						data.add(object.longValue());
						objective.setData2Long(data);
					}
				}
			}

			public Integer getObject() {
				final Objective objective = ObjectiveDialog.this.getModelObject();
				if (objective != null) {
					List<Long> data = objective.getData2Long();
					return data.isEmpty() ? null : data.get(0).intValue();
				}
				return null;
			}
		}, Integer.class) {
			@Override
			public boolean isRequired() {
				return isVisibleInHierarchy();
			}
		}.add(new RangeValidator<Integer>(1, 99) {
			@Override
			public boolean isEnabled(Component component) {
				final ObjectiveType type = getSelectedType();
				return type != null && type.isActivateType();
			}
		}).setOutputMarkupId(true));
		f.add(new TextArea<String>("activateText", new PropertyModel<String>(this, "modelObject.activateText")) {
			@Override
			public boolean isRequired() {
				return isVisibleInHierarchy();
			}
		}.add(new LowASCIIValidator()).setOutputMarkupId(true));
		f.add(new TextField<Long>("activateTime", new PropertyModel<Long>(this, "modelObject.activateTime"),
				Long.class) {
			@Override
			public boolean isRequired() {
				return isVisibleInHierarchy();
			}
		}.setOutputMarkupId(true));
	}

	protected void addTravel(final Form<?> f) {
		XYZPanel p = new XYZPanel("activateArea", new Model<XYZ>() {
			public void setObject(XYZ object) {
				if (object != null && ObjectiveType.TRAVEL.equals(getSelectedType())) {
					List<Long> data = new ArrayList<Long>();
					data.add(object.getX());
					data.add(object.getZ());
					data.add(object.getY());
					ObjectiveDialog.this.getModelObject().setData1Long(data);
				}
			}

			public XYZ getObject() {
				final Objective modelObject = ObjectiveDialog.this.getModelObject();
				if (modelObject == null) {
					return new XYZ();
				}
				List<Long> data = modelObject.getData1Long();
				Iterator<Long> it = data.iterator();
				return new XYZ(it.hasNext() ? it.next() : 0, it.hasNext() ? it.next() : 0,
						it.hasNext() ? it.next() : 0);
			}
		});
		p.setOutputMarkupId(true);
		f.add(p);
		f.add(new TextField<Integer>("distance", new Model<Integer>() {
			public void setObject(Integer object) {
				if (object != null) {
					final Objective objective = ObjectiveDialog.this.getModelObject();
					if (objective != null && ObjectiveType.TRAVEL.equals(getSelectedType())) {
						List<Long> data = new ArrayList<Long>();
						data.add(object.longValue());
						objective.setData2Long(data);
					}
				}
			}

			public Integer getObject() {
				final Objective objective = ObjectiveDialog.this.getModelObject();
				if (objective != null) {
					List<Long> data = objective.getData2Long();
					return data.isEmpty() ? null : data.get(0).intValue();
				}
				return null;
			}
		}, Integer.class) {
			@Override
			public boolean isRequired() {
				return ObjectiveType.TRAVEL.equals(getSelectedType());
			}
		}.add(new RangeValidator<Integer>(1, Integer.MAX_VALUE) {
			@Override
			public boolean isEnabled(Component component) {
				return ObjectiveType.TRAVEL.equals(getSelectedType());
			}
		}).setOutputMarkupId(true));
	}

	protected void addCreatures(Form<?> f) {
		ListView<Long> creaturesField = new ListView<Long>("creatures",
				new PropertyModel<List<Long>>(this, "creatures")) {
			@Override
			protected void populateItem(final ListItem<Long> item) {
				item.add(new AjaxLink<String>("removeCreature") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						final String javascript = "$(\"#creature" + item.getModelObject() + "\").fadeOut();";
						creatures.remove(item.getModelObject());
						target.appendJavaScript(javascript);
					}
				});
				final Link<String> link = new Link<String>("viewCreature") {
					@Override
					public void onClick() {
						setResponsePage(CreaturePage.class, new PageParameters().add("id", item.getModelObject()));
					}
				};
				link.add(new Label("creature", new Model<String>() {
					public String getObject() {
						Creature c = Application.getAppSession(getRequestCycle()).getDatabase().getCreatures()
								.get(item.getModelObject());
						return c == null ? "<Unknown " + item.getModelObject() + ">"
								: (c.getDisplayName() + " (Level " + c.getLevel() + ")");
					}
				}));
				item.add(link);
				item.setOutputMarkupId(true);
				item.setMarkupId("creature" + item.getModelObject());
			}
		};
		creaturesField.setReuseItems(false);
		creaturesField.setOutputMarkupId(true);
		f.add(new CreatureChooser("newCreature", new Model<String>("Creature"), newCreatures, database) {
			@Override
			protected void onEntitySelected(AjaxRequestTarget target, Creature entity) {
				super.onEntitySelected(target, entity);
				checkMarkers(target, entity);
				if (newCreatures.getObject() != null) {
					creatures.add(newCreatures.getObject());
					newCreatures.setObject(null);
				}
				target.add(getParent().get("creaturesContainer"));
				target.add(getParent().get("newCreature"));
			}
		}.setShowClear(false).setOutputMarkupId(true));
		WebMarkupContainer kc = new WebMarkupContainer("creaturesContainer") {
			@Override
			public boolean isVisible() {
				return getSelectedType() != null && getSelectedType().isActivateType();
			}
		};
		kc.setOutputMarkupId(true);
		kc.add(creaturesField);
		f.add(kc);
	}

	protected void addMarker(Form<?> f) {
		markerContainer = new WebMarkupContainer("markerContainer");
		final ListView<Location> markerPoints = new ListView<Location>("markerLocations",
				new PropertyModel<List<Location>>(this, "markers")) {
			@Override
			protected void populateItem(final ListItem<Location> item) {
				item.add(new Label("markerNumber",
						new Model<String>(String.valueOf((char) ('\u278A' + item.getIndex())))));
				item.add(new AjaxLink<String>("removeMarker") {
					@Override
					public void onClick(AjaxRequestTarget target) {
						final String javascript = "$(\"#marker" + item.getIndex() + "\").fadeOut();";
						markers.remove(item.getModelObject());
						target.appendJavaScript(javascript);
					}
				});
				item.add(new LocationViewPanel("markerLocation", new Model<Location>(item.getModelObject())));
				item.setOutputMarkupId(true);
				item.setMarkupId("marker" + item.getIndex());
			}
		};
		markerContainer.setOutputMarkupId(true);
		markerPoints.setReuseItems(false);
		markerContainer.add(markerPoints);
		f.add(markerContainer);
		final LocationPanel p = new LocationPanel("newMarker", newMarker, database) {
			@Override
			protected void onInstanceSelected(AjaxRequestTarget target, ZoneDef entity) {
				refresh(target);
			}
		};
		p.setAllowClear(false);
		p.setOutputMarkupId(true);
		f.add(p);
		f.add(new AjaxSubmitLink("submitNewMarker") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				form.process(this);
				Location loc = newMarker.getObject();
				if (loc != null) {
					if (loc.getInstance() == null) {
						error("You must select an instance.");
						target.add(feedback);
					} else {
						markers.add(new Location(loc));
						newMarker.setObject(new Location());
						target.add(markerContainer);
						target.add(p);
					}
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}
		}.setDefaultFormProcessing(false).setOutputMarkupId(true));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(
				CssHeaderItem.forReference(new PackageResourceReference(ObjectiveDialog.class, "ObjectiveDialog.css")));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onBeforeRender() {
		Objective act = getModelObject();
		creatures = act == null ? new ArrayList<Long>() : new ArrayList<Long>(act.getData1Long());
		markers = act == null ? new ArrayList<Location>()
				: (List<Location>) UnoptimizedDeepCopy.copy(act.getMarkerLocations());
		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		final Objective modelObject = getModelObject();
		if (modelObject != null) {
			if (modelObject.getType().isActivateType()) {
				modelObject.setData1Long(new ArrayList<Long>(creatures));
			}
			modelObject.setMarkerLocations(new ArrayList<Location>(markers));
		}
	}

	public ObjectiveType getSelectedType() {
		return getModelObject() == null ? ObjectiveType.NONE : getModelObject().getType();
	}

	public void clear() {
		markers.clear();
		creatures.clear();
		newMarker.setObject(null);
		newCreatures.setObject(null);
		((CreatureChooser) getActualForm().get("creatureId")).setModelObject(null);
	}

	private Form<?> getActualForm() {
		Dialog dialog = (Dialog) get("objectiveDialogWidget");
		return (Form<?>) dialog.get("objectiveForm");
	}

	public void open(AjaxRequestTarget target) {
		// It took a long time to track this one down. Without this clear, the
		// DropDownChoice
		// does not select the right value when the model changes and we refresh
		// via Ajax
		Form<?> f = getActualForm();
		f.clearInput();
		target.add(f);
		// target.add(dialog);
		// target.add(f.get("quantity"));
		// target.add(f.get("activateText"));
		// target.add(f.get("activateTime"));
		// target.add(f.get("objectiveFeedback"));
		// target.add(f.get("description"));
		// target.add(f.get("complete"));
		// target.add(f.get("type"));
		// target.add(f.get("completeText"));
		// target.add(f.get("creatureId"));
		// target.add(f.get("itemId"));
		// target.add(f.get("killsContainer"));
		// target.add(f.get("newKill"));
		// target.add(f.get("submitNewKill"));
		// target.add(f.get("markerContainer"));
		// target.add(f.get("newMarker"));
		// target.add(f.get("submitNewMarker"));
		// target.add(f.get("activateArea"));
		// target.add(f.get("distance"));
		final Objective modelObject = getModelObject();
		creatures.clear();
		if (modelObject != null && modelObject.getType().isActivateType()) {
			creatures.addAll((List<Long>) UnoptimizedDeepCopy.copy(modelObject.getData1Long()));
		}
		markers.clear();
		if (modelObject != null)
			markers.addAll((List<Location>) UnoptimizedDeepCopy.copy(modelObject.getMarkerLocations()));
		newMarker.setObject(null);
		newCreatures.setObject(null);
		setVisibilityForState(f);
		dialog.open(target);
	}
}