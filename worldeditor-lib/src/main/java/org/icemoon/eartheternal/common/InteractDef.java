package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class InteractDef extends AbstractMultiINIFileEntity<String, IDatabase> {
	private Type type = Type.WARP;
	private Classification classification = Classification.INTERACT;
	private Long objectId;
	private Long zone;
	private Position warpToLocation = new Position();
	private Long instanceId;
	private String message;
	private boolean henge;
	private long cost;
	private int facing;
	private int useTime = Constants.DEFAULT_USE_TIME;
	private Long quest;
	private boolean questComplete;
	private String scriptFunction;

	public InteractDef() {
		this(null);
	}

	public InteractDef(IDatabase database) {
		super(database);
	}

	public final Classification getClassification() {
		return classification;
	}

	public long getCost() {
		return cost;
	}

	public int getFacing() {
		return facing;
	}

	public final Long getInstanceId() {
		return instanceId;
	}

	public final String getMessage() {
		return message;
	}

	public final Long getObjectId() {
		return objectId;
	}

	public Long getQuest() {
		return quest;
	}

	public final String getScriptFunction() {
		return scriptFunction;
	}

	public final Type getType() {
		return type;
	}

	public int getUseTime() {
		return useTime;
	}

	public final Position getWarpToLocation() {
		return warpToLocation;
	}

	public final Long getZone() {
		return zone;
	}

	public final boolean isHenge() {
		return henge;
	}

	public boolean isQuestComplete() {
		return questComplete;
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("Name")) {
			setEntityId(value);
		} else if (name.equals("Class")) {
			classification = Classification.valueOf(value.toUpperCase());
		} else if (name.equals("Type")) {
			type = Type.valueOf(value.toUpperCase());
		} else if (name.equals("ObjectID")) {
			objectId = Long.parseLong(value);
		} else if (name.equals("Zone")) {
			zone = Long.parseLong(value);
		} else if (name.equals("WarpTo")) {
			List<String> l = new ArrayList<String>(Arrays.asList(value.split(",")));
			if (l.size() == 4 || l.size() == 5) {
				if (l.get(0).equals("henge")) {
					l.remove(0);
					henge = true;
				}
				warpToLocation = new Position(Float.parseFloat(l.get(0)), Float.parseFloat(l.get(1)), Float.parseFloat(l.get(2)));
				instanceId = Long.parseLong(l.get(3));
			} else {
				Log.error("Interaction", "Unexpected WarpTo definition.");
			}
			// 4th is Instance ID to warp to
		} else if (name.equals("Message")) {
			message = value;
		} else if (name.equals("ScriptFunction")) {
			scriptFunction = value;
		} else if (name.equals("Facing")) {
			facing = Integer.parseInt(value);
		} else if (name.equals("UseTime")) {
			useTime = Integer.parseInt(value);
		} else if (name.equals("Quest")) {
			String[] a = value.split(",");
			quest = Long.parseLong(a[0]);
			if (a.length > 1) {
				questComplete = a[1].equals("1");
			}
		} else if (name.equals("Cost")) {
			cost = Integer.parseInt(value);
		} else if (!name.equals("")) {
			Log.todo("Zone", "Unhandled property " + name + " = " + value);
		}
	}

	public final void setClassification(Classification classification) {
		this.classification = classification;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}

	public void setFacing(int facing) {
		this.facing = facing;
	}

	public final void setHenge(boolean henge) {
		this.henge = henge;
	}

	public final void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public final void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public void setQuest(Long quest) {
		this.quest = quest;
	}

	public void setQuestComplete(boolean questComplete) {
		this.questComplete = questComplete;
	}

	public final void setScriptFunction(String scriptFunction) {
		this.scriptFunction = scriptFunction;
	}

	public final void setType(Type type) {
		this.type = type;
	}

	public void setUseTime(int useTime) {
		this.useTime = useTime;
	}

	public final void setWarpToLocation(Position warpToLocation) {
		this.warpToLocation = warpToLocation;
	}

	public final void setZone(Long zone) {
		this.zone = zone;
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("Name=" + getEntityId());
		if (!classification.equals(Classification.INTERACT))
			writer.println("Class=" + classification.name().toLowerCase());
		writer.println("Type=" + type.name().toLowerCase());
		if (objectId != null) {
			writer.println("ObjectID=" + objectId);
		}
		StringBuilder bui = new StringBuilder();
		bui.append(warpToLocation.toCompactString());
		bui.append(",");
		if (instanceId != null) {
			bui.append(instanceId);
		}
		if (zone != null) {
			writer.println("Zone=" + zone);
		}
		writer.println("WarpTo=" + bui.toString());
		if (Util.isNotNullOrEmpty(message)) {
			writer.println("Message=" + message);
		}
		if (Util.isNotNullOrEmpty(scriptFunction)) {
			writer.println("ScriptFunction=" + scriptFunction);
		}
		if (cost > 0) {
			writer.println("Cost=" + cost);
		}
		if (facing != 0) {
			writer.println("Facing=" + facing);
		}
		if (quest != null) {
			writer.println("Quest=" + quest + (questComplete ? ",1" : ""));
		}
		if (useTime != Constants.DEFAULT_USE_TIME) {
			writer.println("UseTime=" + useTime);
		}
	}

	public enum Classification {
		STONEHENGE, INTERACT;
		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}

	public enum Type {
		HENGE, WARP, LOCATIONRETURN, SCRIPT;
		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}
}
