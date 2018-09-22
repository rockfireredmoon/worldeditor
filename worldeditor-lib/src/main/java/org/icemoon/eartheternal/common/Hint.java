package org.icemoon.eartheternal.common;

public enum Hint {
	PERSONA(1), COPPER_SHOPKEEPER(2), CREDIT_SHOPKEEPER(4), VENDOR(8), QUEST_GIVER(16), QUEST_ENDER(32), CRAFTER(64), CLAN_REGISTRAR(
			128), VAULT(256), CREDIT_SHOP(512), USABLE(1024), USABLE_SPARKLY(2048), ITEM_GIVER(4096);

	private int val;

	private Hint(int val) {
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