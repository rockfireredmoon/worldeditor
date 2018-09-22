package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.icemoon.eartheternal.common.EternalObjectNotation.EternalArray;
import org.icemoon.eartheternal.common.EternalObjectNotation.EternalObject;

@SuppressWarnings("serial")
public class ItemAppearance implements Serializable {
	private EternalArray ob;

	public ItemAppearance() {
		ob = new EternalArray();
	}

	public ItemAppearance(ItemAppearance appearance) {
		try {
			ob = new EternalArray(appearance.ob.toString());
		} catch (ParseException pe) {
			throw new RuntimeException(pe);
		}
	}

	public ItemAppearance(String appearance) throws ParseException {
		this();
		appearance = appearance.trim();
		if (appearance.startsWith("{")) {
			EternalObject eo = new EternalObject(appearance);
			ob = new EternalArray();
			ob.add(eo);
			ob.add(null);
		} else {
			ob = new EternalArray(appearance);
			if (ob.size() < 2) {
				ob.add(null);
			}
		}
	}

	public List<AttachmentItem> getAttachments() {
		List<AttachmentItem> l = new ArrayList<AttachmentItem>();
		for (Object v : ob) {
			if (v instanceof EternalObject && ((EternalObject) v).containsKey("a")) {
				l.add(AttachmentItem.createAttachment((EternalObject) ((EternalObject) v).get("a")));
			}
		}
		return l;
	}

	@SuppressWarnings("unchecked")
	public List<RGB> getClothingColor() {
		EternalObject eo = findMap("c");
		if (eo == null || !eo.containsKey("colors")) {
			return null;
		}
		return Util.toRGBList((List<String>) eo.get("colors"));
	}

	public String getClothingType() {
		EternalObject eo = findMap("c");
		if (eo == null) {
			return null;
		}
		return (String) eo.get("type");
	}

	public void setAttachments(AttachmentItem... attachments) {
		setAttachments(Arrays.asList(attachments));
	}

	public void setAttachments(List<AttachmentItem> attachments) {
		int firstIndex = -1;
		int index = 0;
		for (Object v : new ArrayList<Object>(ob)) {
			if (v instanceof EternalObject && ((EternalObject) v).containsKey("a")) {
				ob.remove(v);
				if (firstIndex == -1) {
					firstIndex = index;
				}
			}
			index++;
		}
		List<AttachmentItem> ri = new ArrayList<AttachmentItem>(attachments);
		Collections.reverse(ri);
		writeAttachment(firstIndex, ri, ob);
	}

	public void setClothingAsset(String clothingType) {
		EternalObject eo = findOrCreateClothing();
		eo.put("type", clothingType);
	}

	public void setClothingColor(List<RGB> colors) {
		findOrCreateClothing().put("colors", Util.createColorArray(colors));
	}

	public String toCopyString() {
		EternalObject eo = findMap("c");
		if (eo == null) {
			List<AttachmentItem> as = getAttachments();
			if (as.size() > 0) {
				EternalArray oe = new EternalArray();
				writeAttachment(0, as, oe);
				return oe.toString();
			}
			return null;
		}
		return "{c=" + eo.toString(false) + "}";
	}

	@Override
	public String toString() {
		return ob.toString();
	}

	private EternalObject findMap(String n) {
		for (Object v : ob) {
			if (v instanceof EternalObject && ((EternalObject) v).containsKey(n)) {
				return (EternalObject) ((EternalObject) v).get("c");
			}
		}
		return null;
	}

	private EternalObject findOrCreateClothing() {
		EternalObject eo = findMap("c");
		if (eo == null) {
			EternalObject xo = new EternalObject();
			ob.add(xo);
			eo = new EternalObject();
			xo.put("c", eo);
		}
		return eo;
	}

	private void writeAttachment(int firstIndex, List<AttachmentItem> ri, EternalArray arr) {
		for (AttachmentItem ai : ri) {
			EternalObject to = new EternalObject();
			to.put("type", ai.getAsset());
			to.put("colors", Util.createColorArray(ai.getColors()));
			if (ai.getEffect() != null && !ai.getEffect().equals("")) {
				to.put("effect", ai.getEffect());
			}
			if (ai.getNode() != null && !ai.getNode().equals("")) {
				to.put("node", ai.getNode());
			}
			EternalObject eo = new EternalObject();
			eo.put("a", to);
			arr.add(Math.max(firstIndex, 0), eo);
		}
	}
}
