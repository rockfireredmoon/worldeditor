package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.icemoon.eartheternal.common.EternalObjectNotation.EternalArray;
import org.icemoon.eartheternal.common.EternalObjectNotation.EternalObject;

@SuppressWarnings("serial")
public class Appearance implements Serializable {
	private EternalObjectNotation eon;

	public Appearance() {
		EternalObject ob = new EternalObject();
		eon = new EternalObjectNotation(ob);
	}

	public Appearance(String appearance) throws ParseException {
		eon = new EternalObjectNotation(appearance);
	}

	public void addSkinElement(SkinElement element) {
		EternalObject root = (EternalObject) eon.getObject();
		EternalObject object = (EternalObject) root.get("sk");
		if (object == null) {
			object = new EternalObject();
			root.put("sk", object);
		}
		object.put(element.getName(), Util.toHexString(element.getColor()));
	}

	public List<AttachmentItem> getAttachments() {
		List<AttachmentItem> l = new ArrayList<AttachmentItem>();
		EternalObject root = (EternalObject) eon.getObject();
		Object object = root.get("a");
		if (object != null && object instanceof EternalArray) {
			for (Object o : ((EternalArray) object)) {
				l.add(AttachmentItem.createAttachment((EternalObject) o));
			}
		}
		return l;
	}

	public Body getBody() {
		String object = (String) ((EternalObject) eon.getObject()).get("b");
		return object == null || object.length() == 0 ? null : Body.fromCode(object.charAt(0));
	}

