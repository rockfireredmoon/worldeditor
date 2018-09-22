package org.icemoon.eartheternal.common;

public enum LootRarity {
	DENOMINATOR(-1, new Color(0x20, 0x20, 0x20), new Color(0x20, 0x20, 0x20)), 
	FLAT(0, new Color(0x20, 0x20, 0x20), new Color(0x90, 0x90, 0x90)),
	UNCOMMON(2, new Color(0, 212, 0), Color.WHITE), 
	RARE(3, new Color(0, 0x66, 0xff), Color.WHITE), 
	EPIC(4, new Color(0xcc, 0, 0xcc), Color.WHITE), 
	LEGENDARY(5, new Color(0xff, 0xff, 0x00), Color.BLACK), 
	ARTIFACT(6, new Color(0xff, 0x44, 0x11), Color.WHITE);
	public static LootRarity fromCode(int code) {
		for (LootRarity type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		Log.todo("Item", "Unhandle Quality code " + code);
		return null;
	}

	private int code;
	private RGB background;
	private RGB foreground;

	private LootRarity(int code) {
		this(code, Color.WHITE, Color.BLACK);
	}

	private LootRarity(int code, RGB background, RGB foreground) {
		this.code = code;
		this.background = background;
		this.foreground = foreground;
	}

	public RGB getBackground() {
		return background;
	}

	public int getCode() {
		return code;
	}

	public RGB getForeground() {
		return foreground;
	}

	@Override
	public String toString() {
		return Util.toEnglish(name(), true);
	}

	public boolean isRarityUsed() {
		return this == LootRarity.DENOMINATOR || this == LootRarity.FLAT;
	}
}
