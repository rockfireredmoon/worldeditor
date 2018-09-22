package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class Shops extends AbstractShops<Shop, ShopItem> {

	public Shops(String... files) {
		this(null, files);
	}
	
	public Shops(IDatabase database, String... files) {
		super(database, "Shop.txt", files);
	}

	@Override
	protected Shop createItem() {
		return new Shop(getDatabase());
	}
}
