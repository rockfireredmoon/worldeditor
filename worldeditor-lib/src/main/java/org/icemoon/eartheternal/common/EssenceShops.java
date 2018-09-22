package org.icemoon.eartheternal.common;

@SuppressWarnings("serial")
public class EssenceShops extends AbstractShops<EssenceShop, EssenceShopItem> {
	

	public EssenceShops(String... files) {
		super("EssenceShop.txt", files);
	}
	
	public EssenceShops(IDatabase database, String... files) {
		super(database, "EssenceShop.txt", files);
	}

	@Override
	protected EssenceShop createItem() {
		return new EssenceShop(getDatabase());
	}
}
