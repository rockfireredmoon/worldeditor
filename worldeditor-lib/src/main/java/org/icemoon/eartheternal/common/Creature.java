package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.icemoon.eartheternal.common.Appearance.ClothingType;

@SuppressWarnings("serial")
public class Creature extends AbstractMultiINIFileEntity<Long, IDatabase> implements BaseCreature {
	private String aiPackage;
	private int baseDamageMelee;
	private int baseHealth;
	private boolean aggroPlayers;
	private List<Hint> hints = new ArrayList<Hint>();
	private CreatureSupport cs = new CreatureSupport();
	private Map<String, String> extraData = new HashMap<String, String>();
	private boolean namedMob;
	private String dropRateProfile;
	private float dropRateMult = 1;
	private float totalSize = 1;
	
	private List<Effect> effects = new ArrayList<Effect>();
	{
		setLevel(1);
	}

	public Creature() {
		this(null);
	}

	public Creature(IDatabase database) {
		super(database);
	}

	public final boolean isNamedMob() {
		return namedMob;
	}

	public final void setNamedMob(boolean namedMob) {
		this.namedMob = namedMob;
	}

	public final String getDropRateProfile() {
		return dropRateProfile;
	}

	public final void setDropRateProfile(String dropRateProfile) {
		this.dropRateProfile = dropRateProfile;
	}

	public final float getDropRateMult() {
		return dropRateMult;
	}

	public final void setDropRateMult(float dropRateMult) {
		this.dropRateMult = dropRateMult;
	}

	public final String getAiPackage() {
		return aiPackage;
	}

	@Override
	public Appearance getAppearance() {
		return cs.getAppearance();
	}

	public final int getBaseDamageMelee() {
		return baseDamageMelee;
	}

	public final int getBaseHealth() {
		return baseHealth;
	}

	@Override
	public int getCastingSetbackChance() {
		return cs.getCastingSetbackChance();
	}

	@Override
	public int getChannelingBreakChance() {
		return cs.getChannelingBreakChance();
	}

	@Override
	public int getConstitution() {
		return cs.getConstitution();
	}

