package org.icemoon.eartheternal.common;

public enum Slot {
	WEAPON_1(0), WEAPON_2(1), WEAPON_3(2), HEAD(3), NECK(4), SHOULDERS(5), CHEST(6), ARMS(7), HANDS(8), WAIST(9), LEGS(10), FEET(
			11), RING_1(12), RING_2(13), AMULET(14), BAG_1(19), BAG_2(20), BAG_3(21), BAG_4(22);

	public static Slot fromCode(int code) {
		for (Slot type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		Log.todo("Item", "Unhandle Slot code " + code);
		return null;
	}

	private int code;
	
	private Slot(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public EquipType toEquipType() {
		switch(this) {
		case HEAD:
			return EquipType.HEAD;
		case NECK:
			return EquipType.COLLAR;
		case SHOULDERS:
			return EquipType.SHOULDERS;
		case CHEST:
			return EquipType.CHEST;
		case ARMS:
			return EquipType.ARMS;
		case HANDS:
			return EquipType.HANDS;
		case WAIST:
			return EquipType.BELT;
		case LEGS:
			return EquipType.LEGS;
		case FEET:
			return EquipType.FEET;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return Util.toEnglish(name(), true);
	}
}