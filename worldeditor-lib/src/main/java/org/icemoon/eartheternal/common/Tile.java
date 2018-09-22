package org.icemoon.eartheternal.common;

import java.io.File;
import java.io.Serializable;
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class Tile implements Serializable, Comparable<Tile> {
	public static Tile fromFileName(String filename) {
		if (filename.startsWith("x"))
			filename = filename.substring(1);
		StringTokenizer t = new StringTokenizer(filename, "y");
		return new Tile(Integer.parseInt(t.nextToken()), Integer.parseInt(t.nextToken()));
	}
	public int x;

	public int y;

	public Tile() {
	}

	public Tile(File file) {
		this(Integer.parseInt(file.getName().substring(1, 4)), Integer.parseInt(file.getName().substring(5, 8)));
	}

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Tile(String substring) {
		int idx = substring.indexOf(",");
		x = Integer.parseInt(substring.substring(0, idx));
		y = Integer.parseInt(substring.substring(idx + 1));
	}

	@Override
	public int compareTo(Tile o) {
		int i = Integer.valueOf(o.getX()).compareTo(Integer.valueOf(o.getX()));
		return i == 0 ? (Integer.valueOf(o.getY()).compareTo(Integer.valueOf(o.getY()))) : i;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public final int getX() {
		return x;
	}

	public final int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return String.format("x%03d y%03d", x, y);
	}
}