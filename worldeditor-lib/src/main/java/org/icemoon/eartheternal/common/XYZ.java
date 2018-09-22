package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.StringTokenizer;

public class XYZ implements Serializable {

	private long x = 0;
	private long y = 0;
	private long z = 0;

	public XYZ() {
	}

	public XYZ(int x, int y, int z) {
		this((long) x, (long) y, (long) z);
	}

	public XYZ(long x, long y, long z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public XYZ(Long x, Long y, Long z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public XYZ(String locationString) {
		StringTokenizer t = new StringTokenizer(locationString, ",");
		x = Long.parseLong(t.nextToken().trim());
		y = Long.parseLong(t.nextToken().trim());
		z = Long.parseLong(t.nextToken().trim());
	}

	public final long getX() {
		return x;
	}

	public final long getY() {
		return y;
	}

	public final long getZ() {
		return z;
	}

	public final void setX(long x) {
		this.x = x;
	}

	public final void setY(long y) {
		this.y = y;
	}

	public final void setZ(long z) {
		this.z = z;
	}

	public Position toPosition() {
		return new Position(this);
	}

	@Override
	public String toString() {
		return x + "," + y + "," + z;
	}

	public XY toXZPoint() {
		return new XY(x, z);
	}

}
