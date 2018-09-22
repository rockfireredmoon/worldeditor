package org.icemoon.eartheternal.common;

import static org.icemoon.eartheternal.common.Constants.TILE_HEIGHT;
import static org.icemoon.eartheternal.common.Constants.TILE_WIDTH;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MapUtil {
	public static final int SPAWN_SIZE = 12;
	public static final int CHARACTER_SIZE = 12;
	public static final int QUEST_SIZE = 12;
	public static final int QUEST_MARKER_SIZE = 12;

	public static List<MapPoint> filter(List<MapPoint> points, int density) {
		List<MapPoint> l = new ArrayList<MapPoint>();
		List<MapPosition> a = new ArrayList<MapPosition>();
		for (MapPoint p : points) {
			MapPosition pos = new MapPosition(p.getEntity(), p.getLocation(), density);
			if (!a.contains(pos)) {
				a.add(pos);
				l.add(p);
			}
		}
		// System.out.println(">>>>> Original:" + points.size() + " New: " +
		// l.size());
		return l;
	}

	public static List<MapDef> getMapDef(IDatabase db, Long instanceId) {
		ZoneDef instance = db.getZoneDefs().get(instanceId);
		if (instance == null) {
			Log.error("MapUtil", "No instance for spawns " + instanceId);
			return null;
		} else {
			return db.getMapDefs().getByPrimary("Maps-" + instance.getWarpName());
		}
	}

	public static MapImage getMapImage(MapDef mapDef, StaticDataDatabase db) {
		final MapImage img = mapDef == null || Util.isNullOrEmpty(mapDef.getImage()) ? null
				: db.getMapImages().get(mapDef.getImage());
		if (mapDef != null && img == null) {
			throw new IllegalStateException("Missing Map Image for " + mapDef);
		}
		return img;
	}

	public static List<MapDef> getMapsForAccount(IDatabase db, IUserData userData, Account account) {
		final MapDefs mapDb = db.getMapDefs();
		List<MapDef> l = new ArrayList<MapDef>();
		if (account != null) {
			for (MapDef def : mapDb.values()) {
				for (Long gameCharacter : account.getCharacters()) {
					GameCharacter gc = userData.getCharacters().get(gameCharacter);
					if (gc != null && gc.getZone() != null) {
						// Is the characters instance on this map?
						ZoneDef instance = db.getZoneDefs().get(gc.getZone());
						if (def.forInstance(instance) && gc.onMap(def)) {
							l.add(def);
							break;
						}
					}
				}
			}
		}
		return l;
	}

	public static List<MapDef> getMapsForLocation(IDatabase db, final Location giverLocation) {
		List<MapDef> l = new ArrayList<MapDef>();
		ZoneDef instance = giverLocation == null ? null : db.getZoneDefs().get(giverLocation.getInstance());
		if (instance != null) {
			for (MapDef def : db.getMapDefs().getByPrimary("Maps-" + instance.getWarpName())) {
				if (def.containsLocation(giverLocation) && !l.contains(def)) {
					l.add(def);
				}
			}
		}
		return l;
	}

	public static List<MapDef> getMapsForQuest(IDatabase db, Quest quest) {
		List<MapDef> l = new ArrayList<MapDef>();
		addIfNotContained(l, getMapsForLocation(db, quest.getGiverLocation()));
		addIfNotContained(l, getMapsForLocation(db, quest.getEnderLocation()));
		for (Act act : quest.getActs()) {
			for (Objective objective : act.getObjectives()) {
				for (Location m : objective.getMarkerLocations()) {
					addIfNotContained(l, getMapsForLocation(db, m));
				}
			}
		}
		return l;
	}

	public static List<MapPoint> getPointsForCharacter(IDatabase db, GameCharacter character) {
		List<MapPoint> pointsObject = new ArrayList<MapPoint>();
		for (MapDef def : db.getMapDefs().values()) {
			pointsObject.addAll(getPointsForCharacter(db, character, def));
		}
		return pointsObject;
	}

	public static List<MapPoint> getPointsForCharacter(IDatabase db, GameCharacter character, MapDef mapDef) {
		List<MapPoint> pointsObject = new ArrayList<MapPoint>();
		ZoneDef instance = db.getZoneDefs().get(character.getZone());
		if (mapDef.forInstance(instance) && character.onMap(mapDef)) {
			pointsObject.add(new MapPoint(Arrays.asList(mapDef), CHARACTER_SIZE, character, character.getLocation().toXZPoint(),
					character.getIcon(), character.getDisplayName()));
		}
		return pointsObject;
	}

	public static List<MapPoint> getPointsForCreature(IDatabase db, Creature modelObject, MapDef... maps) {
		modelObject.getCreatureCategory().getColor();
		List<String> spawnPackages = new ArrayList<String>();
		List<MapDef> mapList = Arrays.asList(maps);
		for (SpawnPackage sp : db.getSpawnPackages().values()) {
			if (sp.containsCreature(modelObject)) {
				spawnPackages.add(sp.getEntityId());
			}
		}
		String icon = modelObject.getIcon();
		List<MapPoint> l = new ArrayList<MapPoint>();
		for (Sceneries<IDatabase> sceneries : db.getWorld().values()) {
			final long instanceId = sceneries.getEntityId();
			List<MapDef> def = getMapDef(db, instanceId);
			if (def == null) {
				Log.error("MapUtil", "No map def for instance " + instanceId);
			} else {
				if (!mapList.isEmpty()) {
					for (Iterator<MapDef> it = def.iterator(); it.hasNext();) {
						if (!mapList.contains(it.next())) {
							it.remove();
						}
					}
				}
				for (Scenery<IDatabase> spawn : sceneries.values()) {
					if (spawnPackages.contains(spawn.getSpawnPackage())) {
						XY xzPoint = spawn.getP().toLocation().toXZPoint();
						MapPoint mp = new MapPoint(def, SPAWN_SIZE, modelObject, xzPoint, icon, modelObject.getDisplayName());
						l.add(mp);
					}
				}
			}
		}
		return l;
	}

	public static List<MapPoint> getPointsForMap(List<MapPoint> allPoints, MapDef selectedMap) {
		List<MapPoint> a = new ArrayList<MapPoint>(allPoints);
		for (Iterator<MapPoint> it = a.iterator(); it.hasNext();) {
			if (!it.next().getMaps().contains(selectedMap)) {
				it.remove();
			}
		}
		return a;
	}

	public static List<MapPoint> getPointsForQuest(IDatabase db, Quest quest, MapPointType... types) {
		List<MapPointType> mapPointTypes = Arrays.asList(types);
		List<MapPoint> pointsObject = new ArrayList<MapPoint>();
		if (mapPointTypes.size() == 0 || mapPointTypes.contains(MapPointType.QUEST_MARKERS)) {
			int actIdx = 1;
			for (Act act : quest.getActs()) {
				int objIdx = 1;
				for (Objective objective : act.getObjectives()) {
					for (Location location : objective.getMarkerLocations()) {
						List<MapDef> mapDef = getMapDef(db, location.getInstance());
						if (mapDef != null) {
							final List<MapDef> in = location.in(mapDef);
							if (!in.isEmpty()) {
								pointsObject.add(new MapPoint(in, QUEST_MARKER_SIZE, objective, location.toXZPoint(),
										"spawn-red.png", actIdx + "/" + objIdx + " - " + objective.getDescription()));
							}
						}
					}
					objIdx++;
				}
				actIdx++;
			}
		}
		Location enderLocation = quest.getEnderLocation();
		Location giverLocation = quest.getGiverLocation();
		if (mapPointTypes.size() == 0 || mapPointTypes.contains(MapPointType.QUEST_START_POINTS)) {
			List<MapDef> mapDef = giverLocation == null || giverLocation.getInstance() == null ? null
					: getMapDef(db, giverLocation.getInstance());
			if (mapDef != null) {
				final List<MapDef> in = giverLocation.in(mapDef);
				if (!in.isEmpty()) {
					if (giverLocation.equals(enderLocation)) {
						pointsObject.add(
								new MapPoint(in, QUEST_SIZE, quest, giverLocation.toXZPoint(), "questgiver.png", quest.getTitle()));
					} else {
						pointsObject.add(new MapPoint(in, QUEST_SIZE, quest, giverLocation.toXZPoint(), "questgiver.png",
								quest.getTitle() + " - Start"));
					}
				}
			}
		}
		if ((mapPointTypes.size() == 0 || mapPointTypes.contains(MapPointType.QUEST_END_POINTS))
				&& (giverLocation == null || !giverLocation.equals(enderLocation))) {
			List<MapDef> mapDef = enderLocation == null || enderLocation.getInstance() == null ? null
					: getMapDef(db, enderLocation.getInstance());
			if (mapDef != null) {
				final List<MapDef> in = enderLocation.in(mapDef);
				if (!in.isEmpty()) {
					pointsObject.add(new MapPoint(in, QUEST_SIZE, quest, enderLocation.toXZPoint(), "questgiver.png",
							quest.getTitle() + " - End"));
				}
			}
		}
		return pointsObject;
	}

	public static List<Scenery<IDatabase>> getSpawnsForCreature(IDatabase db, Creature modelObject) {
		if (modelObject == null) {
			return null;
		}
		List<String> spawnPackages = new ArrayList<String>();
		List<Scenery<IDatabase>> l = new ArrayList<Scenery<IDatabase>>();
		if(!System.getProperty("dbfe.enableSpawns", "false").equals("true"))
			return l;
		
		for (SpawnPackage sp : db.getSpawnPackages().values()) {
			if (sp.containsCreature(modelObject)) {
				spawnPackages.add(sp.getEntityId());
			}
		}
		final World<IDatabase> world = db.getWorld();
		final List<Sceneries<IDatabase>> values = world.values();
		for (Sceneries<IDatabase> spawns : values) {
			for (Scenery<IDatabase> spawn : spawns.values()) {
				String sp = spawn.getSpawnPackage();
				if (sp != null) {
					if (sp.startsWith("#")) {
						try {
							if (new Long(Util.numbersOnly(sp)).equals(modelObject.getEntityId())) {
								l.add(spawn);
							}
						} catch (NumberFormatException nfe) {
							// Ignore
						}
					} else if (spawnPackages.contains(sp)) {
						l.add(spawn);
					}
				}
			}
		}
		return l;
	}

	public static BufferedImage getTileImage(MapTileLoc loc, MapImage mim) throws IOException {
		// First load the actual map image
		// final String name = "/Maps/" + loc.getMap() + ".jpg";
		// URL url = loc.getClass().getResource(name);
		// if(url == null) {
		// Log.error("tile", "Could not find source image for tiling map " +
		// name);
		// return null;
		// }
		// BufferedImage img = ImageIO.read(url);
		BufferedImage img = mim.getImage();
		// Get the clip we want
		Rectangle clipBounds = new Rectangle(loc.getX() * TILE_WIDTH / loc.getZoom(), loc.getY() * TILE_HEIGHT / loc.getZoom(),
				TILE_WIDTH / loc.getZoom(), TILE_HEIGHT / loc.getZoom());
		// System.out.println("CLIP: " + clipBounds + " IMG: " + img.getWidth()
		// + " / " + img.getHeight());
		// Are we completely outside the image?
		if (clipBounds.x > img.getWidth() || clipBounds.y > img.getHeight()) {
			// System.out.println("DONE!");
			return null;
		}
		BufferedImage clip = new BufferedImage(TILE_WIDTH, TILE_HEIGHT, img.getType());
		Graphics2D g2 = (Graphics2D) clip.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, TILE_WIDTH, TILE_HEIGHT);
		g2.drawImage(img, 0, 0, TILE_WIDTH, TILE_HEIGHT, clipBounds.x, clipBounds.y, clipBounds.x + clipBounds.width,
				clipBounds.y + clipBounds.height, null);
		return clip;
	}

	public static List<MapDef> getUniqueMaps(List<MapPoint> allPoints) {
		List<MapDef> l = new ArrayList<MapDef>();
		for (MapPoint p : allPoints) {
			for (MapDef d : p.getMaps()) {
				if (!l.contains(d)) {
					l.add(d);
				}
			}
		}
		Collections.sort(l, new Comparator<MapDef>() {
			@Override
			public int compare(MapDef o1, MapDef o2) {
				// int o =
				// Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
				// return o == 0 ? o1.getEntityId().compareTo(o2.getEntityId())
				// : o;
				return Long.valueOf(o1.getBounds().getAreaSize()).compareTo(o2.getBounds().getAreaSize());
			}
		});
		return l;
	}

	public static List<MapPoint> space(List<MapPoint> allPoints, int spacing) {
		List<XY> l = new ArrayList<XY>();
		for (MapPoint p : allPoints) {
			XY np = new XY(p.getLocation());
			XY adjusted = null;
			for (int i = spacing; adjusted == null && i < spacing * 10; i += spacing) {
				for (int x = -i; adjusted == null && x < i; x += spacing) {
					for (int y = -i; adjusted == null && y < i; y += spacing) {
						XY z = new XY(np.x + x, np.y + y);
						if (!l.contains(z)) {
							adjusted = z;
						}
					}
				}
			}
			if (adjusted != null) {
				p.setLocation(adjusted);
			}
			l.add(p.getLocation());
		}
		return allPoints;
	}

	protected static void addIfNotContained(List<MapDef> l, Collection<MapDef> toAdd) {
		for (MapDef m : toAdd) {
			if (!l.contains(m)) {
				l.add(m);
			}
		}
	}

	public static class MapPosition {
		private long x;
		private long y;
		private AbstractEntity<?, ?> entity;

		MapPosition(AbstractEntity<?, ?> entity, XY xy, int density) {
			this.entity = entity;
			this.x = (xy.x / density) * density;
			this.y = (xy.y / density) * density;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MapPosition other = (MapPosition) obj;
			if (entity == null) {
				if (other.entity != null)
					return false;
			} else if (!entity.equals(other.entity))
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((entity == null) ? 0 : entity.hashCode());
			result = prime * result + (int) (x ^ (x >>> 32));
			result = prime * result + (int) (y ^ (y >>> 32));
			return result;
		}
	}
}
