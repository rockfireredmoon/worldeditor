package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.text.ParseException;

public class CreatureSupport implements Serializable {
	private static final long serialVersionUID = 1L;
	private int strength;
	private int dexterity;
	private int constitution;
	private int psyche;
	private int spirit;
	private float willRegen = 1;
	private float mightRegen = 1;
	private int offhandWeaponDamage;
	private int castingSetbackChance;
	private int channelingBreakChance;
	private int level;
	private String displayName;
	private Appearance appearance = new Appearance();
	private CreatureCategory creatureCategory = CreatureCategory.ANIMAL;
	private int damageResistMelee;
	private int damageResistFire;
	private int damageResistFrost;
	private int damageResistMystic;
	private int damageResistDeath;
	private Profession profession = Profession.DRUID;
	private Appearance eqAppearance = new Appearance();
	private long copper;
	private String subName;
	private Rarity rarity;
	private int minHealthPercent;
	private int maxHealthPercent = 100;

	public CreatureSupport() {
		super();
	}

	public int getMinHealthPercent() {
		return minHealthPercent;
	}

	public void setMinHealthPercent(int minHealthPercent) {
		this.minHealthPercent = minHealthPercent;
	}

	public int getMaxHealthPercent() {
		return maxHealthPercent;
	}

	public void setMaxHealthPercent(int maxHealthPercent) {
		this.maxHealthPercent = maxHealthPercent;
	}

	public final Appearance getAppearance() {
		return appearance;
	}

	public final int getCastingSetbackChance() {
		return castingSetbackChance;
	}

	public final int getChannelingBreakChance() {
		return channelingBreakChance;
	}

	public final int getConstitution() {
		return constitution;
	}

	public final long getCopper() {
		return copper;
	}

	public final CreatureCategory getCreatureCategory() {
		return creatureCategory;
	}

	public final int getDamageResistDeath() {
		return damageResistDeath;
	}

	public final int getDamageResistFire() {
		return damageResistFire;
	}

	public final int getDamageResistFrost() {
		return damageResistFrost;
	}

	public final int getDamageResistMelee() {
		return damageResistMelee;
	}

	public final int getDamageResistMystic() {
		return damageResistMystic;
	}

	public final int getDexterity() {
		return dexterity;
	}

	public final String getDisplayName() {
		return displayName;
	}

	public final Appearance getEqAppearance() {
		return eqAppearance;
	}

	public String getIcon() {
		CreatureCategory cat = getCreatureCategory();
		if (cat == null) {
			cat = CreatureCategory.INANIMATE;
		}
		return cat.getIcon();
	}

	public final int getLevel() {
		return level;
	}

	public final int getOffhandWeaponDamage() {
		return offhandWeaponDamage;
	}

	public final Profession getProfession() {
		return profession;
	}

	public final int getPsyche() {
		return psyche;
	}

	public final Rarity getRarity() {
		return rarity;
	}

	public final int getSpirit() {
		return spirit;
	}

	public final int getStrength() {
		return strength;
	}

	public final String getSubName() {
		return subName;
	}

	public final float getMightRegen() {
		return mightRegen;
	}

	public final float getWillRegen() {
		return willRegen;
	}

