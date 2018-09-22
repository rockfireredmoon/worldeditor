package org.icemoon.eartheternal.common;

public enum WeaponType {
	NONE(0), SMALL(1), ONE_HAND(2), TWO_HAND(3), POLE(4), WAND(5), BOW(6), THROWN(7), ARCANE_TOTEM(8);
	public static WeaponType fromCode(int code) {
		for (WeaponType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		Log.todo("Item", "Unhandle WeaponType code " + code);
		return null;
	}

	private int code;

	private WeaponType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return Util.toEnglish(name(), true);
	}
}