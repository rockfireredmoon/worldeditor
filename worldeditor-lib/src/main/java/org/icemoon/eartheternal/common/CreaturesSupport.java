package org.icemoon.eartheternal.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.icemoon.eartheternal.common.Appearance.Race;

public class CreaturesSupport<K extends BaseCreature> {
	public List<K> getByLevel(int level, Collection<K> values) {
		List<K> l = new ArrayList<K>();
		for (K t : values) {
			if (t.getLevel() == level) {
				l.add(t);
			}
		}
		return l;
	}

	public List<K> getByRace(Race r, Collection<K> values) {
		List<K> l = new ArrayList<K>();
		for (K t : values) {
			if (t.getAppearance() != null && r.equals(t.getAppearance().getRace())) {
				l.add(t);
			}
		}
		return l;
	}

	public List<K> getBySubName(String subName, Collection<K> values) {
		List<K> l = new ArrayList<K>();
		for (K t : values) {
			if (subName.equals(t.getSubName())) {
				l.add(t);
			}
		}
		return l;
	}

	public K getByDisplayName(String displayName, Collection<K> values) {
		for (K t : values) {
			if (displayName.equals(t.getDisplayName())) {
				return t;
			}
		}
		return null;
	}
}
