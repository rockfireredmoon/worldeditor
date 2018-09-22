package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class Shop extends AbstractShop<ShopItem> {
	public Shop() {
		this(null);
	}

	public Shop(IDatabase database) {
		super(database);
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("CreatureDefID")) {
			if (getEntityId() == null)
				setEntityId(new SpawnKey());
			getEntityId().setCreature(Long.parseLong(value.split("\\s+")[0]));
		} else if (name.equals("Item")) {
			items.add(new ShopItem(value));
		} else if (!name.equals("")) {
			Log.todo("Instance (" + getFile() + ")", "Unhandle property " + name + " = " + value);
		}
	}

	@Override
	public String toString() {
		SpawnKey entityId = getEntityId();
		return entityId == null ? null : entityId.toString();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("CreatureDefID=" + (getEntityId() == null ? 0 : getEntityId().getCreature()));
		for (ShopItem si : items) {
			si.write(writer);
		}
	}
}
