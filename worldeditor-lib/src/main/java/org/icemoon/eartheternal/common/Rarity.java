package org.icemoon.eartheternal.common;

public enum Rarity {
	LEGENDARY(0, new Color(	0xff, 0xff, 0x00), Color.BLACK), 
	HEROIC(1, new Color(0, 212, 0), Color.WHITE), 
	EPIC(2, Color.RED, Color.WHITE),
	NORMAL(3, new Color(0x20, 0x20, 0x20), Color.WHITE);

	public static Rarity fromCode(int code) {
		for (Rarity type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		Log.todo("Item", "Unhandle Rarity code " + code);
		return null;
	}
	private int code;
	private RGB background; 

	private RGB foreground;

	private Rarity(int code) {
		this(code, Color.WHITE, Color.BLACK);
	}

	private Rarity(int code, RGB background, RGB foreground) {
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
}