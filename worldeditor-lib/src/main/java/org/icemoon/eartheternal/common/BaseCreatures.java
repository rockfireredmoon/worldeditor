package org.icemoon.eartheternal.common;

import java.util.List;

import org.icemoon.eartheternal.common.Appearance.Race;

public interface BaseCreatures<T extends BaseCreature, R extends IRoot>  extends Entities<T, Long, String, R> {

	List<T> getByLevel(int level);

	List<T> getByRace(Race r);
	
	List<T> getBySubName(String subName);
	
	T getByDisplayName(String displayName);
}
