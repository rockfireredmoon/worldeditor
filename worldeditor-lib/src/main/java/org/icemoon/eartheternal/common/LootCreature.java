package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class LootCreature extends AbstractTableFileEntity<Long, IDatabase> {
	private long creature;
	private boolean explicit;
	private List<String> packageList = new ArrayList<String>();
	private String comment;

	public LootCreature() {
		this(null);
	}

	public LootCreature(IDatabase database) {
		super(database);
	}

	public final long getCreature() {
		return creature;
	}

	public final void setCreature(long creature) {
		this.creature = creature;
	}

	public final boolean isExplicit() {
		return explicit;
	}

	public final void setExplicit(boolean explicit) {
		this.explicit = explicit;
	}

	public final List<String> getPackages() {
		return packageList;
	}

	public final void setPackages(List<String> packageList) {
		this.packageList = packageList;
	}

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public void set(String[] row, String comment) {
		creature = Long.parseLong(row[0]);
		explicit = row[1].equals("1");
		packageList = new ArrayList<String>(Arrays.asList(row[2].split(",")));
		this.comment = comment;
	}

	@Override
	public String toString() {
		return creature + (packageList.isEmpty() ? "" : packageList.get(0));
	}

	@Override
	public void write(INIWriter writer) {
		writer.print(String.format("%d\t%d\t%s\t", creature, explicit ? 1 : 0, Util.toCommaSeparatedList(packageList)));
		if (StringUtils.isNotBlank(comment))
			writer.println(";" + comment);
		else
			writer.println();
	}

	@Override
	protected void doLoad() throws IOException {
	}
}
