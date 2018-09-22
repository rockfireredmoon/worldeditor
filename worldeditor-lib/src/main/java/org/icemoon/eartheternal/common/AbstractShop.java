package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractShop<S extends AbstractShopItem> extends AbstractMultiINIFileEntity<SpawnKey, IDatabase> {
	protected List<S> items = new ArrayList<S>();

	public AbstractShop(IDatabase database, String file, SpawnKey id) {
		super(database, file, id);
	}

	public AbstractShop(IDatabase database) {
		super(database);
	}

	@Override
	public String toString() {
		SpawnKey entityId = getEntityId();
		return entityId == null ? null : entityId.toString();
	}

	public final List<S> getItems() {
		return items;
	}

	public final void setItems(List<S> items) {
		this.items = items;
	}
}
