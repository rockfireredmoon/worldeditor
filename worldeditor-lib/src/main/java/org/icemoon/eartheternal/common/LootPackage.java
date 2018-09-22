package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class LootPackage extends AbstractTableFileEntity<String, IDatabase> {
	private int auto;
	private String flags = "*";
	private List<String> sets = new ArrayList<String>();

	public LootPackage() {
		this(null);
	}
	public LootPackage(IDatabase database) {
		super(database);
	}

	public final int getAuto() {
		return auto;
	}

	public final void setAuto(int auto) {
		this.auto = auto;
	}

	public final String getFlags() {
		return flags;
	}

	public final void setFlags(String flags) {
		this.flags = flags;
	}

	public final List<String> getSets() {
		return sets;
	}

	public final void setSets(List<String> sets) {
		this.sets = sets;
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println(String.format("%s\t%d\t%s\t%s", getEntityId(), getAuto(), flags, Util.toCommaSeparatedList(sets)));
	}

	@Override
	protected void doLoad() throws IOException {
	}

	@Override
	public void set(String[] row, String comment) {
		setEntityId(row[0]);
		auto = Integer.parseInt(row[1]);
		flags = row[2];
		if (row.length > 3)
			sets = new ArrayList<String>(Arrays.asList(row[3].split(",")));
	}
}