	public boolean set(String name, String value, String section) {
		if (name.equals("strength")) {
			strength = Integer.parseInt(value);
		} else if (name.equals("dexterity")) {
			dexterity = Integer.parseInt(value);
		} else if (name.equals("constitution")) {
			constitution = Integer.parseInt(value);
		} else if (name.equals("psyche")) {
			psyche = Integer.parseInt(value);
		} else if (name.equals("spirit")) {
			spirit = Integer.parseInt(value);
		} else if (name.equals("will_regen")) {
			willRegen = Float.parseFloat(value);
		} else if (name.equals("might_regen")) {
			mightRegen = Float.parseFloat(value);
		} else if (name.equals("damage_resist_melee")) {
			damageResistMelee = Integer.parseInt(value);
		} else if (name.equals("damage_resist_fire")) {
			damageResistFire = Integer.parseInt(value);
		} else if (name.equals("damage_resist_frost")) {
			damageResistFrost = Integer.parseInt(value);
		} else if (name.equals("damage_resist_mystic")) {
			damageResistMystic = Integer.parseInt(value);
		} else if (name.equals("damage_resist_death")) {
			damageResistDeath = Integer.parseInt(value);
		} else if (name.equals("offhand_weapon_damage")) {
			offhandWeaponDamage = Integer.parseInt(value);
		} else if (name.equals("casting_setback_chance")) {
			castingSetbackChance = Integer.parseInt(value);
		} else if (name.equals("channeling_break_chance")) {
			channelingBreakChance = Integer.parseInt(value);
		} else if (name.equals("appearance")) {
			try {
				appearance = new Appearance(value);
			} catch (ParseException e) {
				Log.error("Creature", "Failed to parse appearance.", e);
			}
		} else if (name.equals("eq_appearance")) {
			try {
				eqAppearance = new Appearance(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (name.equals("level")) {
			level = Integer.parseInt(value);
		} else if (name.equals("display_name")) {
			displayName = value;
		} else if (name.equals("profession")) {
			profession = Profession.fromCode(Integer.parseInt(value));
		} else if (name.equals("creature_category")) {
			creatureCategory = CreatureCategory.fromCode(value);
		} else if (name.equals("copper")) {
			copper = Long.parseLong(value);
		} else if (name.equals("sub_name")) {
			subName = value;
		} else if (name.equals("rarity")) {
			rarity = Rarity.fromCode(Integer.parseInt(value));
		} else if (name.equals("min_health_pc")) {
			minHealthPercent = Integer.parseInt(value);
		} else if (name.equals("max_health_pc")) {
			maxHealthPercent = Integer.parseInt(value);
		} else if (!name.equals("") || !value.equals("")) {
			return false;
		}
		return true;
	}

	public final void setAppearance(Appearance appearance) {
		this.appearance = appearance;
	}

	public final void setCastingSetbackChance(int castingSetbackChance) {
		this.castingSetbackChance = castingSetbackChance;
	}

	public final void setChannelingBreakChance(int channelingBreakChance) {
		this.channelingBreakChance = channelingBreakChance;
	}

	public final void setConstitution(int constitution) {
		this.constitution = constitution;
	}

	public final void setCopper(long copper) {
		this.copper = copper;
	}

	public final void setCreatureCategory(CreatureCategory creatureCategory) {
		this.creatureCategory = creatureCategory;
	}

	public final void setDamageResistDeath(int damageResistDeath) {
		this.damageResistDeath = damageResistDeath;
	}

	public final void setDamageResistFire(int damageResistFire) {
		this.damageResistFire = damageResistFire;
	}

	public final void setDamageResistFrost(int damageResistFrost) {
		this.damageResistFrost = damageResistFrost;
	}

	public final void setDamageResistMelee(int damageResistMelee) {
		this.damageResistMelee = damageResistMelee;
	}

	public final void setDamageResistMystic(int damageResistMystic) {
		this.damageResistMystic = damageResistMystic;
	}

	public final void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	public final void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public final void setEqAppearance(Appearance eqAppearance) {
		this.eqAppearance = eqAppearance;
	}

	public final void setLevel(int level) {
		this.level = level;
	}

	public final void setMightRegen(float mightRegen) {
		this.mightRegen = mightRegen;
	}

	public final void setOffhandWeaponDamage(int offhandWeaponDamage) {
		this.offhandWeaponDamage = offhandWeaponDamage;
	}

	public final void setProfession(Profession profession) {
		this.profession = profession;
	}

	public final void setPsyche(int psyche) {
		this.psyche = psyche;
	}

	public final void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}

	public final void setSpirit(int spirit) {
		this.spirit = spirit;
	}

	public final void setStrength(int strength) {
		this.strength = strength;
	}

	public final void setSubName(String subName) {
		this.subName = subName;
	}

	public final void setWillRegen(float willRegen) {
		this.willRegen = willRegen;
	}

	public void write(INIWriter writer) {
		if (strength > 0) {
			writer.println("strength=" + strength);
		}
		if (dexterity > 0) {
			writer.println("dexterity=" + dexterity);
		}
		if (constitution > 0) {
			writer.println("constitution=" + constitution);
		}
		if (psyche > 0) {
			writer.println("psyche=" + psyche);
		}
		if (spirit > 0) {
			writer.println("spirit=" + spirit);
		}
		if (damageResistMelee > 0) {
			writer.println("damage_resist_melee=" + damageResistMelee);
		}
		if (damageResistFire > 0) {
			writer.println("damage_resist_fire=" + damageResistFire);
		}
		if (damageResistFrost > 0) {
			writer.println("damage_resist_frost=" + damageResistFrost);
		}
		if (damageResistMystic > 0) {
			writer.println("damage_resist_mystic=" + damageResistMystic);
		}
		if (damageResistDeath > 0) {
			writer.println("damage_resist_death=" + damageResistMelee);
		}
		if (willRegen != 1)
			writer.println("will_regen=" + Util.compact(willRegen));
		if (mightRegen != 1)
			writer.println("might_regen=" + Util.compact(mightRegen));
		if (offhandWeaponDamage > 0) {
			writer.println("offhand_weapon_damage=" + offhandWeaponDamage);
		}
		if (castingSetbackChance > 0) {
			writer.println("casting_setback_chance=" + castingSetbackChance);
		}
		if (channelingBreakChance > 0) {
			writer.println("channeling_break_chance=" + channelingBreakChance);
		}
		if (appearance != null && appearance.getName() != null) {
			writer.println("appearance=" + appearance.toString());
		}
		if (eqAppearance != null && eqAppearance.getName() != null) {
			writer.println("eq_appearance=" + eqAppearance.toString());
		}
		writer.println("level=" + level);
		writer.println("display_name=" + displayName);
		if (!Util.isNullOrEmpty(subName)) {
			writer.println("sub_name=" + subName);
		}
		writer.println("profession=" + profession.getCode());
		writer.println("creature_category=" + creatureCategory.getCode());
		if (rarity != null) {
			writer.println("rarity=" + rarity.getCode());
		}
		if (minHealthPercent != 0)
			writer.println("min_health_pc=" + minHealthPercent);
		if (maxHealthPercent != 100)
			writer.println("max_health_pc=" + maxHealthPercent);
	}
}