package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Quat implements Serializable {

	private double x;
	private double y;
	private double z;
	private double w;

	public Quat() {
	}

	public Quat(double x, double y, double z, double w) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quat(String locationString) {
		parseString(locationString);
	}

	public final double getW() {
		return w;
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
		z = Double.parseDouble(t.nextToken().trim());
		y = Double.parseDouble(t.nextToken().trim());
		w = Double.parseDouble(t.nextToken().trim());
	}

	public void set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public final void setW(double w) {
		this.w = w;
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

	@Override
	public String toString() {
		return getX() + "," + getY() + "," + getZ() + "," + getW();
	}

	

	public String toCompactString() {
		return Util.compact(getX()) + "," + Util.compact(getY()) + "," + Util.compact(getZ()) + "," + Util.compact(getW());
	}

}
