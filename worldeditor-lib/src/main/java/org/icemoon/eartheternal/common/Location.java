package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Location implements Serializable {
	private double x;
	private double y;
	private double z;
	private Long instance;

	public Location(Location location) {
		x = location.x;
		y = location.y;
		z = location.z;
		instance = location.instance;
	}

	public Location() {
	}

	public Location(double x, double y, double z) {
		this(x, y, z, null);
	}

	public Location(double x, double y, double z, Long instance) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.instance = instance;
	}

	public Location(Position position, Long instance) {
		this(position.getX(), position.getY(), position.getZ(), instance);
	}

	public Location(String locationString) {
		parseString(locationString);
	}

	public final Long getInstance() {
		return instance;
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public final double getZ() {
		return z;
	}

	public List<MapDef> in(List<MapDef> mapDef) {
		List<MapDef> l = new ArrayList<MapDef>(mapDef);
		for (MapDef def : mapDef) {
			if (!def.containsLocation(this)) {
				l.remove(def);
			}
		}
		return l;
	}

	public void parseString(String value) {
		StringTokenizer t = new StringTokenizer(value, ",");
		x = Double.parseDouble(t.nextToken().trim());
		y = Double.parseDouble(t.nextToken().trim());
		z = Double.parseDouble(t.nextToken().trim());
		if (t.hasMoreTokens()) {
			instance = Long.parseLong(t.nextToken().trim());
		}
	}

	public final void setInstance(Long instance) {
		this.instance = instance;
	}

	public final void setX(double x) {
		this.x = x;
	}

	public final void setY(double y) {
		this.y = y;
	}

	public final void setZ(double z) {
		this.z = z;
	}

	public String toPartialLocation() {
		return getX() + "," + getY() + "," + getZ();
	}

	@Override
	public String toString() {
		return getX() + "," + getY() + "," + getZ() + "," + (getInstance() == null ? "0" : getInstance());
	}

	public XY toXZPoint() {
		return new XY((long) getX(), (long) getZ());
	}
}