	@Override
	public CreatureCategory getCreatureCategory() {
		return cs.getCreatureCategory();
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

	@Override
	public int getDexterity() {
		return cs.getDexterity();
	}

	@Override
	public String getDisplayName() {
		return cs.getDisplayName();
	}

	public final List<Effect> getEffects() {
		return effects;
	}

	@Override
	public Appearance getEqAppearance() {
		return cs.getEqAppearance();
	}

	public final Map<String, String> getExtraData() {
		return extraData;
	}

	public final List<Hint> getHints() {
		return hints;
	}

	@Override
	public String getIcon() {
		if (getHints().contains(Hint.VENDOR)) {
			return "shop.png";
		}
		return cs.getIcon();
	}

	@Override
	public int getLevel() {
		return cs.getLevel();
	}

	@Override
	public int getOffhandWeaponDamage() {
		return cs.getOffhandWeaponDamage();
	}

	@Override
	public Profession getProfession() {
		return cs.getProfession();
	}

	@Override
	public int getPsyche() {
		return cs.getPsyche();
	}

	@Override
	public int getSpirit() {
		return cs.getSpirit();
	}

	@Override
	public int getStrength() {
		return cs.getStrength();
	}

	public final float getTotalSize() {
		return totalSize;
	}

	public final boolean isAggroPlayers() {
		return aggroPlayers;
	}

	@Override
	public float getMightRegen() {
		return cs.getMightRegen();
	}

	public boolean isWearingAsset(ClothingType type, String assetName) {
		if (type != null) {
			if (getAppearance().getClothing() != null) {
				for (ClothingItem it : getAppearance().getClothing()) {
					if (it.getType().equals(type) && it.getAsset().equalsIgnoreCase(assetName)) {
						return true;
					}
				}
			}
		} else {
			for (AttachmentItem a : getAppearance().getAttachments()) {
				if (a.getAsset().equalsIgnoreCase(assetName)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public float getWillRegen() {
		return cs.getWillRegen();
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("ID")) {
			setEntityId(Long.parseLong(value));
		} else if (name.equals("base_damage_melee")) {
			baseDamageMelee = Integer.parseInt(value);
		} else if (name.equals("ai_package")) {
			aiPackage = value.equals("nothing") ? null : value;
		} else if (name.equals("base_health")) {
			baseHealth = Integer.parseInt(value);
		} else if (name.equals("defHints")) {
			int defHints = Integer.parseInt(value);
			for (Hint hint : Hint.values()) {
				if ((defHints & hint.getVal()) != 0) {
					hints.add(hint);
				}
			}
		} else if (name.equals("aggro_players")) {
			aggroPlayers = "1".equals(value);
		} else if (name.equals("total_size")) {
			totalSize = Float.parseFloat(value);
		} else if (name.equals("Effects")) {
			StringTokenizer t = new StringTokenizer(value, ",");
			while (t.hasMoreTokens()) {
				effects.add(Effect.valueOf(t.nextToken()));
			}
		} else if (name.equals("ExtraData")) {
			namedMob = false;
			dropRateProfile = null;
			dropRateMult = 1;
			for (String s : value.split("&")) {
				String k = s;
				String v = null;
				int idx = s.indexOf('=');
				if (idx != -1) {
					k = s.substring(0, idx);
					v = s.substring(idx + 1);
				}
				if (k.equalsIgnoreCase("dropratemult")) {
					dropRateMult = Float.parseFloat(v);
				} else if (k.equalsIgnoreCase("namedmob")) {
					namedMob = true;
				} else if (k.equalsIgnoreCase("droprateprofile")) {
					dropRateProfile = v.equalsIgnoreCase("") ? null : v;
				} else {
					extraData.put(k, v);
				}
			}
		} else {
			if (!cs.set(name, value, section)) {
				Log.todo(getClass().getName() + " (" + getFile() + ")", "Unhandle property " + name + " = " + value);
			}
		}
	}

	public final void setAggroPlayers(boolean aggroPlayers) {
		this.aggroPlayers = aggroPlayers;
	}

	public final void setAiPackage(String aiPackage) {
		this.aiPackage = aiPackage;
	}

	@Override
	public void setAppearance(Appearance appearance) {
		cs.setAppearance(appearance);
	}

	public final void setBaseDamageMelee(int baseDamageMelee) {
		this.baseDamageMelee = baseDamageMelee;
	}

	public final void setBaseHealth(int baseHealth) {
		this.baseHealth = baseHealth;
	}

	@Override
	public void setCastingSetbackChance(int castingSetbackChance) {
		cs.setCastingSetbackChance(castingSetbackChance);
	}

	@Override
	public void setChannelingBreakChance(int channelingBreakChance) {
		cs.setChannelingBreakChance(channelingBreakChance);
	}

	@Override
	public void setConstitution(int constitution) {
		cs.setConstitution(constitution);
	}

	@Override
	public void setCreatureCategory(CreatureCategory creatureCategory) {
		cs.setCreatureCategory(creatureCategory);
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

	public final void setHints(List<Hint> hints) {
		this.hints = hints;
	}

	@Override
	public void setLevel(int level) {
		cs.setLevel(level);
	}

	@Override
	public void setMightRegen(float mightRegen) {
		cs.setMightRegen(mightRegen);
	}

	@Override
	public void setOffhandWeaponDamage(int offhandWeaponDamage) {
		cs.setOffhandWeaponDamage(offhandWeaponDamage);
	}

	@Override
	public void setProfession(Profession profession) {
		cs.setProfession(profession);
	}

	@Override
	public void setPsyche(int psyche) {
		cs.setPsyche(psyche);
	}

	@Override
	public void setSpirit(int spirit) {
		cs.setSpirit(spirit);
	}

	@Override
	public void setStrength(int strength) {
		cs.setStrength(strength);
	}

	@Override
	public final String getSubName() {
		return cs.getSubName();
	}

	public final void setSubName(String subName) {
		cs.setSubName(subName);
	}

	public final void setTotalSize(float totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public void setWillRegen(float willRegen) {
		cs.setWillRegen(willRegen);
	}

	@Override
	public String toString() {
		return getDisplayName() == null ? "<Id " + getEntityId() + ">" : getDisplayName();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		int v = 0;
		for (Hint hint : hints) {
			v += hint.getVal();
		}
		if (v > 0) {
			writer.println("defHints=" + v);
		}
		writer.println("ID=" + getEntityId());
		cs.write(writer);
		if (baseDamageMelee > 0) {
			writer.println("base_damage_melee=" + baseDamageMelee);
		}
		if (baseHealth > 0) {
			writer.println("base_health=" + baseHealth);
		}
		if (aggroPlayers) {
			writer.println("aggro_players=" + Util.toBooleanString(aggroPlayers));
		}
		StringBuilder b = new StringBuilder();
		Map<String, String> ex = new HashMap<String, String>(extraData);
		if (namedMob)
			ex.put("namedmob", "");
		else
			ex.remove("namedmob");
		if (dropRateMult != 1)
			ex.put("dropratemult", String.valueOf(dropRateMult));
		else
			ex.remove("dropratemult");
		if (StringUtils.isNotBlank(dropRateProfile)) {
			ex.put("droprateprofile", dropRateProfile);
		} else
			ex.remove("droprateprofile");
		for (Map.Entry<String, String> en : ex.entrySet()) {
			if (b.length() > 0)
				b.append("&");
			b.append(en.getKey());
			b.append("=");
			b.append(en.getValue());
		}
		if (b.length() > 0) {
			writer.println("ExtraData=" + b);
		}
		writer.println("ai_package=" + (aiPackage == null ? "nothing" : aiPackage));
		if (totalSize != 1) {
			writer.println("total_size=" + Util.compact(totalSize));
		}
		if (!effects.isEmpty()) {
			b.setLength(0);
			for (Effect e : effects) {
				if (b.length() > 0)
					b.append(",");
				b.append(e.name());
			}
			writer.println("Effects=" + b.toString());
		}
	}

	@Override
	public Rarity getRarity() {
		return cs.getRarity();
	}

	@Override
	public void setRarity(Rarity rarity) {
		cs.setRarity(rarity);
	}

	@Override
	public int getMinHealthPercent() {
		return cs.getMinHealthPercent();
	}

	@Override
	public int getMaxHealthPercent() {
		return cs.getMaxHealthPercent();
	}

	@Override
	public void setMinHealthPercent(int minHealthPercent) {
		cs.setMinHealthPercent(minHealthPercent);
	}

	@Override
	public void setMaxHealthPercent(int maxHealthPercent) {
		cs.setMaxHealthPercent(maxHealthPercent);
	}
}