	public String getAsset() {
		if (Name.N4.equals(getName())) {
			Object object = ((EternalObject) eon.getObject()).get("c");
			if (object instanceof String) {
				return (String) object;
			}
		} else if (Name.P1.equals(getName())) {
			Object object = ((EternalObject) eon.getObject()).get("a");
			if (object instanceof String) {
				return (String) object;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ClothingItem> getClothing() {
		List<ClothingItem> clothes = new ArrayList<ClothingItem>();
		if (Name.C2.equals(getName())) {
			final EternalObject eObj = (EternalObject) eon.getObject();
			Object object = eObj == null ? null : eObj.get("c");
			if (object != null && object instanceof EternalObject) {
				EternalObject eo = (EternalObject) object;
				for (Object key : eo.keySet()) {
					EternalObject val = (EternalObject) eo.get(key);
					final ClothingItem e = new ClothingItem(ClothingType.valueOf(((String) key).toUpperCase()),
							(String) val.get("type"), (String) val.get("effect"), Util.toRGBList((List<String>) val.get("colors")));
					clothes.add(e);
				}
			}
		}
		return clothes;
	}

	public Gender getGender() {
		String object = (String) ((EternalObject) eon.getObject()).get("g");
		return object == null || object.length() == 0 ? null : Gender.fromCode(object.toLowerCase().charAt(0));
	}

	public Head getHead() {
		Long object = (Long) ((EternalObject) eon.getObject()).get("h");
		return object == null ? null : Head.fromCode(object.intValue());
	}

	public Name getName() {
		if (StringUtils.isNotBlank(eon.getName())) {
			return Name.valueOf(eon.getName().toUpperCase());
		}
		return null;
	}

	public String getProp() {
		if (Name.P1.equals(getName())) {
			EternalObject root = (EternalObject) eon.getObject();
			return (String) root.get("a");
		}
		return null;
	}

	public Race getRace() {
		String object = (String) ((EternalObject) eon.getObject()).get("r");
		return object == null || object.length() == 0 ? null : Race.fromCode(object.charAt(0));
	}

	public double getSize() {
		Object object = ((EternalObject) eon.getObject()).get("sz");
		return object == null ? 1 : Double.parseDouble(object.toString());
	}

	public List<SkinElement> getSkinElements() {
		List<SkinElement> skinElements = new ArrayList<Appearance.SkinElement>();
		EternalObject object = (EternalObject) ((EternalObject) eon.getObject()).get("sk");
		if (object != null) {
			for (Object key : object.keySet()) {
				skinElements.add(new SkinElement((String) key, new Color((String) object.get(key))));
			}
		}
		return skinElements;
	}

	public void setAttachments(List<AttachmentItem> attachments) {
		if (attachments.isEmpty()) {
			((EternalObject) eon.getObject()).remove("a");
		} else {
			EternalObject root = (EternalObject) eon.getObject();
			EternalArray object = (EternalArray) root.get("a");
			if (object == null) {
				object = new EternalArray();
				root.put("a", object);
			}
			object.clear();
			for (AttachmentItem ai : attachments) {
				EternalObject to = new EternalObject();
				to.put("node", ai.getNode());
				to.put("type", ai.getAsset());
				if (ai.getColors() != null) {
					to.put("colors", Util.createColorArray(ai.getColors()));
				}
				object.add(to);
			}
		}
	}

	public void setBody(Body body) {
		final EternalObject eo = (EternalObject) eon.getObject();
		if (body == null)
			eo.remove("b");
		else
			eo.put("b", String.valueOf(body.getCode()));
	}

	public void setAsset(String asset) {
		if (Name.N4.equals(getName())) {
			final EternalObject eo = (EternalObject) eon.getObject();
			eo.remove("a");
			if (StringUtils.isBlank(asset))
				eo.remove("c");
			else
				eo.put("c", asset);
		} else if (Name.P1.equals(getName())) {
			final EternalObject eo = (EternalObject) eon.getObject();
			eo.remove("c");
			if (StringUtils.isBlank(asset))
				eo.remove("a");
			else
				eo.put("a", asset);
		}
	}

	public void setClothing(List<ClothingItem> clothing) {
		if (Name.C2.equals(getName())) {
			if (clothing.isEmpty()) {
				((EternalObject) eon.getObject()).remove("c");
			} else {
				EternalObject eo = new EternalObject();
				for (ClothingItem el : clothing) {
					EternalObject i = new EternalObject();
					i.put("type", el.getAsset());
					EternalArray a = new EternalArray();
					if (el.getColors() != null) {
						for (RGB rgb : el.getColors()) {
							a.add(Util.toHexNumber(rgb).toLowerCase());
						}
					}
					i.put("colors", a);
					eo.put(el.getType().name().toLowerCase(), i);
				}
				((EternalObject) eon.getObject()).put("c", eo);
			}
		}
	}

	public void setGender(Gender gender) {
		if (gender == null)
			((EternalObject) eon.getObject()).remove("g");
		else
			((EternalObject) eon.getObject()).put("g", String.valueOf(gender.toCode()));
	}

	public void setHead(Head head) {
		if (head == null)
			((EternalObject) eon.getObject()).remove("h");
		else
			((EternalObject) eon.getObject()).put("h", Long.valueOf(head.getCode()));
	}

	public void setName(Name name) {
		eon.setName(name == null ? null : name.name().toLowerCase());
	}

	public void setRace(Race race) {
		if (race == null)
			((EternalObject) eon.getObject()).remove("r");
		else
			((EternalObject) eon.getObject()).put("r", String.valueOf(race.getCode()));
	}

	public void setSize(double size) {
		((EternalObject) eon.getObject()).put("sz", String.valueOf(size));
	}

	public void setSkinElements(List<SkinElement> skinElements) {
		if (skinElements.isEmpty()) {
			((EternalObject) eon.getObject()).remove("sk");
		} else {
			EternalObject eo = new EternalObject();
			for (SkinElement el : skinElements) {
				eo.put(el.getName(), Util.toHexNumber(el.getColor()));
			}
			((EternalObject) eon.getObject()).put("sk", eo);
		}
	}

	public String toPrettyString() {
		return getName() == null ? "" : eon.toPrettyString();
	}

	@Override
	public String toString() {
		return getName() == null ? "" : eon.toString();
	}

	public enum Body {
		ROTUND('r'), NORMAL('n'), MUSCULAR('m');
		public static Body fromCode(char c) {
			for (Body b : values()) {
				if (b.getCode() == c) {
					return b;
				}
			}
			return null;
		}

		private char code;

		private Body(char code) {
			this.code = code;
		}

		public char getCode() {
			return code;
		}

		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}

	public enum ClothingType {
		BOOTS, CHEST, ARMS, LEGGINGS, COLLAR, BELT, GLOVES, HELMET;
		public String getDefaultIcon() {
			switch (this) {
			case BOOTS:
				return "Icon-32-C_Armor-Feet01.png";
			case CHEST:
				return "Icon-32-C_Armor-Chest01.png";
			case ARMS:
				return "Icon-32-Armor-Arms01.png";
			case LEGGINGS:
				return "Icon-32-C_Armor-Legs01.png";
			case COLLAR:
				return "Icon-32-Armor-Neck01.png";
			case BELT:
				return "Icon-32-Armor-Belts01.png";
			case GLOVES:
				return "Icon-32-C_Armor-Hands01.png";
			}
			return null;
		}

		public EquipType toEquipType() {
			switch (this) {
			case BOOTS:
				return EquipType.FEET;
			case CHEST:
				return EquipType.CHEST;
			case ARMS:
				return EquipType.ARMS;
			case LEGGINGS:
				return EquipType.LEGS;
			case COLLAR:
				return EquipType.COLLAR;
			case BELT:
				return EquipType.BELT;
			case GLOVES:
				return EquipType.HANDS;
			case HELMET:
				return EquipType.HEAD;
			}
			return null;
		}

		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}

	public enum Gender {
		MALE, FEMALE;
		public static Gender fromCode(char code) {
			if (code == 'f') {
				return FEMALE;
			}
			return MALE;
		}

		public char toCode() {
			return name().toLowerCase().charAt(0);
		}

		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}

	public enum Head {
		NORMAL(0), SERIOUS(1), WISE('2');
		public static Head fromCode(int c) {
			for (Head b : values()) {
				if (b.getCode() == c) {
					return b;
				}
			}
			return null;
		}

		private int code;

		private Head(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}

	public enum Name {
		C2,
		/**
		* 
		*/
		N4,
		/**
		 * Prop
		 */
		P1
	}

	public enum Race {
		// TODO - Not sure about Sylvan
		ANURA('a'), ATAVIAN('v'), BANDICOON('d'), BOUNDER('b'), BROCCAN('r'), CAPRICAN('c'), DAEMON('e'), DRYAD('q'), FANGREN(
				'g'), FELINE('f'), FOXEN('x'), HART('h'), LISIAN(
						'l'), LONGTAIL('o'), NOCTARI('n'), TAURIAN('t'), TROBLIN('s'), TUSKEN('k'), URSINE('u'), YETI('y');
		public static Race fromCode(char c) {
			for (Race b : values()) {
				if (b.getCode() == c) {
					return b;
				}
			}
			return null;
		}

		private char code;

		private Race(char code) {
			this.code = code;
		}

		public char getCode() {
			return code;
		}

		@Override
		public String toString() {
			return Util.toEnglish(name(), true);
		}
	}

	public static class SkinElement implements Serializable, Comparable<SkinElement> {
		private String name;
		private RGB color;

		public SkinElement(String name, RGB color) {
			super();
			this.name = name;
			this.color = color;
		}

		@Override
		public int compareTo(SkinElement o) {
			return name.compareTo(o.name);
		}

		public final RGB getColor() {
			return color;
		}

		public final String getName() {
			return name;
		}

		public final void setColor(RGB color) {
			this.color = color;
		}

		public final void setName(String name) {
			this.name = name;
		}
	}
}
