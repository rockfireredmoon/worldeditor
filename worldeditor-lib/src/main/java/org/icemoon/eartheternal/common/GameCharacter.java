package org.icemoon.eartheternal.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.icemoon.eartheternal.common.EternalObjectNotation.EternalObject;
import org.icemoon.eartheternal.common.GameItem.Type;
import org.icemoon.eartheternal.common.ZoneDef.PVPMode;

@SuppressWarnings("serial")
public class GameCharacter extends AbstractSeparateINIFileEntity<Long, IUserData> implements BaseCreature {
	private int order;
	private Long instance;
	private Long zone;
	private XYZ location = new XYZ(0, 0, 0);
	private String statusText;
	private long secondsLogged;
	private int sessionsLogged;
	private long timeLogged;
	private long lastSession;
	private Date lastLogon;
	private Date lastLogoff;
	private int maxSideKicks;
	private Properties prefs = new Properties();
	private Map<Long, List<Long>> activeQuests = new TreeMap<Long, List<Long>>();
	private List<Long> completeQuests = new ArrayList<Long>();
	private List<Long> repeatQuests = new ArrayList<Long>();
	private int baseLuck;
	private int visWeapon;
	private int will;
	private int might;
	private int healthMod;
	private String translocateDestination;
	private List<Integer> baseStats = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0));
	private int heroism;
	private long exp;
	private int totalAbilityPoints;
	private int currentAbilityPoints;
	private long copper;
	private long credits;
	private double modCastingSpeed;
	private int totalSize;
	private int health;
	private Appearance originalAppearance = new Appearance();
	//
	private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm aa");
	private Map<Integer, Long> inventory;
	private Map<Slot, Long> equipment = new TreeMap<Slot, Long>();
	private List<Long> abilities = new ArrayList<Long>();
	private long lastWarpTime;
	private int unstickCount;
	private long lastUnstickTime;
	private int mightCharges;
	private int willCharges;
	private double modAttackSpeed;
	private int characterVersion = 1;
	private int rotation;
	private int currentVaultSize;
	private int creditsPurchased;
	private int creditsSpent;
	private int extraAbilityPoints;
	private Location groveReturn;
	private Location bindReturn;
	private long accountId;
	private boolean appearanceOverride;
	private List<Long> hengeList = new ArrayList<Long>();
	private Map<Long, String> friends = new TreeMap<Long, String>();
	private List<Sidekick> sidekicks = new ArrayList<Sidekick>();
	private String instanceScaler;
	private boolean hideNameboard;
	private float invisibilityDistance;
	private Map<String, List<Long>> cooldowns = new TreeMap<String, List<Long>>();
	private String alternateIdleAnim;
	private int pvpTeam;
	private String pvpState;
	private EternalObject selectiveEqOverride;
	private Map<Integer, Integer> guilds = new TreeMap<Integer, Integer>();
	private PVPMode mode = PVPMode.PVE;
	private List<CharacterAbility> activeAbilities = new ArrayList<CharacterAbility>();
	private CreatureSupport cs = new CreatureSupport();

	public GameCharacter() {
		this(null);
	}

	public GameCharacter(IUserData database) {
		super(database);
		inventory = new TreeMap<Integer, Long>();
		setLevel(1);
	}

	public void addToInventory(Long entityId) {
		Integer key = getNextFreeSlotNumber();
		if (key == null) {
			throw new IllegalStateException("Not enough room in inventory");
		}
		inventory.put(key, entityId);
	}

	public void clearInventory() {
		inventory.clear();
	}

	public void equip(GameItem item) {
		final Slot slot = item.getEquipType().toSlot();
		Long equipped = equipment.get(slot);
		if (equipped != null) {
			if (equipped.equals(item.getEntityId())) {
				throw new IllegalStateException("Already equipped");
			}
			// Move to inventory
			Integer key = getNextFreeSlotNumber();
			if (key == null) {
				throw new IllegalStateException("Not enough room in inventory for current equipment in slot " + slot.toString());
			}
			inventory.put(key, equipped);
		}
		equip(slot, item);
	}

	public void equip(Slot slot, GameItem item) {
		equipment.put(slot, item.getEntityId());
	}

	public List<Long> getAbilities() {
		return abilities;
	}

	public long getAccountId() {
		return accountId;
	}

	public final List<CharacterAbility> getActiveAbilities() {
		return activeAbilities;
	}

	public final Map<Long, List<Long>> getActiveQuests() {
		return activeQuests;
	}

	public String getAlternateIdleAnim() {
		return alternateIdleAnim;
	}

	@Override
	public Appearance getAppearance() {
		return cs.getAppearance();
	}

	public final int getBaseLuck() {
		return baseLuck;
	}

	public final List<Integer> getBaseStats() {
		return baseStats;
	}

	public Location getBindReturn() {
		return bindReturn;
	}

	@Override
	public int getCastingSetbackChance() {
		return cs.getCastingSetbackChance();
	}

	@Override
	public int getChannelingBreakChance() {
		return cs.getChannelingBreakChance();
	}

	public int getCharacterVersion() {
		return characterVersion;
	}

	public final List<Long> getCompleteQuests() {
		return completeQuests;
	}

	@Override
	public int getConstitution() {
		return cs.getConstitution();
	}

	public Map<String, List<Long>> getCooldowns() {
		return cooldowns;
	}

	public final long getCopper() {
		return copper;
	}

	@Override
	public CreatureCategory getCreatureCategory() {
		return cs.getCreatureCategory();
	}

	public final long getCredits() {
		return credits;
	}

	public int getCreditsPurchased() {
		return creditsPurchased;
	}

	public int getCreditsSpent() {
		return creditsSpent;
	}

	public final int getCurrentAbilityPoints() {
		return currentAbilityPoints;
	}

	public int getCurrentVaultSize() {
		return currentVaultSize;
	}

	@Override
	public int getDamageResistDeath() {
		return cs.getDamageResistDeath();
	}

	@Override
	public int getDamageResistFire() {
		return cs.getDamageResistFire();
	}

	@Override
	public int getDamageResistFrost() {
		return cs.getDamageResistFrost();
	}

	@Override
	public int getDamageResistMelee() {
		return cs.getDamageResistMelee();
	}

	@Override
	public int getDamageResistMystic() {
		return cs.getDamageResistMystic();
	}

	public final DateFormat getDateTimeFormat() {
		return dateTimeFormat;
	}

	@Override
	public int getDexterity() {
		return cs.getDexterity();
	}

	@Override
	public String getDisplayName() {
		return cs.getDisplayName();
	}

	@Override
	public Appearance getEqAppearance() {
		return cs.getEqAppearance();
	}

	public final Map<Slot, Long> getEquipment() {
		return equipment;
	}

	public final long getExp() {
		return exp;
	}

	public int getExtraAbilityPoints() {
		return extraAbilityPoints;
	}

	public Map<Long, String> getFriends() {
		return friends;
	}

	public Location getGroveReturn() {
		return groveReturn;
	}

	public Map<Integer, Integer> getGuilds() {
		return guilds;
	}

	public final int getHealth() {
		return health;
	}

	public int getHealthMod() {
		return healthMod;
	}

	public List<Long> getHengeList() {
		return hengeList;
	}

	public final int getHeroism() {
		return heroism;
	}

	@Override
	public String getIcon() {
		return "cathead.png";
	}

	public final Long getInstance() {
		return instance;
	}

	public String getInstanceScaler() {
		return instanceScaler;
	}

	public final Map<Integer, Long> getInventory() {
		return inventory;
	}

	public float getInvisibilityDistance() {
		return invisibilityDistance;
	}

	public final Date getLastLogoff() {
		return lastLogoff;
	}

	public final Date getLastLogon() {
		return lastLogon;
	}

	public final long getLastSession() {
		return lastSession;
	}

	public long getLastUnstickTime() {
		return lastUnstickTime;
	}

	public long getLastWarpTime() {
		return lastWarpTime;
	}

	@Override
	public int getLevel() {
		return cs.getLevel();
	}

	public final XYZ getLocation() {
		return location;
	}

	public final int getMaxSideKicks() {
		return maxSideKicks;
	}

	public final int getMight() {
		return might;
	}

	public int getMightCharges() {
		return mightCharges;
	}

	public double getModAttackSpeed() {
		return modAttackSpeed;
	}

	public final double getModCastingSpeed() {
		return modCastingSpeed;
	}

	public final PVPMode getMode() {
		return mode;
	}

	public Integer getNextFreeSlotNumber() {
		// TODO only check as many slots as available
		for (int i = 0; i < 1000; i++) {
			if (!inventory.containsKey(i)) {
				return i;
			}
		}
		return null;
	}

	@Override
	public int getOffhandWeaponDamage() {
		return cs.getOffhandWeaponDamage();
	}

	public final int getOrder() {
		return order;
	}

	public Appearance getOriginalAppearance() {
		return originalAppearance;
	}

	public final Properties getPrefs() {
		return prefs;
	}

	@Override
	public Profession getProfession() {
		return cs.getProfession();
	}

	@Override
	public int getPsyche() {
		return cs.getPsyche();
	}

	public String getPvpState() {
		return pvpState;
	}

	public int getPvpTeam() {
		return pvpTeam;
	}

	public Rarity getRarity() {
		return cs.getRarity();
	}

	public List<Long> getRepeatQuests() {
		return repeatQuests;
	}

	public int getRotation() {
		return rotation;
	}

	public final long getSecondsLogged() {
		return secondsLogged;
	}

	public EternalObject getSelectiveEqOverride() {
		return selectiveEqOverride;
	}

	public final int getSessionsLogged() {
		return sessionsLogged;
	}

	public List<Sidekick> getSidekicks() {
		return sidekicks;
	}

	@Override
	public int getSpirit() {
		return cs.getSpirit();
	}

	public final String getStatusText() {
		return statusText;
	}

	@Override
	public int getStrength() {
		return cs.getStrength();
	}

	public final DateFormat getTimeFormat() {
		return timeFormat;
	}

	public final long getTimeLogged() {
		return timeLogged;
	}

	public final int getTotalAbilityPoints() {
		return totalAbilityPoints;
	}

	public final int getTotalSize() {
		return totalSize;
	}

	public final String getTranslocateDestination() {
		return translocateDestination;
	}

	public int getUnstickCount() {
		return unstickCount;
	}

	public final int getVisWeapon() {
		return visWeapon;
	}

	public final int getWill() {
		return will;
	}

	public int getWillCharges() {
		return willCharges;
	}

	public int getWillChargs() {
		return willCharges;
	}

	public final Long getZone() {
		return zone;
	}

	public boolean isAppearanceOverride() {
		return appearanceOverride;
	}

	public boolean isEquipped(GameItem item) {
		final Slot slot = item.getEquipType().toSlot();
		Long equipped = equipment.get(slot);
		return equipped != null && equipped.equals(item.getEntityId());
	}

	public boolean isHideNameboard() {
		return hideNameboard;
	}

	@Override
	public float getMightRegen() {
		return cs.getMightRegen();
	}

	@Override
	public float getWillRegen() {
		return cs.getWillRegen();
	}

	public boolean onMap(MapDef def) {
		return location != null && def.getBounds().contains(location.toXZPoint());
	}

	public void removeAllBags() {
		equipment.remove(Slot.BAG_1);
		equipment.remove(Slot.BAG_2);
		equipment.remove(Slot.BAG_3);
		equipment.remove(Slot.BAG_4);
	}

	public void removeAllEquipment() {
		equipment.clear();
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("ID")) {
			setEntityId(Long.parseLong(value));
		} else if (section.equals("PREFS")) {
			if (!name.equals("")) {
				prefs.setProperty(name, value);
			}
		} else if (section.equals("INV")) {
			if (name.equals("eq")) {
				// Equipment
				StringTokenizer t = new StringTokenizer(value, ",");
				equipment.put(Slot.fromCode(Integer.parseInt(t.nextToken())), Long.parseLong(t.nextToken()));
			} else if (name.equals("inv")) {
				// Inventory
				StringTokenizer t = new StringTokenizer(value, ",");
				inventory.put(Integer.parseInt(t.nextToken()), Long.parseLong(t.nextToken()));
			}
		} else if (section.equals("ABILITIES") && name.equals("Active")) {
			activeAbilities.add(new CharacterAbility(value));
		} else if (section.equals("QUEST") && name.equals("active")) {
			int idx = value.indexOf(",");
			if (idx == -1) {
				Log.error("Character", "Invalid active quest format.");
			} else {
				activeQuests.put(Long.parseLong(value.substring(0, idx)), Util.toLongList(value.substring(idx + 1)));
			}
		} else if (section.equals("QUEST") && name.equals("complete")) {
			completeQuests.addAll(Util.toLongList(value));
		} else if (section.equals("COOLDOWN")) {
			if (!value.equals("")) {
				List<Long> l = new ArrayList<Long>();
				for (String s : value.split(",")) {
					l.add(Long.parseLong(s));
				}
				cooldowns.put(name, l);
			}
		} else if (name.equals("Order")) {
			if (Util.isNotNullOrEmpty(value)) {
				order = Integer.parseInt(value);
			}
		} else if (name.equals("Instance")) {
			instance = Long.parseLong(value);
		} else if (name.equals("Zone")) {
			zone = Long.parseLong(value);
		} else if (name.equals("Mode")) {
			mode = PVPMode.values()[Integer.parseInt(value)];
		} else if (name.equals("X")) {
			location.setX(Long.parseLong(value));
			//
			// NOTE
			//
			//
			// X/Y/Z locations here seem to be different to other locations
			// Going by eartheternal.info, it describes locations as X,Z,Y (with
			// Z being the low number usually seen). In this case, Y is low
			// number
			// I wonder which is correct?
		} else if (name.equals("Z")) {
			location.setY(Long.parseLong(value));
		} else if (name.equals("Y")) {
			location.setZ(Long.parseLong(value));
		} else if (name.equals("StatusText")) {
			statusText = value;
		} else if (name.equals("SecondsLogged")) {
			secondsLogged = Long.parseLong(value);
		} else if (name.equals("SessionsLogged")) {
			sessionsLogged = Integer.parseInt(value);
		} else if (name.equals("TimeLogged")) {
			try {
				timeLogged = Util.parseElapsedTime(value);
			} catch (Exception e) {
			}
		} else if (name.equals("LastSession")) {
			try {
				lastSession = Util.parseElapsedTime(value);
			} catch (Exception e) {
			}
		} else if (name.equals("LastLogOn")) {
			try {
				lastLogon = dateTimeFormat.parse(value);
			} catch (Exception e) {
			}
		} else if (name.equals("LastLogOff")) {
			try {
				lastLogoff = dateTimeFormat.parse(value);
			} catch (Exception e) {
			}
		} else if (name.equals("MaxSidekicks")) {
			maxSideKicks = Integer.parseInt(value);
		} else if (name.equals("vis_weapon")) {
			visWeapon = Integer.parseInt(value);
		} else if (name.equals("will")) {
			will = Integer.parseInt(value);
		} else if (name.equals("might")) {
			might = Integer.parseInt(value);
		} else if (name.equals("translocate_destination")) {
			translocateDestination = value;
		} else if (name.equals("base_stats")) {
			baseStats = Util.toIntegerList(value);
		} else if (name.equals("base_luck")) {
			baseLuck = Integer.parseInt(value);
		} else if (name.equals("health")) {
			health = Integer.parseInt(value);
		} else if (name.equals("health_mod")) {
			healthMod = Integer.parseInt(value);
		} else if (name.equals("heroism")) {
			heroism = Integer.parseInt(value);
		} else if (name.equals("experience")) {
			exp = Long.parseLong(value);
		} else if (name.equals("total_ability_points")) {
			totalAbilityPoints = Integer.parseInt(value);
		} else if (name.equals("current_ability_points")) {
			currentAbilityPoints = Integer.parseInt(value);
		} else if (name.equals("copper")) {
			copper = Long.parseLong(value);
		} else if (name.equals("credits")) {
			credits = Long.parseLong(value);
		} else if (name.equals("mod_casting_speed")) {
			modCastingSpeed = Double.parseDouble(value);
		} else if (name.equals("total_size")) {
			totalSize = Integer.parseInt(value);
		} else if (name.equals("Abilities")) {
			abilities.addAll(Util.toLongList(value));
		} else if (name.equals("OriginalAppearance")) {
			try {
				originalAppearance = new Appearance(value);
			} catch (ParseException e) {
				Log.error("Creature", "Failed to parse appearance.", e);
			}
			// private XYZ groveReturn;
			// private XYZ bindReturn;
		} else if (name.equals("LastWarpTime")) {
			lastWarpTime = Long.parseLong(value);
		} else if (name.equals("UnstickCount")) {
			unstickCount = Integer.parseInt(value);
		} else if (name.equals("LastUnstickTime")) {
			lastUnstickTime = Long.parseLong(value);
		} else if (name.equals("might_charges")) {
			mightCharges = Integer.parseInt(value);
		} else if (name.equals("will_charges")) {
			willCharges = Integer.parseInt(value);
		} else if (name.equals("mod_attack_speed")) {
			modAttackSpeed = Double.parseDouble(value);
		} else if (name.equals("characterVersion")) {
			characterVersion = Integer.parseInt(value);
		} else if (name.equals("AccountID")) {
			accountId = Long.parseLong(value);
		} else if (name.equals("Rotation")) {
			rotation = Integer.parseInt(value);
		} else if (name.equals("CurrentVaultSize")) {
			currentVaultSize = Integer.parseInt(value);
		} else if (name.equals("CreditsPurchased")) {
			creditsPurchased = Integer.parseInt(value);
		} else if (name.equals("CreditsSpent")) {
			creditsSpent = Integer.parseInt(value);
		} else if (name.equals("ExtraAbilityPoints")) {
			extraAbilityPoints = Integer.parseInt(value);
		} else if (name.equals("GroveReturn")) {
			groveReturn = new Location(value);
		} else if (name.equals("BindReturn")) {
			bindReturn = new Location(value);
		} else if (name.equals("appearance_override")) {
			appearanceOverride = value.equals("1");
		} else if (name.equals("hengeList")) {
			hengeList.addAll(Util.toLongList(value));
		} else if (name.equals("hengeList")) {
			hengeList.add(Long.parseLong(value));
		} else if (name.equals("FriendList")) {
			String[] a = value.split(",");
			friends.put(Long.parseLong(a[0]), a[1]);
		} else if (name.equals("Sidekick")) {
			String[] a = value.split(",");
			sidekicks.add(new Sidekick(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2])));
		} else if (name.equals("InstanceScaler")) {
			instanceScaler = value;
		} else if (name.equals("hide_nameboard")) {
			hideNameboard = value.equals("1");
		} else if (name.equals("invisibility_distance")) {
			invisibilityDistance = Float.parseFloat(value);
		} else if (name.equals("repeat")) {
			repeatQuests.addAll(Util.toLongList(value));
		} else if (name.equals("pvp_team")) {
			pvpTeam = Integer.parseInt(value);
		} else if (name.equals("pvp_state")) {
			pvpState = value;
		} else if (name.equals("alternate_idle_anim")) {
			alternateIdleAnim = value;
		} else if (name.equals("GuildList")) {
			String[] a = value.split(",");
			guilds.put(Integer.parseInt(a[0]), Integer.parseInt(a[1]));
		} else if (name.equals("selective_eq_override")) {
			try {
				selectiveEqOverride = new EternalObjectNotation.EternalObject(value);
			} catch (ParseException e) {
				Log.error(getFile(), "Failed to parse eq override", e);
			}
		} else {
			if (!cs.set(name, value, section)) {
				Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
			}
		}
	}

	public void setAbilities(List<Long> abilities) {
		this.abilities = abilities;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public final void setActiveAbilities(List<CharacterAbility> activeAbilities) {
		this.activeAbilities = activeAbilities;
	}

	public final void setActiveQuests(Map<Long, List<Long>> activeQuests) {
		this.activeQuests = activeQuests;
	}

	public void setAlternateIdleAnim(String alternateIdleAnim) {
		this.alternateIdleAnim = alternateIdleAnim;
	}

	@Override
	public void setAppearance(Appearance appearance) {
		cs.setAppearance(appearance);
	}

	public void setAppearanceOverride(boolean appearanceOverride) {
		this.appearanceOverride = appearanceOverride;
	}

	public final void setBaseLuck(int baseLuck) {
		this.baseLuck = baseLuck;
	}

	public final void setBaseStats(List<Integer> baseStats) {
		this.baseStats = baseStats;
	}

	public void setBindReturn(Location bindReturn) {
		this.bindReturn = bindReturn;
	}

	@Override
	public void setCastingSetbackChance(int castingSetbackChance) {
		cs.setCastingSetbackChance(castingSetbackChance);
	}

	@Override
	public void setChannelingBreakChance(int channelingBreakChance) {
		cs.setChannelingBreakChance(channelingBreakChance);
	}

	public void setCharacterVersion(int characterVersion) {
		this.characterVersion = characterVersion;
	}

	public final void setCompleteQuests(List<Long> completeQuests) {
		this.completeQuests = completeQuests;
	}

	@Override
	public void setConstitution(int constitution) {
		cs.setConstitution(constitution);
	}

	public void setCooldowns(Map<String, List<Long>> cooldowns) {
		this.cooldowns = cooldowns;
	}

	public final void setCopper(long copper) {
		this.copper = copper;
	}

	@Override
	public void setCreatureCategory(CreatureCategory creatureCategory) {
		cs.setCreatureCategory(creatureCategory);
	}

	public final void setCredits(long credits) {
		this.credits = credits;
	}

	public void setCreditsPurchased(int creditsPurchased) {
		this.creditsPurchased = creditsPurchased;
	}

	public void setCreditsSpent(int creditsSpent) {
		this.creditsSpent = creditsSpent;
	}

	public final void setCurrentAbilityPoints(int currentAbilityPoints) {
		this.currentAbilityPoints = currentAbilityPoints;
	}

	public void setCurrentVaultSize(int currentVaultSize) {
		this.currentVaultSize = currentVaultSize;
	}

	@Override
	public void setDamageResistDeath(int damageResistDeath) {
		cs.setDamageResistDeath(damageResistDeath);
	}

	@Override
	public void setDamageResistFire(int damageResistFire) {
		cs.setDamageResistFire(damageResistFire);
	}

	@Override
	public void setDamageResistFrost(int damageResistFrost) {
		cs.setDamageResistFrost(damageResistFrost);
	}

	@Override
	public void setDamageResistMelee(int damageResistMelee) {
		cs.setDamageResistMelee(damageResistMelee);
	}

	@Override
	public void setDamageResistMystic(int damageResistMystic) {
		cs.setDamageResistMystic(damageResistMystic);
	}

	public final void setDateTimeFormat(DateFormat dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	@Override
	public void setDexterity(int dexterity) {
		cs.setDexterity(dexterity);
	}

	@Override
	public void setDisplayName(String displayName) {
		cs.setDisplayName(displayName);
	}

	@Override
	public void setEqAppearance(Appearance eqAppearance) {
		cs.setEqAppearance(eqAppearance);
	}

	public final void setEquipment(Map<Slot, Long> equipment) {
		this.equipment = equipment;
	}

	public final void setExp(long exp) {
		this.exp = exp;
	}

	public void setExtraAbilityPoints(int extraAbilityPoints) {
		this.extraAbilityPoints = extraAbilityPoints;
	}

	public void setFriends(Map<Long, String> friends) {
		this.friends = friends;
	}

	public void setGroveReturn(Location groveReturn) {
		this.groveReturn = groveReturn;
	}

	public void setGuilds(Map<Integer, Integer> guilds) {
		this.guilds = guilds;
	}

	public final void setHealth(int health) {
		this.health = health;
	}

	public void setHealthMod(int healthMod) {
		this.healthMod = healthMod;
	}

	public void setHengeList(List<Long> hengeList) {
		this.hengeList = hengeList;
	}

	public final void setHeroism(int heroism) {
		this.heroism = heroism;
	}

	public void setHideNameboard(boolean hideNameboard) {
		this.hideNameboard = hideNameboard;
	}

	public final void setInstance(Long instance) {
		this.instance = instance;
	}

	public void setInstanceScaler(String instanceScaler) {
		this.instanceScaler = instanceScaler;
	}

	public final void setInventory(Map<Integer, Long> inventory) {
		this.inventory = inventory;
	}

	public void setInvisibilityDistance(float invisibilityDistance) {
		this.invisibilityDistance = invisibilityDistance;
	}

	public final void setLastLogoff(Date lastLogoff) {
		this.lastLogoff = lastLogoff;
	}

	public final void setLastLogon(Date lastLogon) {
		this.lastLogon = lastLogon;
	}

	public final void setLastSession(long lastSession) {
		this.lastSession = lastSession;
	}

	public void setLastUnstickTime(long lastUnstickTime) {
		this.lastUnstickTime = lastUnstickTime;
	}

	public void setLastWarpTime(long lastWarpTime) {
		this.lastWarpTime = lastWarpTime;
	}

	@Override
	public void setLevel(int level) {
		cs.setLevel(level);
	}

	public final void setLocation(XYZ location) {
		this.location = location;
	}

	public final void setMaxSideKicks(int maxSideKicks) {
		this.maxSideKicks = maxSideKicks;
	}

	public final void setMight(int might) {
		this.might = might;
	}

	public void setMightCharges(int mightCharges) {
		this.mightCharges = mightCharges;
	}

	@Override
	public void setMightRegen(float mightRegen) {
		cs.setMightRegen(mightRegen);
	}

	public void setModAttackSpeed(double modAttackSpeed) {
		this.modAttackSpeed = modAttackSpeed;
	}

	public final void setModCastingSpeed(double modCastingSpeed) {
		this.modCastingSpeed = modCastingSpeed;
	}

	public final void setMode(PVPMode mode) {
		this.mode = mode;
	}

	@Override
	public void setOffhandWeaponDamage(int offhandWeaponDamage) {
		cs.setOffhandWeaponDamage(offhandWeaponDamage);
	}

	public final void setOrder(int order) {
		this.order = order;
	}

	public void setOriginalAppearance(Appearance originalAppearance) {
		this.originalAppearance = originalAppearance;
	}

	public final void setPrefs(Properties prefs) {
		this.prefs = prefs;
	}

	@Override
	public void setProfession(Profession profession) {
		cs.setProfession(profession);
	}

	@Override
	public void setPsyche(int psyche) {
		cs.setPsyche(psyche);
	}

	public void setPvpState(String pvpState) {
		this.pvpState = pvpState;
	}

	public void setPvpTeam(int pvpTeam) {
		this.pvpTeam = pvpTeam;
	}

	public void setRarity(Rarity rarity) {
		cs.setRarity(rarity);
	}

	public void setRepeatQuests(List<Long> repeatQuests) {
		this.repeatQuests = repeatQuests;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public final void setSecondsLogged(long secondsLogged) {
		this.secondsLogged = secondsLogged;
	}

	public void setSelectiveEqOverride(EternalObject selectiveEqOverride) {
		this.selectiveEqOverride = selectiveEqOverride;
	}

	public final void setSessionsLogged(int sessionsLogged) {
		this.sessionsLogged = sessionsLogged;
	}

	public void setSidekicks(List<Sidekick> sidekicks) {
		this.sidekicks = sidekicks;
	}

	@Override
	public void setSpirit(int spirit) {
		cs.setSpirit(spirit);
	}

	public final void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	@Override
	public void setStrength(int strength) {
		cs.setStrength(strength);
	}

	public final void setTimeFormat(DateFormat timeFormat) {
		this.timeFormat = timeFormat;
	}

	public final void setTimeLogged(long timeLogged) {
		this.timeLogged = timeLogged;
	}

	public final void setTotalAbilityPoints(int totalAbilityPoints) {
		this.totalAbilityPoints = totalAbilityPoints;
	}

	public final void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public final void setTranslocateDestination(String translocateDestination) {
		this.translocateDestination = translocateDestination;
	}

	public void setUnstickCount(int unstickCount) {
		this.unstickCount = unstickCount;
	}

	public final void setVisWeapon(int visWeapon) {
		this.visWeapon = visWeapon;
	}

	public final void setWill(int will) {
		this.will = will;
	}

	public void setWillCharges(int willCharges) {
		this.willCharges = willCharges;
	}

	public void setWillChargs(int willChargs) {
		this.willCharges = willChargs;
	}

	@Override
	public void setWillRegen(float willRegen) {
		cs.setWillRegen(willRegen);
	}

	public final void setZone(Long zone) {
		this.zone = zone;
	}

	public void sortInventoryByTypes(final GameItems items) {
		List<Long> l = new ArrayList<Long>(inventory.values());
		Collections.sort(l, new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				GameItem g1 = items.get(o1);
				GameItem g2 = items.get(o2);
				// Type
				int i = g1.getType().compareTo(g2.getType());
				if (i != 0) {
					return i;
				}
				// Now subtype
				if (g1.getType().equals(Type.WEAPON)) {
					i = g1.getWeaponType().compareTo(g2.getWeaponType());
				} else if (g1.getType().equals(Type.ARMOUR)) {
					i = g1.getEquipType().compareTo(g2.getEquipType());
				} else if (g1.getType().equals(Type.SPECIAL)) {
					i = g1.getSpecialItemType().compareTo(g2.getSpecialItemType());
				} else {
					i = 0;
				}
				if (i != 0) {
					return i;
				}
				// Now colour
				final ItemAppearance a1 = g1.getAppearance();
				final ItemAppearance a2 = g2.getAppearance();
				if (g1.getType().equals(Type.ARMOUR)) {
					if (g1.getEquipType().equals(EquipType.SHOULDERS) || g1.getEquipType().equals(EquipType.HEAD)
							|| g1.getEquipType().equals(EquipType.SHIELD)) {
						i = compareAttachments(a1, a2);
					} else {
						i = compareArmour(a1, a2);
					}
				} else if (g1.getType().equals(Type.WEAPON)) {
					i = compareAttachments(a1, a2);
				}
				if (i != 0) {
					return i;
				}
				// Now level
				i = new Integer(g1.getLevel()).compareTo(g2.getLevel());
				if (i != 0) {
					return i;
				}
				// Finally name
				return g1.getDisplayName().compareTo(g2.getDisplayName());
			}

			private int compareArmour(final ItemAppearance a1, final ItemAppearance a2) {
				List<RGB> c1 = a1 == null ? null : a1.getClothingColor();
				List<RGB> c2 = a1 == null ? null : a2.getClothingColor();
				return compareList(c1, c2);
			}

			private int compareAttachments(final ItemAppearance a1, final ItemAppearance a2) {
				List<AttachmentItem> l1 = a1.getAttachments();
				List<AttachmentItem> l2 = a1.getAttachments();
				List<RGB> c1 = l1 == null || l1.size() == 0 ? null : l1.get(0).getColors();
				List<RGB> c2 = l2 == null || l2.size() == 0 ? null : l2.get(0).getColors();
				return compareList(c1, c2);
			}

			private int compareList(List<RGB> c1, List<RGB> c2) {
				if (c1 == null) {
					if (c2 != null) {
						return -1;
					}
				} else {
					if (c2 == null) {
						return 1;
					}
				}
				return HSVComparator.DEFAULT.compare(c1, c2);
			}
		});
		inventory.clear();
		for (int i = 0; i < l.size(); i++) {
			inventory.put(i, l.get(i));
		}
	}

	@Override
	public String toString() {
		return getDisplayName() == null ? "<Id " + getEntityId() + ">" : getDisplayName();
	}

	@Override
	public final String getSubName() {
		return cs.getSubName();
	}

	public final void setSubName(String subName) {
		cs.setSubName(subName);
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("characterVersion=" + characterVersion);
		writer.println("ID=" + getEntityId());
		if (accountId > 0)
			writer.println("AccountID=" + accountId);
		writer.println("Order=" + order);
		if (instance != null)
			writer.println("Instance=" + instance);
		if (zone != null)
			writer.println("Zone=" + zone);
		//
		// NOTE
		//
		//
		// X/Y/Z locations here seem to be different to other locations
		// Going by eartheternal.info, it describes locations as X,Z,Y (with
		// Z being the low number usually seen). In this case, Y is low number
		writer.println("X=" + location.getX());
		writer.println("Y=" + location.getZ());
		writer.println("Z=" + location.getY());
		writer.println("Rotation=" + rotation);
		if (StringUtils.isNotBlank(instanceScaler))
			writer.println("InstanceScaler=" + instanceScaler);
		writer.println("StatusText=" + Util.nonNull(statusText));
		writer.println("SecondsLogged=" + secondsLogged);
		writer.println("SessionsLogged=" + sessionsLogged);
		writer.println("TimeLogged=" + Util.formatElapsedTime(timeLogged));
		if (lastSession > 0) {
			writer.println("LastSession=" + Util.formatElapsedTime(lastSession));
		}
		if (lastLogon != null) {
			writer.println("LastLogOn=" + dateTimeFormat.format(lastLogon));
		}
		if (lastLogoff != null) {
			writer.println("LastLogOff=" + dateTimeFormat.format(lastLogoff));
		}
		writer.println("OriginalAppearance=" + originalAppearance.toString());
		writer.println("CurrentVaultSize=" + currentVaultSize);
		writer.println("CreditsPurchased=" + creditsPurchased);
		writer.println("CreditsSpent=" + creditsSpent);
		writer.println("ExtraAbilityPoints=" + extraAbilityPoints);
		if (groveReturn != null)
			writer.println("GroveReturn=" + groveReturn);
		if (bindReturn != null)
			writer.println("BindReturn=" + bindReturn);
		writer.println("LastWarpTime=" + lastWarpTime);
		writer.println("UnstickCount=" + unstickCount);
		writer.println("LastUnstickTime=" + lastUnstickTime);
		writer.writeGroupedSeparatedList(hengeList, 10, ",");
		writer.writeGroupedSeparatedList(abilities, 10, ",");
		for (Map.Entry<Long, String> en : friends.entrySet()) {
			writer.println("FriendList=" + en.getKey() + "," + en.getValue());
		}
		for (Map.Entry<Integer, Integer> en : guilds.entrySet()) {
			writer.println("GuildList=" + en.getKey() + "," + en.getValue());
		}
		writer.println("MaxSidekicks=" + maxSideKicks);
		if (!PVPMode.PVE.equals(mode)) {
			writer.println("Mode=" + mode);
		}
		for (Sidekick s : sidekicks) {
			writer.println("Sidekick=" + s);
		}
		writer.println("");
		writer.println("[STATS]");
		cs.write(writer);
		if (pvpTeam > 0)
			writer.println("pvp_team" + pvpTeam);
		if (pvpState != null)
			writer.println("pvp_state" + pvpState);
		if (StringUtils.isNotBlank(alternateIdleAnim))
			writer.println("alternate_idle_anim=" + alternateIdleAnim);
		if (selectiveEqOverride != null) {
			writer.println("selective_eq_override=" + selectiveEqOverride.toString());
		}
		writer.println("health_mod=" + healthMod);
		writer.println("base_luck=" + baseLuck);
		writer.println("vis_weapon=" + visWeapon);
		writer.println("health=" + health);
		writer.println("will=" + will);
		writer.println("might=" + might);
		writer.println("will_charges=" + willCharges);
		writer.println("might_charges=" + mightCharges);
		if (invisibilityDistance > 0)
			writer.println("invisibility_distance=" + invisibilityDistance);
		writer.println("translocate_destination=" + translocateDestination);
		writer.println("base_stats=" + Util.toCommaSeparatedList(baseStats));
		writer.println("heroism=" + heroism);
		writer.println("experience=" + exp);
		writer.println("total_ability_points=" + totalAbilityPoints);
		writer.println("current_ability_points=" + currentAbilityPoints);
		writer.println("copper=" + copper);
		writer.println("credits=" + credits);
		writer.println("mod_casting_speed=" + modCastingSpeed);
		writer.println("mod_attack_speed=" + modAttackSpeed);
		writer.println("total_size=" + totalSize);
		writer.println("appearance_override=" + (appearanceOverride ? "1" : "0"));
		writer.println("hide_nameboard=" + hideNameboard);
		writer.println("");
		writer.println("[PREFS]");
		for (Object key : prefs.keySet()) {
			writer.println(key.toString() + "=" + prefs.getProperty((String) key));
		}
		writer.println("");
		writer.println("[INV]");
		for (Integer slot : inventory.keySet()) {
			writer.println("inv=" + slot + "," + inventory.get(slot));
		}
		for (Slot slot : equipment.keySet()) {
			writer.println("eq=" + slot.getCode() + "," + equipment.get(slot));
		}
		writer.println("");
		writer.println("[QUEST]");
		for (Long questId : activeQuests.keySet()) {
			final List<Long> questData = activeQuests.get(questId);
			// if(questData.size() > 0 &&
			// !questData.iterator().next().equals(0l)) {
			writer.println("active=" + Util.toCommaSeparatedList(questData));
			// }
		}
		if (completeQuests.size() > 0) {
			for (int i = 0; i < completeQuests.size(); i += 10) {
				writer.println("complete="
						+ Util.toCommaSeparatedList(completeQuests.subList(i, Math.min(completeQuests.size(), i + 10))));
			}
		}
		writer.println("");
		writer.println("[COOLDOWN]");
		for (Map.Entry<String, List<Long>> en : cooldowns.entrySet()) {
			writer.println(en.getKey() + "=" + Util.toCommaSeparatedList(en.getValue()));
		}
		if (!activeAbilities.isEmpty()) {
			writer.println("");
			writer.println("[ABILITIES]");
			for (CharacterAbility a : activeAbilities) {
				writer.println("Active=" + a);
			}
		}
		// private EternalObject selectiveEqOverride;
	}
}
