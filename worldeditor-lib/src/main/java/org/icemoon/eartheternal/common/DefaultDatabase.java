package org.icemoon.eartheternal.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

public class DefaultDatabase implements IDatabase {
	public static String[] fileArrayToStringArray(final FileObject[] findFiles) {
		String[] l = new String[findFiles.length];
		for (int i = 0; i < l.length; i++) {
			l[i] = findFiles[i].getName().getURI();
		}
		return l;
	}

	private GameItems items;
	private FileObject serverDir;
	private ZoneDefs instances;
	private InteractDefs interactions;
	private LootPackages lootPackages;
	private Creatures creatures;
	private FileObject dataDir;
	private AIScripts aiScripts;
	private Quests quests;
	private MapDefs mapDefs;
	private GroveTemplates groveTemplates;
	private SpawnPackages spawnPackages;
	private CreatureLoots creatureLoots;
	private String[] environments;
	private World world;
	private DropProfiles dropProfiles;
	private Books books;
	private LootCreatures lootCreatures;
	private LootSets lootSets;
	private Abilities abilities;
	private Shops shops;
	private EssenceShops essenceShops;
	private QuestScripts questScripts;
	private InstanceScripts instanceScripts;
	private ZoneMarkers zoneMarkers;
	private ResCosts resCosts;
	private CraftDefs craftDefs;
	private Dialogs dialogs;
	private TerrainTemplates terrainTemplates;
	private Map<Class<?>, DuplicateHandler<?>> duplicateHandlers = new HashMap<Class<?>, DuplicateHandler<?>>();

	public DefaultDatabase() {
	}

