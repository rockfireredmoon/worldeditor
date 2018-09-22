package org.icemoon.eartheternal.common;

public enum Profession {
	KNIGHT(1), ROGUE(2), MAGE(3), DRUID(4), UNKNOWN_1(5), UNKNOWN_2(6);

	public static Profession fromCode(int code) {
		for (Profession type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		Log.todo("Item", "Unhandle Profession code " + code);
		return null;
	}

	private int code;

	private Profession(int code) {
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