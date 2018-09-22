package org.icemoon.eartheternal.common;

public class MapTileLoc {
	private int zoom;
	private int x;
	private int y;
	private String map;

	public String getMap() {
		return map;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZoom() {
		return zoom;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	@Override
	public String toString() {
		return "[MapLoc zoom=" + getZoom() + " x=" + getX() + " y=" + getY() + "]";
	}
}