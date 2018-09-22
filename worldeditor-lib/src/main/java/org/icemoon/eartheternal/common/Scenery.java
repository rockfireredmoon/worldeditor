package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class Scenery<R extends IRoot> extends AbstractMultiINIFileEntity<Long, R> {
	private Long instanceId;
	private Tile tile;
	private Long placedBy;
	private String asset;
	private String name;
	private Position p = new Position();
	private Quat q = new Quat(0, 0, 0, 1);
	private Position s = new Position(1, 1, 1);
	private int linkCount;
	private List<XY> links = new ArrayList<XY>();
	private long flags;
	private int layer;
	private int patrolSpeed;
	private String patrolEvent;
	private int facing;
	private Sceneries<R> sceneries;
	// Spawn point only
	private int maxLeash;
	private int leaseTime;
	private int mobTotal;
	private int outerRadius;
	private int innerRadius;
	private String aiModule;
	private boolean sequential;
	private int maxActive;
	private String spawnPackage;
	private String spawnName;
	private int loyaltyRadius;
	private int wanderRadius;
	private int despawnTime;
	private String spawnLayer;
	private String sceneryName;

	public Scenery() {
		this(null, null);
	}

	public Scenery(R database, Sceneries<R> sceneries) {
		super(database);
		this.sceneries = sceneries;
	}

	public final int getMaxLeash() {
		return maxLeash;
	}

	public final void setMaxLeash(int maxLeash) {
		this.maxLeash = maxLeash;
	}

	public final int getLeaseTime() {
		return leaseTime;
	}

	public final void setLeaseTime(int leaseTime) {
		this.leaseTime = leaseTime;
	}

	public final int getMobTotal() {
		return mobTotal;
	}

	public final void setMobTotal(int mobTotal) {
		this.mobTotal = mobTotal;
	}

	public final Sceneries<R> getSceneries() {
		return sceneries;
	}

	public final void setSceneries(Sceneries<R> sceneries) {
		this.sceneries = sceneries;
	}

	public final String getSpawnName() {
		return spawnName;
	}

	public final void setSpawnName(String spawnName) {
		this.spawnName = spawnName;
	}

	public final int getLoyaltyRadius() {
		return loyaltyRadius;
	}

	public final void setLoyaltyRadius(int loyaltyRadius) {
		this.loyaltyRadius = loyaltyRadius;
	}

	public final int getWanderRadius() {
		return wanderRadius;
	}

	public final void setWanderRadius(int wanderRadius) {
		this.wanderRadius = wanderRadius;
	}

	public final int getDespawnTime() {
		return despawnTime;
	}

	public final void setDespawnTime(int despawnTime) {
		this.despawnTime = despawnTime;
	}

	public final String getSpawnLayer() {
		return spawnLayer;
	}

	public final void setSpawnLayer(String spawnLayer) {
		this.spawnLayer = spawnLayer;
	}

	public final String getSceneryName() {
		return sceneryName;
	}

	public final void setSceneryName(String sceneryName) {
		this.sceneryName = sceneryName;
	}

	public final String getAiModule() {
		return aiModule;
	}

	public final void setAiModule(String aiModule) {
		this.aiModule = aiModule;
	}

	public final String getAsset() {
		return asset;
	}

	public final int getFacing() {
		return facing;
	}

	public final long getFlags() {
		return flags;
	}

	public final int getInnerRadius() {
		return innerRadius;
	}

	public Long getInstanceId() {
		return instanceId;
	}

	public final int getLayer() {
		return layer;
	}

	public int getLinkCount() {
		return linkCount;
	}

	public List<XY> getLinks() {
		return links;
	}

	public Location getLocation() {
		return new Location(p, instanceId);
	}

	public final int getMaxActive() {
		return maxActive;
	}

	public final String getName() {
		return name;
	}

	public final int getOuterRadius() {
		return outerRadius;
	}

	public Position getP() {
		return p;
	}

	public final String getPatrolEvent() {
		return patrolEvent;
	}

	public final int getPatrolSpeed() {
		return patrolSpeed;
	}

	public final Long getPlacedBy() {
		return placedBy;
	}

	public Quat getQ() {
		return q;
	}

	public Position getS() {
		return s;
	}

	public String getSpawnPackage() {
		return spawnPackage;
	}

	public Tile getTile() {
		return tile;
	}

	public final boolean isSequential() {
		return sequential;
	}

	public boolean onMap(MapDef def) {
		return p != null && def.getBounds().contains(p.toXZPoint());
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("ID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equals("Name")) {
			this.name = value;
		} else if (name.equals("PlacedBy")) {
			placedBy = Long.parseLong(value);
		} else if (name.equals("Asset")) {
			asset = value;
		} else if (name.equals("Facing")) {
			facing = Integer.parseInt(value);
		} else if (name.equals("spawnPackage")) {
			spawnPackage = value;
		} else if (name.equals("aiModule")) {
			aiModule = value;
		} else if (name.equals("links_count")) {
			// Not really needed
			linkCount = Integer.parseInt(value);
		} else if (name.equals("link")) {
			// Not really needed
			links.add(new XY(value));
		} else if (name.equals("Pos")) {
			p = new Position(value);
		} else if (name.equals("PX")) {
			p.setX(Double.parseDouble(value));
		} else if (name.equals("PY")) {
			p.setY(Double.parseDouble(value));
		} else if (name.equals("PZ")) {
			p.setZ(Double.parseDouble(value));
		} else if (name.equals("SX")) {
			s.setX(Double.parseDouble(value));
		} else if (name.equals("SY")) {
			s.setY(Double.parseDouble(value));
		} else if (name.equals("SZ")) {
			s.setZ(Double.parseDouble(value));
		} else if (name.equals("QX")) {
			q.setX(Double.parseDouble(value));
		} else if (name.equals("QY")) {
			q.setY(Double.parseDouble(value));
		} else if (name.equals("QZ")) {
			q.setZ(Double.parseDouble(value));
		} else if (name.equals("QW")) {
			q.setW(Double.parseDouble(value));
		} else if (name.equals("Orient")) {
			q = new Quat(value);
		} else if (name.equals("Scale")) {
			s = new Position(value);
		} else if (name.equals("Flags")) {
			flags = Integer.parseInt(value);
		} else if (name.equals("Layer")) {
			layer = Integer.parseInt(value);
		} else if (name.equals("patrolSpeed")) {
			patrolSpeed = Integer.parseInt(value);
		} else if (name.equals("patrolEvent")) {
			patrolEvent = value;
		} else if (name.equals("sequential")) {
			sequential = "1".equals(value);
		} else if (name.equals("maxActive")) {
			maxActive = Integer.parseInt(value);
		} else if (name.equals("innerRadius")) {
			innerRadius = Integer.parseInt(value);
		} else if (name.equals("outerRadius")) {
			outerRadius = Integer.parseInt(value);
		} else if (name.equals("spawnName")) {
			spawnName = value;
		} else if (name.equals("loyaltyRadius")) {
			loyaltyRadius = Integer.parseInt(value);
		} else if (name.equals("wanderRadius")) {
			wanderRadius = Integer.parseInt(value);
		} else if (name.equals("despawnTime")) {
			despawnTime = Integer.parseInt(value);
		} else if (name.equals("mobTotal")) {
			mobTotal = Integer.parseInt(value);
		} else if (name.equals("spawnLayer")) {
			spawnLayer = value;
		} else if (name.equals("sceneryName")) {
			sceneryName = value;
		} else if (name.equals("leaseTime")) {
			leaseTime = Integer.parseInt(value);
		} else if (name.equals("maxLeash")) {
			maxLeash = Integer.parseInt(value);
		} else {
			Log.todo("Scenery", "Unhandled property " + name + " = " + value);
		}
	}

	public final void setAsset(String asset) {
		this.asset = asset;
	}

	public final void setFacing(int facing) {
		this.facing = facing;
	}

	public final void setFlags(long flags) {
		this.flags = flags;
	}

	public final void setInnerRadius(int innerRadius) {
		this.innerRadius = innerRadius;
	}

	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public final void setLayer(int layer) {
		this.layer = layer;
	}

	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}

	public void setLinks(List<XY> links) {
		this.links = links;
	}

	public final void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setOuterRadius(int outerRadius) {
		this.outerRadius = outerRadius;
	}

	public void setP(Position p) {
		this.p = p;
	}

	public final void setPatrolEvent(String patrolEvent) {
		this.patrolEvent = patrolEvent;
	}

	public final void setPatrolSpeed(int patrolSpeed) {
		this.patrolSpeed = patrolSpeed;
	}

	public final void setPlacedBy(Long placedBy) {
		this.placedBy = placedBy;
	}

	public void setQ(Quat q) {
		this.q = q;
	}

	public void setS(Position s) {
		this.s = s;
	}

	public final void setSequential(boolean sequential) {
		this.sequential = sequential;
	}

	public void setSpawnPackage(String spawnPackage) {
		this.spawnPackage = spawnPackage;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	@Override
	public String toString() {
		return (StringUtils.isBlank(getName()) ? String.valueOf(getEntityId()) : getName()) + "@"
				+ (sceneries == null ? "?" : sceneries.getEntityId());
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("ID=" + getEntityId());
		if (placedBy != null)
			writer.println("PlacedBy=" + placedBy);
		writer.println("Asset=" + asset);
		if (StringUtils.isNotBlank(name))
			writer.println("Name=" + name);
		writer.println("Pos=" + p.toCompactString());
		writer.println("Orient=" + q.toCompactString());
		writer.println("Scale=" + s.toCompactString());
		if (facing > 0)
			writer.println("Facing=" + facing);
		if (flags > 0)
			writer.println("Flags=" + flags);
		if (layer > 0)
			writer.println("Layer=" + layer);
		if (patrolSpeed > 0)
			writer.println("patrolSpeed=" + patrolSpeed);
		if (StringUtils.isNotBlank(patrolEvent))
			writer.println("patrolEvent=" + patrolEvent);
		if (StringUtils.isNotBlank(aiModule))
			writer.println("aiModule=" + aiModule);
		if (maxActive > 0)
			writer.println("maxActive=" + maxActive);
		if (sequential)
			writer.println("sequential=1");
		if (outerRadius > 0)
			writer.println("outerRadius=" + outerRadius);
		if (innerRadius > 0)
			writer.println("innerRadius=" + innerRadius);
		if (!links.isEmpty()) {
			writer.println("links_count=" + links.size());
			for (XY l : links) {
				writer.println("link=" + l);
			}
		}
		if (StringUtils.isNotBlank(spawnName))
			writer.println("spawnName=" + spawnName);
		if (StringUtils.isNotBlank(spawnLayer))
			writer.println("spawnLayer=" + spawnLayer);
		if (StringUtils.isNotBlank(sceneryName))
			writer.println("sceneryName=" + sceneryName);
		if (loyaltyRadius > 0)
			writer.println("loyaltyRadius=" + loyaltyRadius);
		if (wanderRadius > 0)
			writer.println("wanderRadius=" + wanderRadius);
		if (loyaltyRadius > 0)
			writer.println("despawnTime=" + despawnTime);
		if (mobTotal > 0)
			writer.println("mobTotal=" + mobTotal);
		if (maxLeash > 0)
			writer.println("maxLeash=" + maxLeash);
		if (leaseTime > 0)
			writer.println("leaseTime=" + leaseTime);
		if (!Util.isNullOrEmpty(spawnPackage) && !spawnPackage.equals("#")) {
			writer.println("spawnPackage=" + spawnPackage);
		}
	}

	@Override
	protected void onSetFile(String file) {
		final String path = FilenameUtils.getPathNoEndSeparator(file);
		final String baseName = FilenameUtils.getBaseName(path);
		instanceId = Long.parseLong(baseName);
		tile = Tile.fromFileName(FilenameUtils.getBaseName(file));
	}
}
