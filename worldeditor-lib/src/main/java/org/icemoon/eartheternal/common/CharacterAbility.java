package org.icemoon.eartheternal.common;

import java.io.Serializable;
import java.util.StringTokenizer;

public class CharacterAbility implements Serializable {
	private static final long serialVersionUID = 1L;
	private int tier;
	private int buffType;
	private int abilityId;
	private int abilityGroupId;
	private double remainS;

	public CharacterAbility(String value) {
		StringTokenizer t = new StringTokenizer(value, ",");
		tier = Integer.parseInt(t.nextToken());
		buffType = Integer.parseInt(t.nextToken());
		abilityId = Integer.parseInt(t.nextToken());
		abilityGroupId = Integer.parseInt(t.nextToken());
		if (t.hasMoreTokens())
			remainS = Double.parseDouble(t.nextToken());
	}

	public final int getAbilityGroupId() {
		return abilityGroupId;
	}

	public final int getAbilityId() {
		return abilityId;
	}

	public final int getBuffType() {
		return buffType;
	}

	public final double getRemainS() {
		return remainS;
	}

	public final int getTier() {
		return tier;
	}

	public final void setAbilityGroupId(int abilityGroupId) {
		this.abilityGroupId = abilityGroupId;
	}

	public final void setAbilityId(int abilityId) {
		this.abilityId = abilityId;
	}

	public final void setBuffType(int buffType) {
		this.buffType = buffType;
	}

	public final void setRemainS(double remainS) {
		this.remainS = remainS;
	}

	public final void setTier(int tier) {
		this.tier = tier;
	}

	@Override
	public String toString() {
		return tier + "," + buffType + "," + abilityId + "," + abilityGroupId + "," + remainS;
	}
}