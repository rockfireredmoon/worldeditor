package org.icemoon.worldeditor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.icemoon.eartheternal.common.GameCharacter;
import org.icemoon.eartheternal.common.Slot;

@SuppressWarnings("serial")
public class EquipmentModel extends ListModel<Map.Entry<Slot, Long>> {

	private IModel<GameCharacter> characterModel;
	private boolean bags;

	public EquipmentModel(IModel<GameCharacter> characterModel, boolean bags) {
		this.characterModel = characterModel;
		this.bags = bags;
	}

	@Override
	public void detach() {
	}

	@Override
	public List<Map.Entry<Slot, Long>> getObject() {
		final GameCharacter selected = characterModel.getObject();
		final Map<Slot, Long> equipment = selected.getEquipment();
		final ArrayList<Entry<Slot, Long>> arrayList = new ArrayList<Map.Entry<Slot, Long>>();
		for (Map.Entry<Slot, Long> ent : equipment.entrySet()) {
			if (bags ? ent.getKey().getCode() >= Slot.BAG_1.getCode() : ( ent.getKey().getCode() < Slot.BAG_1.getCode())) {
				arrayList.add(ent);
			}
		}
		return arrayList;
	}

	@Override
	public void setObject(List<Map.Entry<Slot, Long>> object) {
	}
}