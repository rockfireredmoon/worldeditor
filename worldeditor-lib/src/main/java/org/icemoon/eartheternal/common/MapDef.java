package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class MapDef extends AbstractMultiINIFileEntity<String, IDatabase> {
	private String primary;
	private String name;
	private MapType type = MapType.REGION;
	private Size pages = new Size();
	private String parentMapImage;
	private String image;
	private int priority;
	private Rectangle bounds = new Rectangle();
	private XY scale;

	public MapDef() {
		this(null);
	}

	public MapDef(IDatabase database) {
		super(database);
	}

	public boolean containsLocation(Location location) {
		return location != null && getBounds().contains(location);
	}

	public boolean containsLocation(XYZ location) {
		return location != null && getBounds().contains(location.toXZPoint());
	}

	public boolean forInstance(ZoneDef instance) {
		return instance != null && primary != null && primary.equals("Maps-" + instance.getWarpName());
	}

	public final Rectangle getBounds() {
		return bounds;
	}

	public final String getImage() {
		return image;
	}

	public final String getName() {
		return name;
	}

	public final Size getPages() {
		return pages;
	}

	public final String getParentMapImage() {
		return parentMapImage;
	}

	public final String getPrimary() {
		return primary;
	}

	public final int getPriority() {
		return priority;
	}

	public final XY getScale() {
		return scale;
	}

	public final MapType getType() {
		return type;
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("Name")) {
			this.name = value;
			setEntityId(name + "/" + getType());
		} else if (name.equals("Primary")) {
			primary = value;
		} else if (name.equals("Type")) {
			setEntityId((this.name == null ? "" : this.name) + "/" + getType());
			type = MapType.valueOf(value.toUpperCase());
		} else if (name.equals("numPagesAcross")) {
			pages.x = Double.parseDouble(value);
		} else if (name.equals("numPagesDown")) {
			pages.y = Double.parseDouble(value);
		} else if (name.equals("parentMapImage")) {
			parentMapImage = value;
		} else if (name.equals("image")) {
			image = value;
		} else if (name.equals("priority")) {
			priority = Integer.parseInt(value);
		} else if (name.equals("u0")) {
			bounds.topLeft.x = Long.parseLong(value);
		} else if (name.equals("v0")) {
			bounds.topLeft.y = Long.parseLong(value);
		} else if (name.equals("u1")) {
			bounds.bottomRight.x = Long.parseLong(value);
		} else if (name.equals("v1")) {
			bounds.bottomRight.y = Long.parseLong(value);
		} else if (name.equals("scale_width")) {
			if (scale == null) {
				scale = new XY();
			}
			scale.x = Long.parseLong(value);
		} else if (name.equals("scale_height")) {
			if (scale == null) {
				scale = new XY();
			}
			scale.y = Long.parseLong(value);
		} else if (!name.equals("")) {
			Log.todo("MapDef", "Unhandled property " + name + " = " + value);
		}
	}

	public final void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public final void setImage(String image) {
		this.image = image;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setPages(Size pages) {
		this.pages = pages;
	}

	public final void setParentMapImage(String parentMapImage) {
		this.parentMapImage = parentMapImage;
	}

	public final void setPrimary(String primary) {
		this.primary = primary;
	}

	public final void setPriority(int priority) {
		this.priority = priority;
	}

	public final void setScale(XY scale) {
		this.scale = scale;
	}

	public final void setType(MapType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("Name=" + name);
		writer.println("Primary=" + primary);
		writer.println("Type=" + type.toString());
		writer.println("numPagesAcross=" + pages.x);
		writer.println("numPagesDown=" + pages.y);
		if (scale != null) {
			if (scale.x > 0) {
				writer.println("scale_width=" + scale.x);
			}
			if (scale.y > 0) {
				writer.println("scale_height=" + scale.y);
			}
		}
		writer.println("parentMapImage=" + parentMapImage);
		writer.println("image=" + image);
		writer.println("priority=" + priority);
		writer.println("u0=" + bounds.topLeft.x);
		writer.println("v0=" + bounds.topLeft.y);
		writer.println("u1=" + bounds.bottomRight.x);
		writer.println("v1=" + bounds.bottomRight.y);
	}

	public enum MapType {
		LOCAL, WORLD, REGION, SUPERWORLD;
		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}
}
