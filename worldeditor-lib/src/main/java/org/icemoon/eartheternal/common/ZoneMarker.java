package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class ZoneMarker extends AbstractMultiINIFileEntity<Long, IDatabase> {
	public static class Marker implements Serializable {
		private Position location;
		private String description;
		private String map;

		public Marker() {
			location = new Position();
		}

		public Marker(String line) {
			StringTokenizer t = new StringTokenizer(line, ",");
			location = new Position(t.nextToken() + "," + t.nextToken() + "," + t.nextToken());
			description = t.nextToken();
			if (t.hasMoreTokens())
				map = t.nextToken();
		}

		public Marker(Position location, String description, String map) {
			super();
			this.location = location;
			this.description = description;
			this.map = map;
		}

		public final Position getLocation() {
			return location;
		}

		public final void setLocation(Position location) {
			this.location = location;
		}

		public final String getDescription() {
			return description;
		}

		public final void setDescription(String description) {
			this.description = description;
		}

		public final String getMap() {
			return map;
		}

		public final void setMap(String map) {
			this.map = map;
		}

		public void write(INIWriter writer) {
			writer.print("Sanctuary=" + location.toCompactString() + "," + description);
			if (StringUtils.isNotBlank(map))
				writer.println("," + map);
			else
				writer.println();
		}

		@Override
		public String toString() {
			return location.toCompactString() + " (" + description + ")";
		}
	}

	private List<Marker> markers = new ArrayList<ZoneMarker.Marker>();

	public ZoneMarker() {
		this(null);
	}

	public ZoneMarker(IDatabase database) {
		super(database);
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("ZoneID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equals("Sanctuary")) {
			markers.add(new Marker(value));
		} else if (!name.equals("")) {
			Log.todo("Instance (" + getFile() + ")", "Unhandle property " + name + " = " + value);
		}
	}

	public final List<Marker> getMarkers() {
		return markers;
	}

	public final void setMarkers(List<Marker> markers) {
		this.markers = markers;
	}

	@Override
	public String toString() {
		return getEntityId() == null ? "" : getEntityId().toString();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("ZoneID=" + getEntityId());
		for (Marker s : markers) {
			s.write(writer);
		}
	}
}
