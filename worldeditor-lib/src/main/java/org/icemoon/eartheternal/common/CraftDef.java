package org.icemoon.eartheternal.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.icemoon.eartheternal.common.GameItem.Type;

@SuppressWarnings("serial")
public class CraftDef extends AbstractTableFileEntity<String, IDatabase> {
	public abstract static class AbstractCraftCondition extends AbstractCondition {
		private int slotIndex;

		public final int getSlotIndex() {
			return slotIndex;
		}

		public final void setSlotIndex(int slotIndex) {
			this.slotIndex = slotIndex;
		}
	}

	public abstract static class AbstractCraftAction extends AbstractCondition {
		private long itemId;

		public final long getItemId() {
			return itemId;
		}

		public final void setItemId(long itemId) {
			this.itemId = itemId;
		}
	}

	public static class GiveID extends AbstractCraftAction {
		private int itemCount;

		public GiveID(String... args) {
			if (args.length > 0) {
				itemCount = Integer.parseInt(args[0]);
			}
		}

		public final int getItemCount() {
			return itemCount;
		}

		public final void setItemCount(int itemCount) {
			this.itemCount = itemCount;
		}
	}

	public static class GiveIDXMult extends AbstractCraftAction {
		private int slotIndex;
		private int mult;

		public GiveIDXMult(String... args) {
			if (args.length > 0) {
				slotIndex = Integer.parseInt(args[0]);
				if (args.length > 1) {
					mult = Integer.parseInt(args[1]);
				}
			}
		}

		public final int getSlotIndex() {
			return slotIndex;
		}

		public final void setSlotIndex(int slotIndex) {
			this.slotIndex = slotIndex;
		}

		public final int getMult() {
			return mult;
		}

		public final void setMult(int mult) {
			this.mult = mult;
		}
	}

	public static class GiveIDXDiv extends AbstractCraftAction {
		private int slotIndex;
		private int div;

		public GiveIDXDiv(String... args) {
			if (args.length > 0) {
				slotIndex = Integer.parseInt(args[0]);
				if (args.length > 1) {
					div = Integer.parseInt(args[1]);
				}
			}
		}

		public final int getSlotIndex() {
			return slotIndex;
		}

		public final void setSlotIndex(int slotIndex) {
			this.slotIndex = slotIndex;
		}

		public final int getDiv() {
			return div;
		}

		public final void setDiv(int div) {
			this.div = div;
		}
	}

	public static class RequireID extends AbstractCraftCondition {
		private long itemId;
		private int itemCount;

		public RequireID(String... args) {
			if (args.length > 0) {
				itemId = Integer.parseInt(args[0]);
				if (args.length > 1) {
					itemCount = Integer.parseInt(args[1]);
				}
			}
		}

		public final long getItemId() {
			return itemId;
		}

		public final void setItemId(long itemId) {
			this.itemId = itemId;
		}

		public final int getItemCount() {
			return itemCount;
		}

		public final void setItemCount(int itemCount) {
			this.itemCount = itemCount;
		}
	}

	public static class RequireIDMult extends RequireID {
		public RequireIDMult(String... args) {
			super(args);
		}
	}

	public static class RequireIDXMult extends AbstractCraftCondition {
		private long itemId;
		private int checkIndex;
		private int mult;

		public RequireIDXMult(String... args) {
			if (args.length > 0) {
				itemId = Integer.parseInt(args[0]);
				if (args.length > 1) {
					checkIndex = Integer.parseInt(args[1]);
					if (args.length > 1) {
						mult = Integer.parseInt(args[2]);
					}
				}
			}
		}

		public final long getItemId() {
			return itemId;
		}

		public final void setItemId(long itemId) {
			this.itemId = itemId;
		}

		public final int getCheckIndex() {
			return checkIndex;
		}

		public final void setCheckIndex(int checkIndex) {
			this.checkIndex = checkIndex;
		}

		public final int getMult() {
			return mult;
		}

		public final void setMult(int mult) {
			this.mult = mult;
		}
	}

	public static class ItemType extends AbstractCraftCondition {
		private Type itemType;

		public ItemType(String... args) {
			if (args.length > 0) {
				itemType = Type.fromCode(Integer.parseInt(args[0]));
			}
		}

		public final Type getItemType() {
			return itemType;
		}

		public final void setItemType(Type itemType) {
			this.itemType = itemType;
		}
	}

	public static class Quality extends AbstractCraftCondition {
		private ItemQuality quality;

		public Quality(String... args) {
			if (args.length > 0) {
				quality = ItemQuality.fromCode(Integer.parseInt(args[0]));
			}
		}

		public final ItemQuality getQuality() {
			return quality;
		}

		public final void setQuality(ItemQuality quality) {
			this.quality = quality;
		}
	}

	public static class Cmp extends AbstractCraftCondition {
		public enum Comparator {
			EQUAL("="), NOT_EQUAL("!="), LESS("<"), LESS_OR_EQUAL("<="), GREATER(">"), GREATER_OR_EQUAL(">=");
			String code;

			Comparator(String code) {
				this.code = code;
			}

			public static Comparator fromCode(String code) {
				for (Comparator c : values()) {
					if (c.code.equals(code)) {
						return c;
					}
				}
				throw new IllegalArgumentException("Invalid comparator " + code);
			}
		}

