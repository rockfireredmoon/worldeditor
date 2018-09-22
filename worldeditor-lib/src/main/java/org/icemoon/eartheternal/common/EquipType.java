package org.icemoon.eartheternal.common;

import org.icemoon.eartheternal.common.Appearance.ClothingType;

public enum EquipType {
	NONE(0), WEAPON_1H(1), WEAPON_1H_UNIQUE(2), WEAPON_1H_MAIN(3), WEAPON_1H_OFF(4), WEAPON_2H(5), RANGED_WEAPON(6), SHIELD(7), HEAD(
			8), COLLAR(9, ClothingType.COLLAR), SHOULDERS(10), CHEST(11, ClothingType.CHEST), ARMS(12, ClothingType.ARMS), HANDS(
			13, ClothingType.GLOVES), BELT(14, ClothingType.BELT), LEGS(15, ClothingType.LEGGINGS), FEET(16, ClothingType.BOOTS), RING(
			17), RING_UNIQUE(18), AMULET(19), FOCUS_FIRE(20), FOCUS_FROST(21), FOCUS_MYSTIC(22), FOCUS_DEATH(23), CONTAINER(24), COSMETIC_SHOULDER(
			25), COSMETIC_HIP(26), RED_CHARM(27), GREEN_CHARM(28), BLUE_CHARM(29), ORANGE_CHARM(30), YELLOW_CHARM(31), PURPLE_CHARM(
			32);

	public static EquipType fromCode(int code) {
		for (EquipType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		Log.todo("Item", "Unhandle EquipType code " + code);
		return null;
	}
	int code;

	private ClothingType clothingType;

	private EquipType(int code) {
		this(code, null);
	}

	private EquipType(int code, ClothingType clothingType) {
		this.code = code;
		this.clothingType = clothingType;
	}

	public int getCode() {
		return code;
	}
	
	public String getDefaultIcon() {
		switch (this) {
		case FEET:
			return "Icon-32-C_Armor-Feet01.png";
		case CHEST:
			return "Icon-32-C_Armor-Chest01.png";
		case ARMS:
			return "Icon-32-Armor-Arms01.png";
		case LEGS:
			return "Icon-32-C_Armor-Legs01.png";
		case COLLAR:
			return "Icon-32-Armor-Neck01.png";
		case BELT:
			return "Icon-32-Armor-Belts01.png";
		case HANDS:
			return "Icon-32-C_Armor-Hands01.png";
		case HEAD:
			return "Icon-32-C_Armor-Head01.png";
		case WEAPON_1H:
		case WEAPON_1H_OFF:
			return "Icon-32-1hSword-Medium1.png";
		case WEAPON_2H:
			return "Icon-32-Sword9.png";
		case WEAPON_1H_UNIQUE:
			return "Icon-32-Sword1.png";
		case SHOULDERS:
			return "Icon-32-C_Armor-Shldr04.png";
		}
		return null;
	}

	public ClothingType toClothingItemType() {
		return clothingType;
	}

	public Slot toSlot() {
		switch (this) {
		case WEAPON_2H:
		case WEAPON_1H:
		case WEAPON_1H_UNIQUE:
		case WEAPON_1H_MAIN:
			return Slot.WEAPON_1;
		case WEAPON_1H_OFF:
			return Slot.WEAPON_2;
		case RANGED_WEAPON:
			return Slot.WEAPON_3;
		case HEAD:
			return Slot.HEAD;
		case COLLAR:
			return Slot.NECK;
		case SHOULDERS:
			return Slot.SHOULDERS;
		case CHEST:
			return Slot.CHEST;
		case ARMS:
			return Slot.ARMS;
		case HANDS:
			return Slot.HANDS;
		case BELT:
			return Slot.WAIST;
		case LEGS:
			return Slot.LEGS;
		case FEET:
			return Slot.FEET;
		case RING:
			return Slot.RING_1;
		case RING_UNIQUE:
			return Slot.RING_2;
		case AMULET:
			return Slot.AMULET;
		case CONTAINER:
			return Slot.BAG_1;
		}
		return null;
	}

	@Override
	public String toString() {
		return Util.toEnglish(name(), true);
	}
}