package org.icemoon.eartheternal.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class ResCost extends AbstractTableFileEntity<Integer, IDatabase> {
	private long revive;
	private long resurrect;
	private long rebirth;

	public ResCost() {
		this(null);
	}

	public ResCost(IDatabase database) {
		super(database);
	}

	@Override
	public void set(String[] row, String comment) {
		setEntityId(Integer.parseInt(row[0]));
		setResurrect(Long.parseLong(row[1]));
		setRebirth(Long.parseLong(row[2]));
	}

	public final long getRevive() {
		return revive;
	}

	public final void setRevive(long revive) {
		this.revive = revive;
	}

	public final long getResurrect() {
		return resurrect;
	}

	public final void setResurrect(long resurrect) {
		this.resurrect = resurrect;
	}

	public final long getRebirth() {
		return rebirth;
	}

	public final void setRebirth(long rebirth) {
		this.rebirth = rebirth;
	}

	@Override
	public void write(INIWriter writer) {
		writer.println(String.format("%d\t%d\t%d", getEntityId(), resurrect, rebirth));
	}

	@Override
	protected void doLoad() throws IOException {
	}
}
