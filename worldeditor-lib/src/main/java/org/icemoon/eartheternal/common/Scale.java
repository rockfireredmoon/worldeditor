package org.icemoon.eartheternal.common;

import static org.icemoon.eartheternal.common.Util.format;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Scale implements Serializable {

	public double x;
	public double y;
	public double z;

	public Scale() {
	}

	public Scale(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Scale(String locationString) {
		parseString(locationString);
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public double getZ() {
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

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return format(getX()) + "," + format(getY()) + "," + format(getZ());
	}
}