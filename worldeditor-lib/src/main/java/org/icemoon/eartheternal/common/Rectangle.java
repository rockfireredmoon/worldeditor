package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.Iterator;
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class Rectangle implements Serializable {
	public XY topLeft = new XY();

	public XY bottomRight = new XY();

	public Rectangle() {
	}

	public Rectangle(long topLeftX, long topLeftY, long bottomRightX, long bottomRightY) {
		topLeft.x = topLeftX;
		topLeft.y = topLeftY;
		bottomRight.x = bottomRightX;
		bottomRight.y = bottomRightY;
	}

	public Rectangle(String value) {
		Iterator<Long> it = Util.toLongList(value).iterator();
		topLeft.x = it.next();
		topLeft.y = it.next();
		bottomRight.x = it.next();
		bottomRight.y = it.next();
	}
	public Rectangle(StringTokenizer t) {
		this(Long.parseLong(t.nextToken()), Long.parseLong(t.nextToken()), Long.parseLong(t.nextToken()), Long.parseLong(t.nextToken()));
	}

	public boolean contains(Location location) {
		return location.getX() >= topLeft.x && location.getX() <= bottomRight.x && location.getZ() >= topLeft.y
			&& location.getZ() <= bottomRight.y;
	}

	public boolean contains(Point location) {
		return location.x >= topLeft.x && location.x <= bottomRight.x && location.y >= topLeft.y && location.y <= bottomRight.y;
	}

	public boolean contains(XY location) {
		return location.x >= topLeft.x && location.x <= bottomRight.x && location.y >= topLeft.y && location.y <= bottomRight.y;
	}

	public long getAreaSize() {
		return (bottomRight.x - topLeft.x) * (bottomRight.y - topLeft.y);
	}

	@Override
	public String toString() {
		return "Rectangle [topLeft=" + topLeft + ", bottomRight=" + bottomRight + "]";
	}

	public String toValueString() {
		return topLeft.x + "," + topLeft.y + "," + bottomRight.x + "," + bottomRight.y;
	}
}