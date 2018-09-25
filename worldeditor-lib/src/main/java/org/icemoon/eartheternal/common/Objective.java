package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Objective extends AbstractINIFileEntity<Integer, IDatabase> {
	private ObjectiveType type = ObjectiveType.NONE;
	private List<Double> data1 = new ArrayList<Double>();
	private List<Double> data2 = new ArrayList<Double>();
	private String description;
	private boolean complete = false;
	private Long creatureId;
	private Long itemId;
	private String completeText = "";
	private List<Location> markerLocations = new ArrayList<Location>();
	private String activateText;
	private Long activateTime;
	private Act act;

	public Objective() {
		this(null);
	}

	public Objective(IDatabase database) {
		super(database);
	}

	Objective(IDatabase database, int id, Act act) {
		super(database);
		this.act = act;
		setEntityId(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Objective other = (Objective) obj;
		if (act == null) {
			if (other.act != null)
				return false;
		} else if (!act.equals(other.act))
			return false;
		return true;
	}

	public Act getAct() {
		return act;
	}

	public final String getActivateText() {
		return activateText;
	}

	public final Long getActivateTime() {
		return activateTime;
	}

	public final String getCompleteText() {
		return completeText;
	}

	public final Long getCreatureId() {
		return creatureId;
	}

	public final List<Double> getData1() {
		return data1;
	}

	public final List<Long> getData1Long() {
		List<Long> l = new ArrayList<Long>(data1.size());
		for (Double d : data1) {
			l.add(d.longValue());
		}
		return l;
	}

	public final List<Double> getData2() {
		return data2;
	}

	public final List<Long> getData2Long() {
		List<Long> l = new ArrayList<Long>(data2.size());
		for (Double d : data2) {
			l.add(d.longValue());
		}
		return l;
	}

	public final String getDescription() {
		return description;
	}

	public final Long getItemId() {
		return itemId;
	}

	public final List<Location> getMarkerLocations() {
		return markerLocations;
	}

	public final ObjectiveType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((act == null) ? 0 : act.hashCode());
		result = prime * result + super.hashCode();
		return result;
	}

	public final boolean isComplete() {
		return complete;
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("type")) {
			type = ObjectiveType.valueOf(value.toUpperCase());
		} else if (name.equals("data1")) {
			try {
				data1 = Util.toDoubleList(value);
			} catch (Exception e) {
				Log.error(getArtifactName(), "Invalid data1 " + value, e);
				data1 = new ArrayList<Double>();
			}
		} else if (name.equals("data2")) {
			try {
				data2 = Util.toDoubleList(value);
			} catch (Exception e) {
				Log.error(getArtifactName(), "Invalid data2 " + value, e);
				data2 = new ArrayList<Double>();
			}
		} else if (name.equals("complete")) {
			complete = "1".equals(value);
		} else if (name.equals("description")) {
			description = value;
		} else if (name.equals("ActivateText")) {
			activateText = value;
		} else if (name.equals("ActivateTime")) {
			activateTime = Util.toLong(value, 0l);
		} else if (name.equals("completeText")) {
			completeText = value;
		} else if (name.equals("myCreatureDefID")) {
			creatureId = Util.toLong(value, null);
		} else if (name.equals("myItemID")) {
			try {
				itemId = Util.toLong(value, null);
			} catch (NumberFormatException nfe) {
				Log.error(getArtifactName(), "Invalid myItemId " + value, nfe);
			}
		} else if (name.equals("markerLocations")) {
			try {
				markerLocations = Util.toLocationList(value);
			} catch (NumberFormatException nfe) {
				Log.error(getArtifactName(), "Invalid markerLocations " + value, nfe);
			}
		} else {
			Log.todo("Objective", "Unhandled property " + name + " = " + value);
		}
	}

	public final void setAct(Act act) {
		this.act = act;
	}

	public final void setActivateText(String activateText) {
		this.activateText = activateText;
	}

	public final void setActivateTime(Long activateTime) {
		this.activateTime = activateTime;
	}

	public final void setComplete(boolean complete) {
		this.complete = complete;
	}

	public final void setCompleteText(String completeText) {
		this.completeText = completeText;
	}

	public final void setCreatureId(Long creatureId) {
		this.creatureId = creatureId;
	}

	public final void setData1(List<Double> data1) {
		this.data1 = data1;
	}

	public void setData1Long(List<Long> data1) {
		this.data1 = new ArrayList<Double>(data1.size());
		for (Long l : data1) {
			this.data1.add(l.doubleValue());
		}
	}

	public final void setData2(List<Double> data2) {
		this.data2 = data2;
	}

	public void setData2Long(List<Long> data2) {
		this.data2 = new ArrayList<Double>(data2.size());
		for (Long l : data2) {
			this.data2.add(l.doubleValue());
		}
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public final void setMarkerLocations(List<Location> markerLocations) {
		this.markerLocations = markerLocations;
	}

	public final void setType(ObjectiveType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return description;
	}

	@Override
	public void write(INIWriter writer) {
		String prefix = "Obj." + getEntityId() + ".";
		writer.println(prefix + "type=" + type.name().toLowerCase());
		writer.println(prefix + "data1=" + (data1.isEmpty() ? "0" : Util.toCompactNumberList(data1)));
		writer.println(prefix + "data2=" + (data2.isEmpty() ? "0" : Util.toCompactNumberList(data2)));
		writer.println(prefix + "description=" + description);
		if(complete)
			writer.println(prefix + "complete=" + Util.toBooleanString(complete));
		writer.println(prefix + "myCreatureDefID=" + (creatureId == null ? "" : creatureId));
		writer.println(prefix + "myItemID=" + (itemId == null ? "" : itemId));
		writer.println(prefix + "completeText=" + Util.nonNull(completeText));
		writer.println(prefix + "markerLocations=" + Util.toLocationList(markerLocations));
		// At the point "TALK", "NONE", "KILL", "TRAVEL" needs no more
		if (type.equals(ObjectiveType.ACTIVATE) || type.equals(ObjectiveType.GATHER)) {
			writer.println(prefix + "ActivateTime=" + activateTime);
			writer.println(prefix + "ActivateText=" + activateText);
		}
	}

	@Override
	protected void doLoad() throws IOException {
	}
}