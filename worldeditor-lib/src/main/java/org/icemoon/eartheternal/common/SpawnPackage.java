package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class SpawnPackage extends AbstractMultiINIFileEntity<String, IDatabase> {
	private List<SpawnEntry> spawns = new ArrayList<SpawnEntry>();
	private int shares;
	private List<Flag> flags = new ArrayList<Flag>();
	private String newPackageName;
	private String dialog;
	private long divide;
	private DivideOrient divideOrient;
	private int divideShareThreshold;
	private int loyaltyRadius;
	private int spawnDelay;
	private int wanderRadius;
	private boolean scriptCall;
	private boolean sequential;
	private List<PointOverride> pointOverrides = new ArrayList<PointOverride>();
	private int uniquePoints;

	public SpawnPackage() {
		this(null);
	}

	public SpawnPackage(IDatabase database) {
		super(database);
	}

	public SpawnPackage(IDatabase database, String file) {
		super(database);
		setFile(file);
	}

	public final List<Flag> getFlags() {
		return flags;
	}

	public boolean containsCreature(Creature modelObject) {
		for (SpawnEntry e : spawns) {
			if (e.getCreatureId().equals(modelObject.getEntityId())) {
				return true;
			}
		}
		return false;
	}

	public final String getDialog() {
		return dialog;
	}

	public final void setDialog(String dialog) {
		this.dialog = dialog;
	}

	public final long getDivide() {
		return divide;
	}

	public final DivideOrient getDivideOrient() {
		return divideOrient;
	}

	public final int getDivideShareThreshold() {
		return divideShareThreshold;
	}

	public final int getLoyaltyRadius() {
		return loyaltyRadius;
	}

	public String getNewPackageName() {
		return newPackageName;
	}

	public String getPackage() {
		return newPackageName == null ? (getFile() == null ? null : FilenameUtils.getBaseName(getFile())) : newPackageName;
	}

	public final List<PointOverride> getPointOverrides() {
		return pointOverrides;
	}

	public final int getShares() {
		return shares;
	}

	public final int getSpawnDelay() {
		return spawnDelay;
	}

	public final List<SpawnEntry> getSpawns() {
		return spawns;
	}

	public final int getUniquePoints() {
		return uniquePoints;
	}

	public final int getWanderRadius() {
		return wanderRadius;
	}

	public final boolean isScriptCall() {
		return scriptCall;
	}

	public final boolean isSequential() {
		return sequential;
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("Name")) {
			setEntityId(value);
		} else if (name.equals("Spawn")) {
			String[] l = value.split(";");
			String comment = null;
			if (l.length > 0) {
				if (l.length > 1)
					comment = l[1];
				String[] a = l[0].trim().split(",");
				SpawnEntry s = new SpawnEntry(Long.parseLong(a[0].trim()), Integer.parseInt(a[1].trim()), comment);
				spawns.add(s);
			}
		} else if (name.equals("Shares")) {
			shares = Integer.parseInt(value);
		} else if (name.equals("Divide")) {
			String[] l = value.split(",");
			divide = Long.parseLong(l[0].trim());
			divideOrient = DivideOrient.fromCode(Integer.parseInt(l[1].trim()));
			divideShareThreshold = Integer.parseInt(l[2]);
		} else if (name.equals("Flags")) {
			int fl = Integer.parseInt(value);
			for (Flag flag : Flag.values()) {
				if ((fl & flag.getVal()) != 0) {
					flags.add(flag);
				}
			}
		} else if (name.equals("loyaltyRadius")) {
			loyaltyRadius = Integer.parseInt(value);
		} else if (name.equals("dialog")) {
			dialog = value;
		} else if (name.equals("spawnDelay")) {
			spawnDelay = Integer.parseInt(value);
		} else if (name.equals("wanderRadius")) {
			wanderRadius = Integer.parseInt(value);
		} else if (name.equals("ScriptCall")) {
			scriptCall = "1".equals(value);
		} else if (name.equals("Sequential")) {
			scriptCall = "1".equals(value);
		} else if (name.equals("UniquePoints")) {
			uniquePoints = Integer.parseInt(value);
		} else if (name.equals("PointOverride")) {
			String[] a = value.split(",");
			pointOverrides.add(new PointOverride(Long.parseLong(a[0]), Long.parseLong(a[1])));
		} else if (!name.equals("")) {
			Log.todo("SpawnPackage", "Unhandled property " + name + " = " + value);
		}
	}

	public final void setDivide(long divide) {
		this.divide = divide;
	}

	public final void setDivideOrient(DivideOrient divideOrient) {
		this.divideOrient = divideOrient;
	}

	public final void setDivideShareThreshold(int divideShareThreshold) {
		this.divideShareThreshold = divideShareThreshold;
	}

	public final void setFlags(List<Flag> flags) {
		this.flags = flags;
	}

	public final void setLoyaltyRadius(int loyaltyRadius) {
		this.loyaltyRadius = loyaltyRadius;
	}

	public final void setNewPackageName(String newPackageName) {
		this.newPackageName = newPackageName;
	}

	public void setPackage(String newPackageName) {
		// Package name only gets set when creating - it cannot be changed as
		// it's derived from the file name
		this.newPackageName = newPackageName;
	}

	public final void setPointOverrides(List<PointOverride> pointOverrides) {
		this.pointOverrides = pointOverrides;
	}

	public final void setScriptCall(boolean scriptCall) {
		this.scriptCall = scriptCall;
	}

	public final void setSequential(boolean sequential) {
		this.sequential = sequential;
	}

	public final void setShares(int shares) {
		this.shares = shares;
	}

	public final void setSpawnDelay(int spawnDelay) {
		this.spawnDelay = spawnDelay;
	}

	public final void setSpawns(List<SpawnEntry> spawns) {
		this.spawns = spawns;
	}

	public final void setUniquePoints(int uniquePoints) {
		this.uniquePoints = uniquePoints;
	}

	public final void setWanderRadius(int wanderRadius) {
		this.wanderRadius = wanderRadius;
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("Name=" + getEntityId());
		if (shares > 0) {
			writer.println("Shares=" + shares);
		}
		int v = 0;
		for (Flag fl : flags) {
			v += fl.getVal();
		}
		if (v > 0) {
			writer.println("Flags=" + v);
		}
		for (SpawnEntry e : spawns) {
			if (e.getComment() == null)
				writer.println("Spawn=" + e.getCreatureId() + "," + e.getChance());
			else
				writer.println("Spawn=" + e.getCreatureId() + "," + e.getChance() + " ;" + e.getComment());
		}
		if (divideOrient != null) {
			writer.println("Divide=" + divide + "," + divideOrient.toCode() + "," + divideShareThreshold);
		}
		if (loyaltyRadius > 0)
			writer.println("loyaltyRadius=" + loyaltyRadius);
		if (spawnDelay > 0)
			writer.println("spawnDelay=" + spawnDelay);
		if (wanderRadius > 0)
			writer.println("wanderRadius=" + wanderRadius);
		if (StringUtils.isNotBlank(dialog))
			writer.println("dialog=" + dialog);
		if (scriptCall)
			writer.println("ScriptCall=1");
		if (sequential)
			writer.println("Sequential=1");
		for (PointOverride p : pointOverrides) {
			writer.println("PointOverride=" + p);
		}
		if (uniquePoints > 0)
			writer.println("UniquePoints=" + uniquePoints);
	}

	public enum DivideOrient {
		NORTH_TO_SOUTH, EAST_TO_WEST;
		public static DivideOrient fromCode(int code) {
			switch (code) {
			case 1:
				return DivideOrient.NORTH_TO_SOUTH;
			case 2:
				return DivideOrient.EAST_TO_WEST;
			default:
				throw new IllegalArgumentException("Invvalid divide orient code " + code);
			}
		}

		public int toCode() {
			switch (this) {
			case NORTH_TO_SOUTH:
				return 1;
			default:
				return 2;
			}
		}
	}

	public static class PointOverride {
		private long propId;
		private long creatureId;

		public PointOverride(long propId, long creatureId) {
			super();
			this.propId = propId;
			this.creatureId = creatureId;
		}

		public final long getCreatureId() {
			return creatureId;
		}

		public final long getPropId() {
			return propId;
		}

		public final void setCreatureId(long creatureId) {
			this.creatureId = creatureId;
		}

		public final void setPropId(long propId) {
			this.propId = propId;
		}

		@Override
		public String toString() {
			return propId + "," + creatureId;
		}
	}
}
