package org.icemoon.worldeditor;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.validation.validator.RangeValidator;
import org.icemoon.eartheternal.common.AIScript;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Sceneries;
import org.icemoon.eartheternal.common.Scenery;
import org.icemoon.eartheternal.common.SpawnPackage;
import org.icemoon.eartheternal.common.Util;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.worldeditor.components.CreatureChooser;
import org.icemoon.worldeditor.components.PositionPanel;
import org.icemoon.worldeditor.components.TilePanel;
import org.icemoon.worldeditor.model.AIScriptsModel;
import org.icemoon.worldeditor.model.SpawnPackagesModel;
import org.icemoon.worldeditor.table.ClassedPropertyColumn;
import org.icemoon.worldeditor.table.TextFilteredClassedPropertyColumn;

@SuppressWarnings("serial")
public class SceneryPage extends AbstractEntityPage<Scenery<IDatabase>, Long, Long, Sceneries<IDatabase>, IDatabase> {
	public final static Character[] FLAGS = { 'F', 'H', 'N', 'A', 'E', 'M', 'R', 'P' };

	public enum SpawnType {
		PACKAGE, CREATURE
	}

	public SceneryPage() {
		super("entityId", Long.class);
	}

	@Override
	protected void buildForm(final Form<Scenery<IDatabase>> form) {
		final WebMarkupContainer wmc = new WebMarkupContainer("spawnContainer");
		wmc.setOutputMarkupId(true);
		wmc.add(new SelectorPanel<String, SpawnPackage, String, IDatabase>("spawnPackage", new Model<String>("Spawn Package"),
				new SpawnPackagesModel(this), "entityId", new PropertyModel<String>(this, "selected.spawnPackage"),
				SpawnPackage.class, String.class, SpawnPackagesPage.class) {
			@Override
			public boolean isVisible() {
				Scenery<IDatabase> s = getSelected();
				String sp = s == null ? "#" : s.getSpawnPackage();
				return sp == null || !sp.startsWith("#");
			}
		}.setShowClear(true).setShowLabel(true));
		ListView<Character> flags = new ListView<Character>("spawnFlags", Arrays.asList(FLAGS)) {
			@Override
			protected void populateItem(final ListItem<Character> item) {
				CheckBox cb = new CheckBox("spawnFlag", new Model<Boolean>() {
					@Override
					public Boolean getObject() {
						final Scenery<IDatabase> selected = getSelected();
						if (selected == null)
							return false;
						else {
							String sp = selected.getSpawnPackage();
							return sp != null
									&& sp.toLowerCase().contains(String.valueOf(Character.toLowerCase(item.getModelObject())));
						}
					}

					@Override
					public void setObject(Boolean object) {
						final Scenery<IDatabase> selected = getSelected();
						if (selected != null) {
							String sp = selected.getSpawnPackage();
							if (sp == null) {
								sp = "#";
							}
							if (object
									&& !sp.toLowerCase().contains(String.valueOf(Character.toLowerCase(item.getModelObject())))) {
								sp = "#" + item.getModelObject() + sp.substring(1);
							} else if (!object
									&& sp.toLowerCase().contains(String.valueOf(Character.toLowerCase(item.getModelObject())))) {
								int idx = sp.toLowerCase().indexOf(Character.toLowerCase(item.getModelObject()));
								sp = sp.substring(0, idx) + sp.substring(idx + 1);
							}
							selected.setSpawnPackage(sp);
						}
					}
				});
				Label l = new Label("spawnFlagLabel", new ResourceModel("spawnFlag." + item.getModelObject().toString()));
				item.add(cb);
				item.add(l);
			}
		};
		wmc.add(flags);
		wmc.add(new CreatureChooser("spawnCreature", new Model<String>("Spawn Creature"), new Model<Long>() {
			@Override
			public Long getObject() {
				Scenery<IDatabase> s = getSelected();
				String sp = s.getSpawnPackage();
				if (sp != null && sp.startsWith("#")) {
					try {
						return Long.parseLong(Util.numbersOnly(sp));
					} catch (NumberFormatException nfe) {
						return 0l;
					}
				}
				return 0l;
			}

			@Override
			public void setObject(Long object) {
				Scenery<IDatabase> s = getSelected();
				if (s != null) {
					String sp = s.getSpawnPackage();
					if (sp != null) {
						sp = Util.removeNumbers(sp);
					} else
						sp = "";
					if (object != null) {
						if (!sp.startsWith("#"))
							sp = "#" + sp;
						sp += String.valueOf(object);
					}
					s.setSpawnPackage(sp);
				}
			}
		}, new PropertyModel<IDatabase>(this, "database")) {
			@Override
			public boolean isVisible() {
				Scenery<IDatabase> s = getSelected();
				String sp = s == null ? "#" : s.getSpawnPackage();
				return sp != null && sp.startsWith("#");
			}

			@Override
			protected void onEntitySelected(AjaxRequestTarget target, Creature entity) {
				super.onEntitySelected(target, entity);
			}
		}.setShowClear(true).setShowLabel(true).setRequired(true));
		final WebMarkupContainer smc = new WebMarkupContainer("spawnOptions") {
			@Override
			public boolean isVisible() {
				Scenery<IDatabase> s = getSelected();
				return s != null && s.getAsset() != null && (s.getAsset().startsWith("Manipulator#Manipulator-SpawnPoint")
						|| s.getAsset().startsWith("Manipulator-SpawnPoint"));
			}
		};
		smc.add(new ListChoice<SpawnType>("spawnType", new PropertyModel<SpawnType>(this, "spawnType"),
				Arrays.asList(SpawnType.values())).setMaxRows(1).setRequired(false)
						.add(new AjaxFormComponentUpdatingBehavior("onchange") {
							@Override
							protected void onUpdate(AjaxRequestTarget target) {
								target.add(wmc);
							}
						}));
		//
		smc.add(wmc);
		smc.add(new TextField<String>("spawnName", String.class));
		smc.add(new TextField<String>("spawnLayer", String.class));
		smc.add(new TextField<Integer>("mobTotal", Integer.class).setRequired(true));
		smc.add(new TextField<Integer>("maxActive", Integer.class).setRequired(true));
		smc.add(new TextField<Integer>("maxLeash", Integer.class).setRequired(true));
		smc.add(new TextField<Integer>("loyaltyRadius", Integer.class).setRequired(true));
		smc.add(new TextField<Integer>("wanderRadius", Integer.class).setRequired(true));
		smc.add(new TextField<Integer>("despawnTime", Integer.class).setRequired(true));
		smc.add(new CheckBox("sequential").setRequired(true));
		smc.add(new SelectorPanel<String, AIScript, String, IDatabase>("aiModule", new Model<String>("AI Script"),
				new AIScriptsModel(this), "entityId", AIScript.class, String.class, AIScriptsPage.class).setShowLabel(true)
						.setShowClear(true));
		form.add(new TilePanel("tile") {
			@Override
			public boolean isEnabled() {
				return !isEditing();
			}
		});
		form.add(new PositionPanel("p").setRequired(true));
		form.add(new TextField<Integer>("facing", Integer.class).setRequired(true).add(new RangeValidator<Integer>(0, 359)));
		form.add(new TextField<String>("name", String.class));
		form.add(new TextField<String>("asset", String.class));
		form.add(smc);
		BookmarkablePageLink<String> zone = new BookmarkablePageLink<String>("zone", ZoneDefsPage.class) {
			@Override
			public PageParameters getPageParameters() {
				PageParameters parms = new PageParameters();
				parms.add("id", getSelected().getSceneries().getEntityId());
				return parms;
			}

			public boolean isVisible() {
				return editing && getSelected().getSceneries() != null;
			}
		};
		zone.add(new Label("zoneName", new Model<String>() {
			@Override
			public String getObject() {
				ZoneDef def = Application.getAppSession(getRequestCycle()).getDatabase().getZoneDefs()
						.get(getSelected().getSceneries().getEntityId());
				return def == null ? "Unknown" : def.toString();
			}
		}));
		form.add(zone);
	}

