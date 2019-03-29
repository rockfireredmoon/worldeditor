package org.icemoon.eartheternal.common;

public enum MapPointType {
	QUEST_START_POINTS, QUEST_END_POINTS, QUEST_MARKERS, CHARACTERS, SPAWNS;

	@Override
	public String toString() {
		return Util.toEnglish(name(), true);
	}
}