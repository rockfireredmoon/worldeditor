package org.icemoon.eartheternal.common;

import org.apache.commons.vfs2.FileObject;

public interface IDatabase extends IRoot {
	
	CraftDefs getCraftDefs();
	
	ResCosts getResCosts();
	
	ZoneMarkers getZoneMarkers();
	
	QuestScripts getQuestScripts();
	
	InstanceScripts getInstanceScripts();
	
	EssenceShops getEssenceShops();
	
	Shops getShops();

	Abilities getAbilities();

	Books getBooks();

	AIScripts getAIScripts();

	CreatureLoots getCreatureLoots();

	Creatures getCreatures();

	DropProfiles getDropProfiles();

	String[] getEnvironments();

	GroveTemplates getGroveTemplates();

	InteractDefs getInteractions();

	GameItems getItems();

	LootPackages getLootPackages();

	LootCreatures getLootCreatures();

	LootSets getLootSets();

	MapDefs getMapDefs();

	Quests getQuests();

	FileObject getServerDirectory();

	SpawnPackages getSpawnPackages();

	World<IDatabase> getWorld();

	ZoneDefs getZoneDefs();

	String[] getPackageFiles(String packageName, String prefix);
	
	Dialogs getDialogs();
	
	TerrainTemplates getTerrainTemplates();
}