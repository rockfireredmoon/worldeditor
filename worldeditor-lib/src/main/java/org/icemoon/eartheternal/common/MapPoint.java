package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.List;

public class MapPoint implements Serializable {
	private AbstractEntity<?, ?> entity;
	private XY location;
	private String imageName;
	private String text;
	private int diameter;
	private List<MapDef> maps;

	public MapPoint(List<MapDef> maps, int diamter, AbstractEntity<?, ?> entity, XY location, String imageName, String text) {
		this.maps = maps;
		this.entity = entity;
		this.location = location;
		this.imageName = imageName;
		this.text = text;
		this.diameter = diamter;
	}
	
	
	public int getActualX(MapImage mapImage, MapDef mapDef, Viewport vp) {
		float sw = (float) vp.getWidth() / (float) mapImage.getWidth();
		return (int) ((getImageX(mapImage, mapDef) - vp.getX()) * sw * vp.getScale());
	}

	public int getActualY(MapImage mapImage, MapDef mapDef, Viewport vp) {
		float sh = (float) vp.getHeight() / (float) mapImage.getHeight();
		return (int) ((getImageY(mapImage, mapDef) - vp.getY()) * sh * vp.getScale());
	}
	

	public int getDiameter() {
		return diameter;
	}



	public final AbstractEntity<?, ?> getEntity() {
		return entity;
	}

	public final String getImageName() {
		return imageName;
	}

	public int getImageX(MapImage mapImage, MapDef mapDef) {
		return (int)(mapImage.getWidth() * getTranslatedX(mapDef));
	}

	public int getImageY(MapImage mapImage, MapDef mapDef) {
		return (int)(mapImage.getHeight() * getTranslatedY(mapDef));
	}

	public final XY getLocation() {
		return location;
	}

	public final List<MapDef> getMaps() {
		return maps;
	}

	public final String getText() {
		return text;
	}

	public float getTranslatedX(MapDef def) {
		Rectangle bounds = def.getBounds();
		final double tx = location.x - bounds.topLeft.x;
		final double width = bounds.bottomRight.x - bounds.topLeft.x;
		return (float)(tx / width);
	}

	public float getTranslatedY(MapDef def) {
		Rectangle bounds = def.getBounds();
		final double ty = location.y - bounds.topLeft.y;
		final double height = bounds.bottomRight.y - bounds.topLeft.y;
		return (float)(ty / height);
	}

	public final void setEntity(AbstractEntity<?, ?> entity) {
		this.entity = entity;
	}

	public final void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public final void setLocation(XY location) {
		this.location = location;
	}

	public final void setMap(List<MapDef> maps) {
		this.maps = maps;
	}

	public final void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "MapPoint [entity=" + entity + ", location=" + location + ", diameter=" + diameter + "]";
	}

}