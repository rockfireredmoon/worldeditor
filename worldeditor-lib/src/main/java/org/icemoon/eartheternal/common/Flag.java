package org.icemoon.eartheternal.common;

public enum Flag {
	FRIENDLY(1), HIDEMAP(2), NEUTRAL(4), FRIENDLY_INVINCIBLE(8), FRIENDLY_ATTACK(16), ENEMY(32), VISWEAPON_MELEE(
			64), VISWEAPON_RANGED(128);
	
	private int val;

	private Flag(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	@Override
	public String toString() {
		return Util.toEnglish(name(), true);
	}
}