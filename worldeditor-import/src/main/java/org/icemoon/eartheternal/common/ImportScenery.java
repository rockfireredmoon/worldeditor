package org.icemoon.eartheternal.common;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ImportScenery {

	public static void main(String[] args) throws Exception {
		long zoneId = Integer.parseInt(args[0]);
		String dbuser = args[1];
		String dbpass = args[2];
		String dburl = args[3];
		String dbdriver = args[4];

		File worldDir = new File("tmp");
		File sceneryDir = new File(worldDir, "Scenery");
		sceneryDir.mkdirs();
		IDatabase db = new DefaultDatabase(worldDir);
		World<IDatabase> world = db.getWorld();

		File zoneSceneryDir = new File(sceneryDir, String.valueOf(zoneId));
		zoneSceneryDir.mkdirs();
		Sceneries<IDatabase> sceneries = new Sceneries<>(db, sceneryDir.toURI().toString(), world);
		sceneries.setEntityId(zoneId);

		Class.forName(dbdriver);
		try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass)) {
			ZoneDef zdef;

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM ZoneDef WHERE id = ?");
			ps.setLong(1, zoneId);
			try {
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						zdef = new ZoneDef(db);
						zdef.setFile(new File(new File(worldDir, "Data"), "ZoneDef.txt").toURI().toString());
						zdef.setEntityId(new Long(zoneId));
						zdef.setName(rs.getString("name"));
						zdef.setEnvironmentType(rs.getString("environmentType"));
						zdef.setPageSize(rs.getInt("pageSize"));
						zdef.setRegions(rs.getString("regions"));
						zdef.setTerrainConfig(rs.getString("terrainConfig"));
						zdef.setMapName(rs.getString("mapName"));
						zdef.setInstance(rs.getBoolean("instanceMode"));
					} else
						throw new IOException("No such zone");
				}
			} finally {
				ps.close();
			}

			db.getZoneDefs().save(zdef);

			ps = conn.prepareStatement("SELECT * FROM Scenery WHERE zoneDef_id = ?");
			ps.setLong(1, zoneId);
			try {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						Scenery<IDatabase> s = new Scenery<>();
						s.setEntityId(rs.getLong("id"));
						s.setAsset(rs.getString("asset"));
						s.setFlags(rs.getInt("flags"));
						s.setP(new Position(rs.getDouble("px"), rs.getDouble("py"), rs.getDouble("pz")));
						s.setQ(new Quat(rs.getDouble("qx"), rs.getDouble("qy"), rs.getDouble("qz"),
								rs.getDouble("qw")));
						s.setS(new Position(rs.getDouble("sx"), rs.getDouble("sy"), rs.getDouble("sz")));
						s.setName(rs.getString("name"));
						s.setLayer(rs.getString("layer"));
						int pageX = (int) (s.getP().getX() / zdef.getPageSize());
						int pageY = (int) (s.getP().getZ() / zdef.getPageSize());
						s.setFile(new File(zoneSceneryDir, String.format("x%03dy%03d.txt", pageX, pageY)).toURI()
								.toString());

						PreparedStatement ps2 = conn
								.prepareStatement("SELECT * FROM SceneryPropertyValues WHERE Scenery_id = ?");
						ps2.setLong(1, s.getEntityId());
						try {
							try (ResultSet rs2 = ps2.executeQuery()) {
								while (rs2.next()) {
									String name = rs2.getString("name");
									String sv = rs2.getString("stringValue");
									int iv = rs2.getInt("integerValue");
									float fv = rs2.getFloat("floatValue");
									if (name.equals("package")) {
										s.setSpawnPackage(sv);
									} else if (name.equals("despawnTime")) {
										s.setDespawnTime(iv);
									} else if (name.equals("innerRadius")) {
										s.setInnerRadius(fv);
									} else if (name.equals("outerRadius")) {
										s.setOuterRadius(fv);
									} else
										throw new UnsupportedOperationException();
								}
							}
						} finally {
							ps2.close();
						}

//						
						sceneries.save(s);
					}
				}
			} finally {
				ps.close();
			}
		}

		INIWriter iw = new INIWriter(System.out);
	}
}
