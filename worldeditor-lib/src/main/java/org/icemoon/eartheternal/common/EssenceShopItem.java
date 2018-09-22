package org.icemoon.eartheternal.common;

import org.apache.commons.lang.StringUtils;

public class EssenceShopItem extends AbstractShopItem {
	private int tokens;

	public EssenceShopItem(String line) {
		line = line.trim();
		StringBuilder b = new StringBuilder();
		for (char c : line.toCharArray()) {
			if (!Character.isDigit(c) && c != ',')
				break;
			b.append(c);
		}
		line = line.substring(b.length()).trim();
		if (line.length() > 0) {
			comment = line;
			while (comment.startsWith(";"))
				comment = comment.substring(1);
		}
		String[] a = b.toString().split(",");
		id = Long.parseLong(a[0]);
		if (a.length > 1)
			tokens = Integer.parseInt(a[1]);
	}

	public EssenceShopItem(Long id, String comment) {
		super(id, comment);
	}

	public final int getTokens() {
		return tokens;
	}

	public final void setTokens(int tokens) {
		this.tokens = tokens;
	}

	public void write(INIWriter writer) {
		writer.print("Item=" + id + "," + tokens);
		if (StringUtils.isBlank(comment))
			writer.println();
		else
			writer.println("\t;" + comment);
	}
}