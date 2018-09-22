package org.icemoon.worldeditor;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.EEPrincipal;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.Account.Permission;
import org.icemoon.worldeditor.entities.ActiveUser;
import org.icemoon.worldeditor.player.AccountsPage;
import org.icemoon.worldeditor.player.CharactersPage;
import org.icemoon.worldeditor.player.HomePage;
import org.icemoon.worldeditor.search.SearchPage;
import org.icemoon.worldeditor.tools.ToolsPage;

@SuppressWarnings("serial")
public class AppSession extends WebSession {
	private static final String PREF_DATABASE_ID = "database";
	private List<Place> adminPlaces = new ArrayList<Place>();
	private List<Place> userPlaces = new ArrayList<Place>();
	private boolean authenticated;
	private boolean admin;
	private String databaseId;
	private EEPrincipal user;

	public AppSession(Request request, IUserData userData) {
		super(request);
		if (userData != null) {
			userPlaces.add(new Place("home", HomePage.class, new ResourceModel("place.home")));
			userPlaces.add(new Place("search", SearchPage.class, new ResourceModel("place.search")));
			userPlaces.add(new Place("questTree", QuestTreePage.class, new ResourceModel("place.questTree")));
			adminPlaces.add(new Place("accounts", AccountsPage.class, new ResourceModel("place.accounts")));
			adminPlaces.add(new Place("characters", CharactersPage.class, new ResourceModel("place.characters")));
		}
		Place world = new Place("world", null, new ResourceModel("place.world"));
		world.getPlaces().add(new Place("zones", ZoneDefsPage.class, new ResourceModel("place.zones")));
		world.getPlaces().add(new Place("interactions", InteractionsPage.class, new ResourceModel("place.interactions")));
		world.getPlaces().add(new Place("maps", MapDefsPage.class, new ResourceModel("place.maps")));
		world.getPlaces().add(new Place("spawnPackages", SpawnPackagesPage.class, new ResourceModel("place.spawnPackages")));
		world.getPlaces().add(new Place("shops", ShopsPage.class, new ResourceModel("place.shops")));
		world.getPlaces().add(new Place("zoneMarkers", ZoneMarkersPage.class, new ResourceModel("place.zoneMarkers")));
		world.getPlaces().add(new Place("resCosts", ResCostsPage.class, new ResourceModel("place.resCosts")));
		world.getPlaces().add(new Place("terrainTemplates", TerrainTemplatesPage.class, new ResourceModel("place.terrainTemplates")));
		adminPlaces.add(world);
		adminPlaces.add(new Place("craftDefs", CraftDefsPage.class, new ResourceModel("place.craftDefs")));
		adminPlaces.add(new Place("abilities", AbilitiesPage.class, new ResourceModel("place.abilities")));
		adminPlaces.add(new Place("stores", DatabaseStoresPage.class, new ResourceModel("place.stores")));
		adminPlaces.add(new Place("books", BooksPage.class, new ResourceModel("place.books")));
		adminPlaces.add(new Place("dialogs", DialogsPage.class, new ResourceModel("place.dialogs")));
		adminPlaces.add(new Place("creatures", CreaturesPage.class, new ResourceModel("place.creatures")));
		adminPlaces.add(new Place("quests", QuestsPage.class, new ResourceModel("place.quests")));
		adminPlaces.add(new Place("items", ItemsPage.class, new ResourceModel("place.items")));
		Place scripts = new Place("scripts", null, new ResourceModel("place.scripts"));
		scripts.getPlaces().add(new Place("aiScripts", AIScriptsPage.class, new ResourceModel("place.aiScripts")));
		scripts.getPlaces()
				.add(new Place("instanceScripts", InstanceScriptsPage.class, new ResourceModel("place.instanceScripts")));
		scripts.getPlaces().add(new Place("questScripts", QuestScriptsPage.class, new ResourceModel("place.questScripts")));
		adminPlaces.add(scripts);
		Place loot = new Place("loot", null, new ResourceModel("place.loot"));
		loot.getPlaces().add(new Place("lootSets", LootSetsPage.class, new ResourceModel("place.lootSets")));
		loot.getPlaces().add(new Place("lootPackages", LootPackagesPage.class, new ResourceModel("place.lootPackages")));
		loot.getPlaces().add(new Place("lootCreatures", LootCreaturesPage.class, new ResourceModel("place.lootCreatures")));
		loot.getPlaces().add(new Place("essenceShops", EssenceShopsPage.class, new ResourceModel("place.essenceShops")));
		loot.getPlaces().add(new Place("dropProfiles", DropProfilesPage.class, new ResourceModel("place.dropProfiles")));
		adminPlaces.add(loot);
		adminPlaces.add(new Place("tools", ToolsPage.class, new ResourceModel("place.tools")));
	}

	public ActiveUser getActiveUser() {
		return StringUtils.isNotBlank(getId()) ? ((Application) getApplication()).getActiveUsers().get(getId()) : null;
	}

	@Override
	public void onInvalidate() {
		super.onInvalidate();
		Application application = (Application) Application.get(Application.KEY);
		ActiveUser au = application.getActiveUsers().get(getId());
		if (au != null)
			application.getActiveUsers().delete(au);
	}

	public final String getDatabaseId() {
		return databaseId;
	}

	public final void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
		final Principal acc = getUser();
		if (acc != null) {
			Application.getApp().getSiteData().getPreferences(acc).put(PREF_DATABASE_ID, databaseId);
		}
	}

	public IDatabase getDatabase() {
		return Application.getApp().getDatabase(databaseId);
	}

	public final boolean isAuthenticated() {
		return authenticated;
	}

	public boolean isAdmin() {
		return admin;
	}

	public Principal getUser() {
		return user;
	}

	public void setAdmin(boolean admin) {
		if (admin != this.admin) {
			if (admin) {
				if (user == null || !user.getPermissions().contains(Permission.ADMIN))
					throw new SecurityException();
			}
			this.admin = admin;
		}
	}

	public Account getAccount() {
		final IUserData userData = Application.getApp().getUserData();
		if (userData == null || user == null)
			return null;
		return userData.getAccounts().getAccount(user.getName());
	}

	public final String getUsername() {
		return user == null ? null : user.getName();
	}

	public boolean authenticate(final String username, final String password) {
		authenticated = true;
		user = Application.getApp().getAuthenticator().login(username, password.toCharArray());
		if (user == null)
			return false;
		getActiveUser().setUser(user);
		databaseId = Application.getApp().getSiteData().getPreferences(user).get(PREF_DATABASE_ID, "");
		if (databaseId.equals("") || !Application.getApp().getDatabaseStores().contains(databaseId))
			databaseId = Application.getApp().getDatabaseStores().isEmpty() ? null
					: Application.getApp().getDatabaseStores().values().get(0).getEntityId();
		return true;
	}

	public void signOut() {
		authenticated = false;
		user = null;
	}

	public List<Place> getPlaces() {
		return admin ? adminPlaces : userPlaces;
	}
}
