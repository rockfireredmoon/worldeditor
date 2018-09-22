package org.icemoon.eartheternal.common;

import java.io.Serializable;

public class SpawnEntry implements Serializable {

	private Long creatureId;
	private int chance;
	private String comment;
	
	public SpawnEntry(Long creatureId, int chance, String comment) {
		this.creatureId = creatureId;
		this.chance = chance;
		this.comment = comment;
	}

	public final int getChance() {
		return chance;
	}

	public final String getComment() {
		return comment;
	}

	public final Long getCreatureId() {
		return creatureId;
	}

	public final void setChance(int chance) {
		this.chance = chance;
	}

	public final void setComment(String comment) {
		this.comment = comment;
	}

	public final void setCreatureId(Long creatureId) {
		this.creatureId = creatureId;
	}

}