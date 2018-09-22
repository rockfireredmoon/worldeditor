package org.icemoon.eartheternal.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class GroveTemplate extends AbstractTableFileEntity<String, IDatabase> {
	private String fileName;
	private String terrainConfig;
	private Rectangle buildBounds = new Rectangle();
	private String environment;
	private String mapName;
	private String regions;
	private XYZ defaultLocation = new XYZ();

	public GroveTemplate() {
		this(null);
	}

	public GroveTemplate(IDatabase database) {
		super(database);
	}

	public GroveTemplate(IDatabase database, String shortName) {
		this(database, shortName, null, null, null, null, null, null, null);
	}

	public GroveTemplate(IDatabase database, String shortName, String fileName, String terrainConfig, String environment,
			String mapName, String regions, Rectangle buildBounds, XYZ defaultLocation) {
		super(database, null, shortName);
		this.fileName = fileName;
		this.terrainConfig = terrainConfig;
		this.environment = environment;
		this.mapName = mapName;
		this.regions = regions;
		this.buildBounds = buildBounds;
		this.defaultLocation = defaultLocation;
	}

	public Rectangle getBuildBounds() {
		return buildBounds;
	}

	public final XYZ getDefaultLocation() {
		return defaultLocation;
	}

	public final String getEnvironment() {
		return environment;
	}

	public final String getFileName() {
		return fileName;
	}

	public final String getMapName() {
		return mapName;
	}

	public final String getRegions() {
		return regions;
	}

	public final String getTerrainConfig() {
		return terrainConfig;
	}

	@Override
	public void set(String[] row, String comment) {
		setEntityId(row[0]);
		fileName = row[1];
		terrainConfig = row[2];
		environment = row[3];
		mapName = row[4];
		regions = row[5];
		buildBounds = new Rectangle(Long.parseLong(row[6]), Long.parseLong(row[7]), Long.parseLong(row[8]), Long.parseLong(row[9]));
		if (row.length > 10)
			defaultLocation = new XYZ(Long.parseLong(row[10]), Long.parseLong(row[11]), Long.parseLong(row[12]));
		else
			defaultLocation = new XYZ(0, 0, 0);
	}

	public void setBuildBounds(Rectangle buildBounds) {
		this.buildBounds = buildBounds;
	}

	public final void setDefaultLocation(XYZ defaultLocation) {
		this.defaultLocation = defaultLocation;
	}

	public final void setEnvironment(String environment) {
		this.environment = environment;
	}

	public final void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public final void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public final void setRegions(String regions) {
		this.regions = regions;
	}

	public final void setTerrainConfig(String terrainConfig) {
		this.terrainConfig = terrainConfig;
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	public void write(INIWriter writer) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doLoad() throws IOException {
	}
}
