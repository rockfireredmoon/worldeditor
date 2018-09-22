package org.icemoon.eartheternal.common;

public interface BaseCreature extends Entity<Long> {
	Appearance getAppearance();

	int getCastingSetbackChance();

	int getChannelingBreakChance();

	int getConstitution();

	CreatureCategory getCreatureCategory();

	int getDamageResistDeath();

	int getDamageResistFire();

	int getDamageResistFrost();

	int getDamageResistMelee();

	int getDamageResistMystic();

	int getDexterity();

	String getDisplayName();

	Appearance getEqAppearance();

	String getIcon();

	int getLevel();

	int getOffhandWeaponDamage();

	Profession getProfession();

	int getPsyche();

	int getSpirit();

	int getStrength();

	String getSubName();

	float getMightRegen();

	float getWillRegen();

	void setAppearance(Appearance appearance);

	void setCastingSetbackChance(int castingSetbackChance);

	void setChannelingBreakChance(int channelingBreakChance);

	void setConstitution(int constitution);

	void setCreatureCategory(CreatureCategory creatureCategory);

	void setDamageResistDeath(int damageResistDeath);

	void setDamageResistFire(int damageResistFire);

	void setDamageResistFrost(int damageResistFrost);

	void setDamageResistMelee(int damageResistMelee);

	void setDamageResistMystic(int damageResistMystic);

	void setDexterity(int dexterity);

	void setDisplayName(String displayName);

	void setEqAppearance(Appearance eqAppearance);

	void setLevel(int level);

	void setMightRegen(float mightRegen);

	void setOffhandWeaponDamage(int offhandWeaponDamage);

	void setProfession(Profession profession);

	void setPsyche(int psyche);

	void setSpirit(int spirit);

	void setStrength(int strength);

	void setWillRegen(float willRegen);

	void setSubName(String subName);

	Rarity getRarity();

	void setRarity(Rarity rarity);
}
