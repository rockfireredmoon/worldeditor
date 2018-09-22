package org.icemoon.eartheternal.common;

public enum CreatureCategory {
	ANIMAL("Animal", "yellow"), DEMON("Demon", "red"), DIVINE("Divine", "white"), DRAGONKIN("Dragonkin", "purple"), ELEMENTAL(
			"Elemental", "blue"), INANIMATE("Inanimate", "orange"), MAGICAL("Magical", "cyan"), MORTAL("Mortal", "green"), UNLIVING(
			"Unliving", "grey");

	public static CreatureCategory fromCode(String code) {
		for (CreatureCategory type : values()) {
			if (type.code.equals(code)) {
				return type;
			}
		}
		Log.todo("Item", "Unhandle CreatureCategory code " + code);
		return null;
	}
	private String code;

	private String color;

	private CreatureCategory(String code, String color) {
		this.code = code;
		this.color = color;
	}

	public String getCode() {
		return code;
	}

	public String getColor() {
		return color;
	}

	public String getIcon() {
		return "Icon-" + code + "-Portrait.png";
	}

	@Override
	public String toString() {
		return Util.toEnglish(name(), true);
	}
}