package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Position implements Serializable {

	private double x;
	private double y;
	private double z;

	public Position() {
	}

	public Position(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Position(String locationString) {
		parseString(locationString);
	}

	public Position(XYZ loc) {
		this(loc.getX(), loc.getY(), loc.getZ());
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

	public void parseString(String value) {
		StringTokenizer t = new StringTokenizer(value, ",");
		x = Double.parseDouble(t.nextToken().trim());
		y = Double.parseDouble(t.nextToken().trim());
		z = Double.parseDouble(t.nextToken().trim());
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

	public String toCompactString() {
		return Util.compact(getX()) + "," + Util.compact(getY()) + "," + Util.compact(getZ());
	}
	
	public XYZ toLocation() {
		return new XYZ((long) x, (long) y, (long) z);
	}

	@Override
	public String toString() {
		return getX() + "," + getY() + "," + getZ();
	}

	public Point toXZPoint() {
		return new Point(x, z);
	}
}
