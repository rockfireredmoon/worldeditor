package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.GameItem;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.eartheternal.common.Slot;

@SuppressWarnings("serial")
public class InventoryModel extends ListModel<Long> {
	private IModel<GameCharacter> characterModel;
	private IModel<IDatabase> database;

	public InventoryModel(IModel<GameCharacter> characterModel, IModel<IDatabase> database) {
		this.characterModel = characterModel;
		this.database = database;
	}

	@Override
	public void detach() {
	}

	@Override
	public List<Long> getObject() {
		int totalSlots = 24;
		final GameCharacter selected = characterModel.getObject();
		final Map<Slot, Long> equipment = selected.getEquipment();
		final Map<Integer, Long> inventory = selected.getInventory();
		for (Slot slot : Arrays.asList(Slot.BAG_1, Slot.BAG_2, Slot.BAG_3, Slot.BAG_4)) {
			Long bagItemId = equipment.get(slot);
			if (bagItemId != null) {
				GameItem item = database.getObject().getItems().get(bagItemId);
				if (item != null) {
					totalSlots += item.getContainerSlots();
				}
			}
		}
		List<Long> slotList = new ArrayList<Long>();
		for (int i = 0; i < totalSlots; i++) {
			final Long slotItemId = inventory.get(i);
			slotList.add(slotItemId == null ? 0l : slotItemId);
		}
		return slotList;
	}

	@Override
	public void setObject(List<Long> object) {
	}
}