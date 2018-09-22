package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.icemoon.eartheternal.common.EternalObjectNotation.EternalObject;

@SuppressWarnings("serial")
public class AttachmentItem implements Serializable, Comparable<AttachmentItem> {
	@SuppressWarnings("unchecked")
	public static AttachmentItem createAttachment(EternalObject oo) {
		return createAttachment((String) oo.get("type"), (String) oo.get("effect"),
			Util.toRGBList((List<String>) oo.get("colors")), (String) oo.get("node"));
	}
	public static AttachmentItem createAttachment(String asset, String effect, List<RGB> colors) {
		return createAttachment(asset, effect, colors, null);
	}
	public static AttachmentItem createAttachment(String asset, String effect, List<RGB> colors, String node) {
		EquipType equipType = null;
		if (node != null) {
			if (node.equalsIgnoreCase("hat") || node.equalsIgnoreCase("helmet")) {
				equipType = EquipType.HEAD;
			}
			if (node.equalsIgnoreCase("left_shoulder") || node.equalsIgnoreCase("right_shoulder")) {
				equipType = EquipType.SHOULDERS;
			}
			if (node.equalsIgnoreCase("left_hand") || node.equalsIgnoreCase("right_hand")) {
				// TODO allow this be changed in UI
				equipType = EquipType.WEAPON_1H;
			}
			if (node.equalsIgnoreCase("left_calf") || node.equalsIgnoreCase("right_calf")) {
				equipType = EquipType.LEGS;
			}
			if (node.equalsIgnoreCase("left_forearm") || node.equalsIgnoreCase("right_forearm")) {
				equipType = EquipType.ARMS;
			}
			if (node.equalsIgnoreCase("back_pack")) {
				equipType = EquipType.BELT;
			}
		} else {
			if (asset.toLowerCase().endsWith("_pauldron")) {
				equipType = EquipType.SHOULDERS;
			}
		}
		return new AttachmentItem(equipType, asset, effect, colors, node);
	}
	private String asset;
	private List<RGB> colors;

	private String node;

	private EquipType equipType;

	private String effect;

	public AttachmentItem(AttachmentItem original) {
		this.asset = original.asset;
		this.colors = new ArrayList<RGB>(original.colors);
		this.node = original.node;
		this.equipType = original.equipType;
		this.effect = original.effect;
	}

	public AttachmentItem(EquipType equipType, String asset, String effect, List<RGB> colors) {
		this(equipType, asset, effect, colors, null);
	}

	public AttachmentItem(EquipType equipType, String asset, String effect, List<RGB> colors, String node) {
		super();
		setNode(node);
		setAsset(asset);
		setEquipType(equipType);
		setEffect(effect);
		this.colors = colors;
	}

	@Override
	public int compareTo(AttachmentItem o) {
		return asset.compareTo(o.asset);
	}

	public final String getAsset() {
		return asset;
	}

	public final List<RGB> getColors() {
		return colors;
	}

	public String getEffect() {
		return effect;
	}

	public EquipType getEquipType() {
		return equipType;
	}

	public final String getNode() {
		return node;
	}

	public final AttachmentItem setAsset(String asset) {
		this.asset = asset;
		return this;
	}

	public final AttachmentItem setColors(List<RGB> colors) {
		this.colors = colors;
		return this;
	}

	public AttachmentItem setEffect(String effect) {
		this.effect = effect;
		return this;
	}

	public AttachmentItem setEquipType(EquipType equipType) {
		this.equipType = equipType;
		return this;
	}

	public final AttachmentItem setNode(String node) {
		this.node = node;
		return this;
	}
}