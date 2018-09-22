package org.icemoon.eartheternal.common;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class EssenceShop extends AbstractShop<EssenceShopItem> {
	private long essence;
	private String comment;

	public EssenceShop() {
		this(null);
	}

	public EssenceShop(IDatabase database) {
		super(database);
	}

	@Override
	public void set(String name, String value, String section) {
		if (name.equals("CreatureDefID")) {
			if (getEntityId() == null)
				setEntityId(new SpawnKey());
			getEntityId().setCreature(Long.parseLong(value.split("\\s+")[0]));
		} else if (name.equals("EssenceID")) {
			StringTokenizer t = new StringTokenizer(value, "\t; ");
			essence = Long.parseLong(t.nextToken());
			if (t.hasMoreTokens()) {
				comment = value.substring(String.valueOf(essence).length()).trim();
				while (comment.startsWith(";"))
					comment = comment.substring(1);
			}
		} else if (name.equals("Item")) {
			items.add(new EssenceShopItem(value));
		} else if (!name.equals("")) {
			Log.todo("Instance (" + getFile() + ")", "Unhandle property " + name + " = " + value);
		}
	}

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		SpawnKey entityId = getEntityId();
		return entityId == null ? null : entityId.toString();
	}

	public final long getEssence() {
		return essence;
	}

	public final void setEssence(long essence) {
		this.essence = essence;
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("[ENTRY]");
		writer.println("CreatureDefID=" + (getEntityId() == null ? 0 : getEntityId().getCreature()));
		writer.print("EssenceID=" + essence);
		if (StringUtils.isBlank(comment))
			writer.println();
		else
			writer.println(" ;" + comment);
		for (EssenceShopItem si : items) {
			si.write(writer);
		}
	}
}