		public enum Property {
			LEVEL, QUALITY, TYPE, WEAPON_TYPE, ARMOR_TYPE, EQUIP_TYPE;
			public static Property fromCode(String code) {
				for (Property p : values()) {
					if (p.name().toLowerCase().replace("_", "").equals(code)) {
						return p;
					}
				}
				throw new IllegalArgumentException("Invalid property " + code);
			}
		}

		private int value;
		private Property property;
		private Comparator comparator;

		public Cmp(String... args) {
			if (args.length > 0) {
				property = Property.fromCode(args[0]);
				if (args.length > 1) {
					comparator = Comparator.fromCode(args[1]);
					if (args.length > 2) {
						value = Integer.parseInt(args[2]);
					}
				}
			}
		}

		public final int getValue() {
			return value;
		}

		public final void setValue(int value) {
			this.value = value;
		}

		public final Property getProperty() {
			return property;
		}

		public final void setProperty(Property property) {
			this.property = property;
		}

		public final Comparator getComparator() {
			return comparator;
		}

		public final void setComparator(Comparator comparator) {
			this.comparator = comparator;
		}
	}

	private int input;
	private int output;
	private String comment;
	private List<AbstractCraftCondition> conditions = new ArrayList<AbstractCraftCondition>();
	private List<AbstractCraftAction> actions = new ArrayList<AbstractCraftAction>();
	private static Map<String, Class<? extends AbstractCraftCondition>> supportedConditions = new HashMap<String, Class<? extends AbstractCraftCondition>>();
	private static Map<String, Class<? extends AbstractCraftAction>> supportedActions = new HashMap<String, Class<? extends AbstractCraftAction>>();
	static {
		addSupportedCondition(RequireID.class);
		addSupportedCondition(RequireIDXMult.class);
		addSupportedCondition(RequireIDMult.class);
		addSupportedCondition(ItemType.class);
		addSupportedCondition(Quality.class);
		addSupportedCondition(Cmp.class);
		addSupportedAction(GiveID.class);
		addSupportedAction(GiveIDXMult.class);
		addSupportedAction(GiveIDXDiv.class);
	}

	public static String addSupportedCondition(Class<? extends AbstractCraftCondition> cond) {
		String k = cond.getSimpleName().toLowerCase();
		supportedConditions.put(k, cond);
		return k;
	}

	public static String addSupportedAction(Class<? extends AbstractCraftAction> cond) {
		String k = cond.getSimpleName().toLowerCase();
		supportedActions.put(k, cond);
		return k;
	}

	public CraftDef() {
		this(null);
	}

	public CraftDef(IDatabase database) {
		super(database);
	}

	public final String getComment() {
		return comment;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	public static final Map<String, Class<? extends AbstractCraftCondition>> getSupportedConditions() {
		return supportedConditions;
	}

	public static final Map<String, Class<? extends AbstractCraftAction>> getSupportedActions() {
		return supportedActions;
	}

	public final List<AbstractCraftAction> getActions() {
		return actions;
	}

	public final void setActions(List<AbstractCraftAction> actions) {
		this.actions = actions;
	}

	public final int getInput() {
		return input;
	}

	public final void setInput(int input) {
		this.input = input;
	}

	public final int getOutput() {
		return output;
	}

	public final void setOutput(int output) {
		this.output = output;
	}

	public final List<AbstractCraftCondition> getConditions() {
		return conditions;
	}

	public final void setConditions(List<AbstractCraftCondition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public void set(String[] row, String comment) {
		setEntityId(row[0]);
		setInput(Integer.parseInt(row[1]));
		setOutput(Integer.parseInt(row[2]));
		for (String cond : row[3].split("\\|")) {
			conditions.add(createCondition(cond));
		}
		for (String cond : row[4].split("\\|")) {
			actions.add(createAction(cond));
		}
		this.comment = comment;
	}

	private AbstractCraftCondition createCondition(String cond) {
		String[] args = cond.split(",");
		int slotIndex = Integer.parseInt(args[0]);
		String name = args[1].toLowerCase();
		Class<? extends AbstractCraftCondition> clazz = supportedConditions.get(name);
		if (clazz == null)
			throw new IllegalArgumentException("No condition " + name);
		String[] cargs = new String[args.length - 2];
		System.arraycopy(args, 2, cargs, 0, cargs.length);
		try {
			AbstractCraftCondition cc = clazz.getConstructor(String[].class).newInstance((Object) cargs);
			cc.setSlotIndex(slotIndex);
			return cc;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid condition.", e);
		}
	}

	private AbstractCraftAction createAction(String cond) {
		String[] args = cond.split(",");
		long itemId = Long.parseLong(args[1]);
		String name = args[0].toLowerCase();
		Class<? extends AbstractCraftAction> clazz = supportedActions.get(name);
		if (clazz == null)
			throw new IllegalArgumentException("No action " + name);
		String[] cargs = new String[args.length - 2];
		System.arraycopy(args, 2, cargs, 0, cargs.length);
		try {
			AbstractCraftAction cc = clazz.getConstructor(String[].class).newInstance((Object) cargs);
			cc.setItemId(itemId);
			return cc;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid condition.", e);
		}
	}

	@Override
	public void write(INIWriter writer) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doLoad() throws IOException {
		// TODO Auto-generated method stub
	}
}
