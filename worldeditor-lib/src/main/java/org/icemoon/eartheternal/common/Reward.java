package org.icemoon.eartheternal.common;

import java.io.IOException;

public class Reward extends AbstractINIFileEntity<Integer, IDatabase> {
	private Long itemId;
	private boolean required;
	private int itemCount;
	private Act act;

	public Reward() {
		this(null);
	}

	public Reward(IDatabase database) {
		super(database);
	}

	public Reward(IDatabase database, Act act) {
		super(database);
		this.act = act;
	}

	public Act getAct() {
		return act;
	}

	public final int getItemCount() {
		return itemCount;
	}

	public final Long getItemId() {
		return itemId;
	}

	public final boolean isRequired() {
		return required;
	}

	@Override
	public void set(String name, String value, String section) {
		String[] val = value.split(",");
		setItemId(Long.parseLong(val[0]));
		setItemCount(Integer.parseInt(val[1]));
		setRequired(val[2].equals("1"));
	}

	public final void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public final void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public final void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public void write(INIWriter writer) {
		writer.println("RewardItem." + getEntityId() + "=" + getItemId() + "," + itemCount + "," + Util.toBooleanString(required));
	}

	@Override
	protected void doLoad() throws IOException {
	}
}