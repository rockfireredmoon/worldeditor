package org.icemoon.eartheternal.common;

public enum SpecialItemType {
	

//	enum Enum
//	{
//		NONE				=	0,
//		STACKING			=	1,
//		DURABILITY			=	2,
//		CHARGES				=	3,
//		CAPACITY			=	4,
//		QUEST_ID			=	5,
//		RESULT_ITEM			=	6,
//		KEY_COMPONENT		=	7,
//		REQUIRE_ROLLING		=	8,
//		LIFETIME			=	9,
//		BONUS_VALUE			=	10,
//		BOOK_PAGE			=   11
//	};
	
	NONE(0), REAGENT_GENERATOR(1), ITEM_GRINDER(2), XP_TOME(3), SPEED_TOME(4);
	
	public static SpecialItemType fromCode(int code) {
		for (SpecialItemType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		Log.todo("Item", "Unhandle SpecialItemType code " + code);
		return null;
	}

	private int code;

	private SpecialItemType(int code) {
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