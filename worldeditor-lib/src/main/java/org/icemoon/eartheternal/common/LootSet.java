package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class LootSet extends AbstractTableFileEntity<String, IDatabase> {
	private LootRarity rarity = LootRarity.FLAT;
	private int rate;
	private List<Long> items = new ArrayList<Long>();
	private String comment;

	public LootSet() {
		this(null);
	}

	public LootSet(IDatabase database) {
		super(database);
	}

	public final LootRarity getRarity() {
		return rarity;
	}

	public final void setRarity(LootRarity rarity) {
		this.rarity = rarity;
	}

	public final int getRate() {
		return rate;
	}

	public final void setRate(int rate) {
		this.rate = rate;
	}

	public final List<Long> getItems() {
		return items;
	}

	public final void setItems(List<Long> items) {
		this.items = items;
	}

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public void set(String[] row, String comment) {
		setEntityId(row[0]);
		rarity = LootRarity.fromCode(Integer.parseInt(row[1]));
		rate = Integer.parseInt(row[2]);
		items = Util.toLongList(row[3]);
		if (row.length > 3)
			this.comment = row[4];
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	public void write(INIWriter writer) {
		writer.print(String.format("%s\t%d\t%d\t%s", getEntityId(), rarity.getCode(), rate, Util.toCommaSeparatedList(items)));
		if (StringUtils.isNotBlank(comment))
			writer.println("\t" + comment);
		else
			writer.println();
	}

	@Override
	protected void doLoad() throws IOException {
	}
}
