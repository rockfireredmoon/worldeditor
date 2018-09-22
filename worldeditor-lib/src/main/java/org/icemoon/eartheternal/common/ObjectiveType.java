package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.List;

public enum ObjectiveType {
	ACTIVATE, GATHER, EMOTE, TALK, NONE, KILL, TRAVEL;

	public static List<String> names() {
		List<String> l = new ArrayList<String>();
		for(ObjectiveType t : values()) {
			l.add(t.name());
		}
		return l;
	}
	
	public boolean isActivateType() {
		switch(this) {
		case ACTIVATE:
		case GATHER:
		case KILL:
			return true;
		default:
			return false;
		}
	}

	@Override
	public String toString() {
		return Util.toEnglish(name(), true);
	}
}