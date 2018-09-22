package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.Account;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.IUserData;
import org.icemoon.eartheternal.common.GameItem.BindingType;
import org.icemoon.eartheternal.common.GameItem.Type;

@SuppressWarnings("serial")
public final class PlayerInventoryModel extends ListModel<GameItem> {
	private Account account;
	private boolean includeBags;
	private boolean includeBound;
	private IModel<IDatabase> database;
	private IModel<IUserData> userData;

	public PlayerInventoryModel(Account account, boolean includeBags, boolean includeBound, IModel<IDatabase> database, IModel<IUserData> userData) {
		this.account = account;
		this.includeBags = includeBags;
		this.includeBound = includeBound;
		this.database = database;
		this.userData = userData;
	}

	public List<GameItem> getObject() {
		List<GameItem> items = new ArrayList<GameItem>();
		if (account != null) {
			for (Long characterId : account.getCharacters()) {
				GameCharacter gc = userData.getObject().getCharacters().get(characterId);
				if (gc != null) {
					for (Long itemId : gc.getEquipment().values()) {
						addEquipItemId(items, itemId);
					}
					for (Long itemId : gc.getInventory().values()) {
						addInventoryItemId(items, itemId);
					}
				}
			}
		}
		return items;
	}

	protected void addEquipItemId(List<GameItem> items, Long itemId) {
		GameItem it = database.getObject().getItems().get(itemId);
		if (it != null && itemId != null && !items.contains(it)) {
			if ((includeBags || (!includeBags && !it.getType().equals(Type.CONTAINER))) && (includeBound
					|| (!includeBound && it.getBindingType() == null || BindingType.NORMAL.equals(it.getBindingType())))) {
				items.add(it);
			}
		}
	}

	protected void addInventoryItemId(List<GameItem> items, Long itemId) {
		GameItem it = database.getObject().getItems().get(itemId);
		if (it != null && itemId != null && !items.contains(it)) {
			if ((includeBags || (!includeBags && !it.getType().equals(Type.CONTAINER)))
					&& (includeBound || (!includeBound && !BindingType.PICKUP.equals(it.getBindingType())))) {
				items.add(it);
			}
		}
	}
}