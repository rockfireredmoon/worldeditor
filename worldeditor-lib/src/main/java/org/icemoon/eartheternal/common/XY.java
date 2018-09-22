package org.icemoon.eartheternal.common;

import java.io.Serializable;

@SuppressWarnings("serial")
public class XY implements Serializable {

	public long x;
	public long y;
	
	public XY() {
	}

	public XY(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public XY(String p) {
		String[] a = p.split(",");
		x = Long.parseLong(a[0]);
		y = Long.parseLong(a[1]);
	}

	public XY(XY location) {
		x = location.x;
		y = location.y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XY other = (XY) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public final long getX() {
		return x;
	}

	public final long getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (x ^ (x >>> 32));
		result = prime * result + (int) (y ^ (y >>> 32));
		return result;
	}

	public final void setX(long x) {
		this.x = x;
	}

	public final void setY(long y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return x + ", " + y;
	}

}