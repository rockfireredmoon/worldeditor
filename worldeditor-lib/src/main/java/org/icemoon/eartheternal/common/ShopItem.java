package org.icemoon.eartheternal.common;

import org.apache.commons.lang.StringUtils;

public class ShopItem extends AbstractShopItem {

	public ShopItem(Long id, String comment) {
		super(id, comment);
	}

	public ShopItem(String line) {
		super(line);
	}

	public void write(INIWriter writer) {
		writer.print("Item=" + id);
		if (StringUtils.isBlank(comment))
			writer.println();
		else
			writer.println("\t;" + comment);
	}
}