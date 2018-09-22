package org.icemoon.eartheternal.common;

import java.io.Serializable;

public class SpawnKey implements Serializable, Comparable<SpawnKey> {
	private static final long serialVersionUID = 121884228446734976L;
	private Long zone = 0l;
	private Long creature = 0l;

	public SpawnKey() {
	}

	public SpawnKey(Long zone, Long creature) {
		super();
		this.zone = zone;
		this.creature = creature;
	}

	public final Long getZone() {
		return zone;
	}

	public final void setZone(Long zone) {
		this.zone = zone;
	}

	public final Long getCreature() {
		return creature;
	}

	public final void setCreature(Long creature) {
		this.creature = creature;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creature == null) ? 0 : creature.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpawnKey other = (SpawnKey) obj;
		if (creature == null) {
			if (other.creature != null)
				return false;
		} else if (!creature.equals(other.creature))
			return false;
		if (zone == null) {
			if (other.zone != null)
				return false;
		} else if (!zone.equals(other.zone))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return zone + "/" + creature;
	}

	@Override
	public int compareTo(SpawnKey o) {
		int i = zone.compareTo(o.zone);
		if (i == 0) {
			i = creature.compareTo(o.creature);
		}
		return i;
	}
}