	public final SpawnType getSpawnType() {
		final Scenery<IDatabase> selected = getSelected();
		if (selected == null) {
			return SpawnType.PACKAGE;
		} else {
			String sp = selected.getSpawnPackage();
			if (StringUtils.isBlank(sp) || !selected.getSpawnPackage().startsWith("#"))
				return SpawnType.PACKAGE;
			else
				return SpawnType.CREATURE;
		}
	}

	public final void setSpawnType(SpawnType spawnType) {
		if (spawnType == SpawnType.PACKAGE)
			getSelected().setSpawnPackage("");
		else
			getSelected().setSpawnPackage("#");
	}

	@Override
	protected void addIdField() {
		form.add(new Label("entityId") {
			@Override
			public boolean isVisible() {
				return isEditing();
			}
		});
	}

	@Override
	protected void onRenderEntityHead(IHeaderResponse response) {
		response.render(CssHeaderItem.forReference(new PackageResourceReference(SceneryPage.class, "SceneryPage.css")));
		super.onRenderEntityHead(response);
	}

	@Override
	protected Scenery<IDatabase> createNewInstance() {
		// TODO
		return new Scenery<IDatabase>(getDatabase(), null);
	}

	protected boolean entityMatches(Scenery<IDatabase> object, Scenery<IDatabase> filter) {
		if (!super.entityMatches(object, filter)) {
			return false;
		}
		if (Util.notMatches(object.getSpawnPackage(), filter.getSpawnPackage())) {
			return false;
		}
		return true;
	}

	@Override
	public Sceneries<IDatabase> getEntityDatabase() {
		final long instanceId = getPageParameters().get("zoneId").toLong();
		return getDatabase().getWorld().get(instanceId);
	}

	@Override
	protected void buildColumns(List<IColumn<Scenery<IDatabase>, String>> columns) {
		columns.add(new TextFilteredClassedPropertyColumn<Scenery<IDatabase>, String>(new ResourceModel("column.asset"), "asset",
				"asset", "asset"));
		columns.add(new TextFilteredClassedPropertyColumn<Scenery<IDatabase>, String>(new ResourceModel("column.spawnPackage"),
				"spawnPackage", "spawnPackage", "spawnPackage"));
		columns.add(new ClassedPropertyColumn<Scenery<IDatabase>>(new ResourceModel("column.tile"), "tile", "tile", "tile"));
	}
}
