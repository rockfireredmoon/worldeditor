package org.icemoon.eartheternal.common;

import static org.icemoon.eartheternal.common.Util.isNotNullOrEmpty;
import static org.icemoon.eartheternal.common.Util.toBooleanString;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class ZoneDef extends AbstractMultiINIFileEntity<Long, IDatabase> {
	public enum TimeOfDay {
		Sunrise, Day, Sunset, Night
	}
	
	
	private String shardName;
	private boolean persist;
	private XYZ location = new XYZ();
	private String warpName;
	private String name;
	private String description;
	private String environmentType;
	private TimeOfDay timeOfDay;
	private String terrainConfig;
	private String mapName;
	private String regions;
	private boolean grove;
	private boolean audit;
	private int pageSize = Constants.DEFAULT_PAGESIZE;
	private boolean guildHall;
	private boolean instance;
	private boolean environmentCycle;
	private boolean arena;
	private int maxAggroRange = Constants.DEFAULT_MAXAGGRORANGE;
	private int maxLeashRange = Constants.DEFAULT_MAXLEASHRANGE;
	private PVPMode mode = PVPMode.PVE;
	private String dropRateProfile;
	private String areaEnvironment = "";
	private int minLevel;
	private int maxLevel;

	public ZoneDef() {
		this(null);
	}

	public ZoneDef(IDatabase database) {
		super(database);
	}

	public final int getMinLevel() {
		return minLevel;
	}

	public final void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public final int getMaxLevel() {
		return maxLevel;
	}

	public final void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public String getDescription() {
		return description;
	}

	public final String getDropRateProfile() {
		return dropRateProfile;
	}

	public String getEnvironmentType() {
		return environmentType;
	}

	public final XYZ getLocation() {
		return location;
	}

	public String getMapName() {
		return mapName;
	}

	public int getMaxAggroRange() {
		return maxAggroRange;
	}

	public int getMaxLeashRange() {
		return maxLeashRange;
	}

	public final PVPMode getMode() {
		return mode;
	}

	public String getName() {
		return name;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean getPersist() {
		return persist;
	}

	public String getRegions() {
		return regions;
	}

	public String getShardName() {
		return shardName;
	}

	public String getTerrainConfig() {
		return terrainConfig;
	}

	public final String getWarpName() {
		return warpName;
	}

	public boolean isArena() {
		return arena;
	}

	public boolean isAudit() {
		return audit;
	}

	public boolean isEnvironmentCycle() {
		return environmentCycle;
	}

	public boolean isGrove() {
		return grove;
	}

	public boolean isGuildHall() {
		return guildHall;
	}

	public boolean isInstance() {
		return instance;
	}

	public boolean isPersist() {
		return persist;
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("WarpName")) {
			warpName = value;
		} else if (name.equals("ShardName")) {
			setShardName(value);
		} else if (name.equals("DESC")) {
			description = value;
		} else if (name.equals("ID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equals("Persist")) {
			setPersist(Integer.parseInt(value) > 0);
		} else if (name.equals("DefLoc")) {
			location = new XYZ(value);
		} else if (name.equals("NAME")) {
			this.name = value;
		} else if (name.equals("ENVIRONMENTTYPE")) {
			environmentType = value;
		}else if (name.equals("TimeOfDay")) {
			timeOfDay = value.equals("") ? null : TimeOfDay.valueOf(value);
		} else if (name.equals("TERRAINCONFIG")) {
			terrainConfig = value;
		} else if (name.equals("MAPNAME")) {
			mapName = value;
		} else if (name.equals("REGIONS")) {
			regions = value;
		} else if (name.equals("Grove")) {
			grove = value.equals("1");
		} else if (name.equals("Audit")) {
			audit = value.equals("1");
		} else if (name.equals("Arena")) {
			arena = value.equals("1");
		} else if (name.equals("GuildHall")) {
			guildHall = value.equals("1");
		} else if (name.equals("Instance")) {
			instance = value.equals("1");
		} else if (name.equals("EnvironmentCycle")) {
			environmentCycle = value.equals("1");
		} else if (name.equals("PageSize")) {
			pageSize = Integer.parseInt(value);
		} else if (name.equals("MaxAggroRange")) {
			maxAggroRange = Integer.parseInt(value);
		} else if (name.equals("MaxLeashRange")) {
			maxLeashRange = Integer.parseInt(value);
		} else if (name.equals("Mode")) {
			mode = PVPMode.values()[Integer.parseInt(value)];
		} else if (name.equals("DropRateProfile")) {
			dropRateProfile = value;
		} else if (name.equals("MinLevel")) {
			minLevel = Integer.parseInt(value);
		} else if (name.equals("MaxLevel")) {
			maxLevel = Integer.parseInt(value);
		} else if (name.equals("AreaEnvironment")) {
			if (areaEnvironment == null)
				areaEnvironment = "";
			if (!areaEnvironment.equals(""))
				areaEnvironment += "\n";
			areaEnvironment += value;
		} else if (!name.equals("")) {
			Log.todo("Instance (" + getFile() + ")", "Unhandle property " + name + " = " + value);
		}
	}

	public void setArena(boolean arena) {
		this.arena = arena;
	}

	public void setAudit(boolean audit) {
		this.audit = audit;
	}

	public void setDesc(String description) {
		this.description = description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public final void setDropRateProfile(String dropRateProfile) {
		this.dropRateProfile = dropRateProfile;
	}

	public void setEnvironmentCycle(boolean environmentCycle) {
		this.environmentCycle = environmentCycle;
	}

	public void setEnvironmentType(String environmentType) {
		this.environmentType = environmentType;
	}

	public void setGrove(boolean grove) {
		this.grove = grove;
	}

	public void setGuildHall(boolean guildHall) {
		this.guildHall = guildHall;
	}

	public void setInstance(boolean instance) {
		this.instance = instance;
	}

	public final void setLocation(XYZ location) {
		this.location = location;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public void setMaxAggroRange(int maxAggroRange) {
		this.maxAggroRange = maxAggroRange;
	}

	public void setMaxLeashRange(int maxLeashRange) {
		this.maxLeashRange = maxLeashRange;
	}

	public final void setMode(PVPMode mode) {
		this.mode = mode;
	}

	public final TimeOfDay getTimeOfDay() {
		return timeOfDay;
	}

	public final void setTimeOfDay(TimeOfDay timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPersist(boolean persist) {
		this.persist = persist;
	}

	public void setRegions(String regions) {
		this.regions = regions;
	}

	public void setShardName(String shardName) {
		this.shardName = shardName;
	}

	public void setTerrainConfig(String terrainConfig) {
		this.terrainConfig = terrainConfig;
	}

	public final void setWarpName(String warpName) {
		this.warpName = warpName;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("ID=" + getEntityId());
		writer.println("NAME=" + name);
		if (StringUtils.isNotBlank(description))
			writer.println("DESC=" + Util.nonNull(description));
		if (isNotNullOrEmpty(terrainConfig)) {
			writer.println("TERRAINCONFIG=" + terrainConfig);
		}
		if (isNotNullOrEmpty(environmentType)) {
			writer.println("ENVIRONMENTTYPE=" + environmentType);
		}
		if (isNotNullOrEmpty(mapName)) {
			writer.println("MAPNAME=" + mapName);
		}
		if (isNotNullOrEmpty(regions)) {
			writer.println("REGIONS=" + regions);
		}
		if (StringUtils.isNotBlank(shardName))
			writer.println("ShardName=" + shardName);
		if (StringUtils.isNotBlank(warpName))
			writer.println("WarpName=" + warpName);
		if (mode != PVPMode.PVE) {
			writer.println("Mode=" + mode.ordinal());
		}
		if (persist) {
			writer.println("Persist=" + toBooleanString(persist));
		}
		if (grove) {
			writer.println("Grove=" + toBooleanString(grove));
		}
		if (arena) {
			writer.println("Arena=" + toBooleanString(arena));
		}
		if (instance) {
			writer.println("Instance=" + toBooleanString(instance));
		}
		if (guildHall) {
			writer.println("GuildHall=" + toBooleanString(guildHall));
		}
		if (maxAggroRange != Constants.DEFAULT_MAXAGGRORANGE) {
			writer.println("MaxAggroRange=" + maxAggroRange);
		}
		if (maxLeashRange != Constants.DEFAULT_MAXLEASHRANGE) {
			writer.println("MaxLeashRange=" + maxLeashRange);
		}
		writer.println("DefLoc=" + location.toString());
		if (audit) {
			writer.println("Audit=" + toBooleanString(audit));
		}
		if (pageSize != Constants.DEFAULT_PAGESIZE) {
			writer.println("PageSize=" + pageSize);
		}
		if (timeOfDay != null) {
			writer.println("TimeOfDay=" + timeOfDay.name());
		}
		if (environmentCycle) {
			writer.println("EnvironmentCycle=" + toBooleanString(environmentCycle));
		}
		if (dropRateProfile != null) {
			writer.println("DropRateProfile=" + dropRateProfile);
		}
		if (StringUtils.isNotBlank(areaEnvironment)) {
			for (String l : areaEnvironment.replace("\r", "").split("\n")) {
				writer.println("AreaEnvironment=" + l);
			}
		}
		if(minLevel > 0)
			writer.println("MinLevel=" + minLevel);
		if(maxLevel > 0 && maxLevel < 9999)
			writer.println("MaxLevel=" + maxLevel);
	}

	public enum PVPMode {
		PVE, PVP, PVP_ONLY, PVE_ONLY, SPECIAL_EVENT
	}
}
