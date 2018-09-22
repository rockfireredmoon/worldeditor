package org.icemoon.eartheternal.common;

import java.util.List;

import org.icemoon.eartheternal.common.Appearance.ClothingType;

@SuppressWarnings("serial")
public class ClothingItem extends AttachmentItem {
	private ClothingType type;

	public ClothingItem(ClothingType type, String asset, String effect, List<RGB> colors) {
		super(type.toEquipType(), asset, effect, colors);
		this.type = type;
	}

	@Override
	public int compareTo(AttachmentItem o) {
		return getType().compareTo(((ClothingItem)o).getType());
	}

	public final ClothingType getType() {
		return type;
	}

	public final void setType(ClothingType type) {
		this.type = type;
	}
}