	public DefaultDatabase(File serverDir) {
		try {
			setServerDir(VFS.getManager().resolveFile(serverDir.getAbsolutePath()));
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	public DefaultDatabase(FileObject serverDir) {
		setServerDir(serverDir);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> DuplicateHandler<T> getDuplicateHandler(Class<T> clazz) {
		return (DuplicateHandler<T>) duplicateHandlers.get(clazz);
	}

	@Override
	public void setDuplicateHandler(Class<?> clazz, DuplicateHandler<?> handler) {
		duplicateHandlers.put(clazz, handler);
	}

	@Override
	public synchronized Shops getShops() {
		if (shops == null) {
			try {
				shops = new Shops(this, serverDir.resolveFile("Instance").getName().getURI());
				shops.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return shops;
	}

	@Override
	public synchronized InstanceScripts getInstanceScripts() {
		if (instanceScripts == null) {
			try {
				instanceScripts = new InstanceScripts(this, serverDir.resolveFile("Instance").getName().getURI());
				instanceScripts.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instanceScripts;
	}

	@Override
	public synchronized EssenceShops getEssenceShops() {
		if (essenceShops == null) {
			try {
				essenceShops = new EssenceShops(this, serverDir.resolveFile("Instance").getName().getURI());
				essenceShops.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return essenceShops;
	}

	@Override
	public synchronized Abilities getAbilities() {
		if (abilities == null) {
			try {
				abilities = new Abilities(this, serverDir.resolveFile("Data").resolveFile("AbilityTable.txt").getName().getURI(),
						serverDir.resolveFile("Data").resolveFile("AbilityTableAdmin.txt").getName().getURI());
				abilities.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return abilities;
	}

	@Override
	public synchronized AIScripts getAIScripts() {
		if (aiScripts == null) {
			try {
				aiScripts = new AIScripts(this, serverDir.resolveFile("AIScript").getName().getURI());
				aiScripts.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return aiScripts;
	}

	@Override
	public synchronized QuestScripts getQuestScripts() {
		if (questScripts == null) {
			try {
				questScripts = new QuestScripts(this, serverDir.resolveFile("QuestScripts").getName().getURI());
				questScripts.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return questScripts;
	}

	@Override
	public synchronized Books getBooks() {
		if (books == null) {
			try {
				books = new Books(this, serverDir.resolveFile("Books").getName().getURI());
				books.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return books;
	}

	@Override
	public synchronized Dialogs getDialogs() {
		if (dialogs == null) {
			try {
				dialogs = new Dialogs(this, serverDir.resolveFile("Dialog").getName().getURI());
				dialogs.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return dialogs;
	}

	@Override
	public synchronized TerrainTemplates getTerrainTemplates() {
		if (terrainTemplates == null) {
			try {
				terrainTemplates = new TerrainTemplates(this, serverDir.resolveFile("SOURCE/AssetPatches").getName().getURI());
				terrainTemplates.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return terrainTemplates;
	}

	@Override
	public synchronized CreatureLoots getCreatureLoots() {
		if (creatureLoots == null) {
			try {
				creatureLoots = new CreatureLoots(this,
						serverDir.resolveFile("Loot").resolveFile("CreatureLootList.txt").getName().getURI());
				creatureLoots.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return creatureLoots;
	}

	@Override
	public synchronized Creatures getCreatures() {
		if (creatures == null) {
			creatures = new Creatures(this, getPackageFiles("Packages/CreaturePack.txt", null));
			creatures.load();
		}
		return creatures;
	}

	@Override
	public synchronized DropProfiles getDropProfiles() {
		if (dropProfiles == null) {
			try {
				dropProfiles = new DropProfiles(this,
						serverDir.resolveFile("Data").resolveFile("DropRateProfile.txt").getName().getURI());
				dropProfiles.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return dropProfiles;
	}

	@Override
	public synchronized CraftDefs getCraftDefs() {
		if (craftDefs == null) {
			try {
				craftDefs = new CraftDefs(this, serverDir.resolveFile("Data").resolveFile("CraftDef.txt").getName().getURI());
				craftDefs.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return craftDefs;
	}

	@Override
	public synchronized ResCosts getResCosts() {
		if (resCosts == null) {
			try {
				resCosts = new ResCosts(this, serverDir.resolveFile("Data").resolveFile("ResCost.txt").getName().getURI());
				resCosts.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return resCosts;
	}

	@Override
	public synchronized String[] getEnvironments() {
		if (environments == null) {
			try {
				final InputStream in = getClass().getResourceAsStream("/environments.txt");
				try {
					environments = IOUtils.readLines(in).toArray(new String[0]);
				} finally {
					in.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return environments;
	}

	@Override
	public synchronized GroveTemplates getGroveTemplates() {
		if (groveTemplates == null) {
			try {
				groveTemplates = new GroveTemplates(this,
						serverDir.resolveFile("Data").resolveFile("GroveTemplate.txt").getName().getURI());
				groveTemplates.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return groveTemplates;
	}

	@Override
	public synchronized InteractDefs getInteractions() {
		if (interactions == null) {
			try {
				interactions = new InteractDefs(this, dataDir.resolveFile("InteractDef.txt").getName().getURI());
				interactions.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return interactions;
	}

	@Override
	public synchronized GameItems getItems() {
		if (items == null) {
			items = new GameItems(this, getPackageFiles("Packages/ItemPack.txt", null));
			Log.info("Loading items for the first time");
			items.load();
		}
		return items;
	}

	@Override
	public synchronized LootPackages getLootPackages() {
		if (lootPackages == null) {
			try {
				lootPackages = new LootPackages(this, serverDir.resolveFile("Loot").resolveFile("Packages.txt").getName().getURI());
				lootPackages.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return lootPackages;
	}

	@Override
	public synchronized LootCreatures getLootCreatures() {
		if (lootCreatures == null) {
			try {
				lootCreatures = new LootCreatures(this,
						serverDir.resolveFile("Loot").resolveFile("Creatures.txt").getName().getURI());
				lootCreatures.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return lootCreatures;
	}

	@Override
	public synchronized LootSets getLootSets() {
		if (lootSets == null) {
			try {
				lootSets = new LootSets(this, serverDir.resolveFile("Loot").resolveFile("Sets.txt").getName().getURI());
				lootSets.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return lootSets;
	}

	@Override
	public synchronized MapDefs getMapDefs() {
		if (mapDefs == null) {
			try {
				mapDefs = new MapDefs(this, serverDir.resolveFile("Data").resolveFile("MapDef.txt").getName().getURI());
				mapDefs.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return mapDefs;
	}

	@Override
	public synchronized Quests getQuests() {
		if (quests == null) {
			quests = new Quests(this, getPackageFiles("Packages/QuestPack.txt", null));
			quests.load();
		}
		return quests;
	}

	@Override
	public synchronized FileObject getServerDirectory() {
		return serverDir;
	}

	@Override
	public synchronized SpawnPackages getSpawnPackages() {
		if (spawnPackages == null) {
			try {
				spawnPackages = new SpawnPackages(this,
						serverDir.resolveFile("SpawnPackages/SpawnPackageList.txt").getName().getURI());
				spawnPackages.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return spawnPackages;
	}

	@Override
	public synchronized World getWorld() {
		if (world == null) {
			try {
				// TODO groves
				world = new World(this, serverDir.resolveFile("Scenery").getName().getURI());
				world.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return world;
	}

	@Override
	public synchronized ZoneDefs getZoneDefs() {
		if (instances == null) {
			try {
				instances = new ZoneDefs(this, dataDir.resolveFile("ZoneDef.txt").getName().getURI());
				instances.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return instances;
	}

	@Override
	public synchronized ZoneMarkers getZoneMarkers() {
		if (zoneMarkers == null) {
			try {
				zoneMarkers = new ZoneMarkers(this, dataDir.resolveFile("ZoneMarkers.txt").getName().getURI());
				zoneMarkers.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return zoneMarkers;
	}

	protected void setServerDir(FileObject serverDir) {
		this.serverDir = serverDir;
		try {
			dataDir = serverDir.resolveFile("Data");
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] getPackageFiles(String packageName, String prefix) {
		try {
			FileObject packagesFile = serverDir.resolveFile(packageName);
			List<String> f = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new InputStreamReader(packagesFile.getContent().getInputStream()));
			try {
				String line = null;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (!line.startsWith(";") && !line.equals("")) {
						f.add(serverDir
								.resolveFile((prefix == null ? "" : prefix + File.separator) + line.replace("\\", File.separator))
								.getName().getURI());
					}
				}
			} finally {
				br.close();
			}
			return f.toArray(new String[0]);
		} catch (IOException ioe) {
			throw new IllegalStateException("Failed to read package file list " + packageName + ".", ioe);
		}
	}
}
