package org.icemoon.worldeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.icemoon.eartheternal.common.Creature;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.MapDef;
import org.icemoon.eartheternal.common.MapPoint;
import org.icemoon.eartheternal.common.MapPointType;
import org.icemoon.eartheternal.common.MapUtil;
import org.icemoon.eartheternal.common.Quest;
import org.icemoon.eartheternal.common.Quests;
import org.icemoon.eartheternal.common.Sceneries;
import org.icemoon.eartheternal.common.Scenery;
import org.icemoon.eartheternal.common.SpawnEntry;
import org.icemoon.eartheternal.common.SpawnPackage;
import org.icemoon.eartheternal.common.ZoneDef;
import org.icemoon.worldeditor.components.MapPanel;

@SuppressWarnings("serial")
public class DynamicMapPage extends AbstractPage {
	private IModel<MapDef> mapDefModel = new Model<MapDef>();
	private ListModel<MapPoint> points = new ListModel<MapPoint>(new ArrayList<MapPoint>());
	private List<MapPointType> mapPointTypes = new ArrayList<MapPointType>();
	private MapPanel mapPanel;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		parseParameters();
		loadMaps();
		add(mapPanel = new MapPanel("mapPanel", mapDefModel, points));
		mapPanel.setOutputMarkupId(true);
		addOptions();
	}

	protected void parseParameters() {
		mapPointTypes.clear();
		if (Application.getApp().getUserData() != null && !getPageParameters().get("characterId").isNull()) {
			mapPointTypes.add(MapPointType.CHARACTERS);
		}
		if (!getPageParameters().get("instanceId").isNull()) {
			mapPointTypes.add(MapPointType.SPAWNS);
		}
		if (!getPageParameters().get("quests").isNull() || !getPageParameters().get("questId").isNull()) {
			mapPointTypes.add(MapPointType.QUEST_START_POINTS);
			mapPointTypes.add(MapPointType.QUEST_END_POINTS);
			mapPointTypes.add(MapPointType.QUEST_MARKERS);
		}
		// Individual types
		for (MapPointType t : MapPointType.values()) {
			if (!getPageParameters().get(t.name().toLowerCase()).isNull()) {
				if (!mapPointTypes.contains(t)) {
					mapPointTypes.add(t);
				}
			}
		}
		// Map objects
		final StringValue idParm = getPageParameters().get("id");
		if (!idParm.isNull()) {
			mapDefModel.setObject(getDatabase().getMapDefs().get(idParm.toString()));
		}
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
	}

	protected void addOptions() {
		Form<?> optionsForm = new Form<Object>("optionsForm");
		List<MapPointType> l = new ArrayList<MapPointType>(Arrays.asList(MapPointType.values()));
		if (Application.getApp().getUserData() == null)
			l.remove(MapPointType.CHARACTERS);
		optionsForm.add(new CheckBoxMultipleChoice<MapPointType>("mapPointTypes",
				new PropertyModel<List<MapPointType>>(this, "mapPointTypes"), l) {
		}.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				loadMaps();
				target.add(mapPanel);
			}
		}));
		add(optionsForm);
	}

	protected void loadMaps() {
		List<MapPoint> pointsObject = this.points.getObject();
		final PageParameters pageParameters = getPageParameters();
		pointsObject.clear();
		long started = System.currentTimeMillis();
		long questId = pageParameters.get("questId").toLong(-1);
		long characterId = pageParameters.get("characterId").toLong(-1);
		long instanceId = pageParameters.get("instanceId").toLong(-1);
		// Spawns
		if (mapPointTypes.contains(MapPointType.SPAWNS)) {
			if (instanceId == -1) {
				for (Sceneries<IDatabase> spawns : getDatabase().getWorld().values()) {
					addSceneries(spawns);
				}
			}
		}
		// Quests
		if (mapPointTypes.contains(MapPointType.QUEST_START_POINTS) || mapPointTypes.contains(MapPointType.QUEST_START_POINTS)
				|| mapPointTypes.contains(MapPointType.QUEST_END_POINTS) || mapPointTypes.contains(MapPointType.QUEST_MARKERS)) {
			final Quests quests = getDatabase().getQuests();
			if (questId == -1) {
				for (Quest quest : quests.values()) {
					addQuest(quest);
				}
			} else {
				addQuest(quests.get(questId));
			}
		}
		// Characters
		if (mapPointTypes.contains(MapPointType.CHARACTERS)) {
			if (characterId == -1) {
				for (GameCharacter character : Application.getApp().getUserData().getCharacters().values()) {
					pointsObject.addAll(MapUtil.getPointsForCharacter(getDatabase(), character, mapDefModel.getObject()));
				}
			}
		}
		System.out.println("Took " + (System.currentTimeMillis() - started) + "ms to get " + pointsObject.size() + " map points");
	}

	protected void addSceneries(Sceneries<IDatabase> spawns) {
		MapDef mapDef = mapDefModel.getObject();
		ZoneDef instance = getDatabase().getZoneDefs().get(spawns.getEntityId());
		List<MapPoint> p = new ArrayList<MapPoint>();
		if (mapDef.forInstance(instance)) {
			for (Scenery<IDatabase> spawn : spawns.values()) {
				if (spawn.onMap(mapDef) && StringUtils.isNotBlank(spawn.getSpawnPackage())) {
					SpawnPackage sp = getDatabase().getSpawnPackages().get(spawn.getSpawnPackage());
					if (sp != null) {
						for (SpawnEntry se : sp.getSpawns()) {
							Creature c = getDatabase().getCreatures().get(se.getCreatureId());
							String name = c == null ? sp.getPackage() : c.getDisplayName();
							p.add(new MapPoint(Arrays.asList(mapDef), MapUtil.SPAWN_SIZE, c, spawn.getP().toLocation().toXZPoint(),
									c.getIcon(), name));
						}
					}
				}
			}
		}
		this.points.getObject().addAll(MapUtil.space(MapUtil.filter(p, 3000), 2));
	}

	protected void addQuest(Quest quest) {
		List<MapPoint> pointsObject = this.points.getObject();
		pointsObject.addAll(MapUtil.getPointsForQuest(getDatabase(), quest, mapPointTypes.toArray(new MapPointType[0])));
	}

	@Override
	protected void onRenderHead(IHeaderResponse response) {
		super.onRenderHead(response);
		response.render(CssHeaderItem.forReference(new CssResourceReference(DynamicMapPage.class, "DynamicMapPage.css")));
	}
}
