package org.icemoon.eartheternal.common;

public enum Effect {

	DEAD(                  0, 0  ),
	SILENCE(               0, 1  ),
	DISARM(                0, 2  ),
	STUN(                  0, 3 ),

	DAZE(                  0, 4 ),
	CHARM(                 0, 5 ),
	FEAR(                  0, 6 ),
	ROOT(                  0, 7 ),
	
	LOCKED(                0, 8 ),
	BROKEN(                0, 9 ),
	CAN_USE_WEAPON_2H(     0, 10 ),
	CAN_USE_WEAPON_1H(     0, 11 ),

	CAN_USE_WEAPON_SMALL(  0, 12 ),
	CAN_USE_WEAPON_POLE(   0, 13 ),
	CAN_USE_WEAPON_BOW(    0, 14 ),
	CAN_USE_WEAPON_THROWN( 0, 15 ),

	CAN_USE_WEAPON_WAND(   0, 16 ),
	CAN_USE_DUAL_WIELD(    0, 17 ),
	CAN_USE_PARRY(         0, 18 ),
	CAN_USE_BLOCK(         0, 19 ),

	CAN_USE_ARMOR_CLOTH(  0, 20 ),
	CAN_USE_ARMOR_LIGHT(   0, 21 ),
	CAN_USE_ARMOR_MEDIUM(  0, 22 ),
	CAN_USE_ARMOR_HEAVY(   0, 23 ),

	INVISIBLE(             0, 24 ),
	ALL_SEEING(            0, 25 ),
	IN_COMBAT_STAND(       0, 26 ),
	INVINCIBLE(            0, 27 ),

	FLEE(                  0, 28 ),
	NO_AGGRO_GAINED(       0, 29 ),
	UNATTACKABLE(          0, 30 ),
	IS_USABLE(             0, 31 ),

	// New array index
	CLIENT_LOADING(        1, 0 ),
	IMMUNE_INTERRUPT(      1, 1 ),
	IMMUNE_SILENCE(        1, 2 ),
	IMMUNE_DISARM(         1, 3 ),

	IMMUNE_BLEED(          1, 4 ),
	IMMUNE_DAMAGE_FIRE(    1, 5 ),
	IMMUNE_DAMAGE_FROST(   1, 6 ),
	IMMUNE_DAMAGE_MYSTIC(  1, 7 ),

	IMMUNE_DAMAGE_DEATH(   1, 8 ),
	IMMUNE_DAMAGE_MELEE(   1, 9 ),
	DISABLED(              1, 10 ),
	AUTO_ATTACK(           1, 11 ),

	AUTO_ATTACK_RANGED(    1, 12 ),
	CARRYING_RED_FLAG(     1, 13 ),
	CARRYING_BLUE_FLAG(    1, 14 ),
	HENGE(                 1, 15 ),

	TRANSFORMER(           1, 16 ),
	PVPABLE(               1, 17 ),
	IN_COMBAT(             1, 18 ),
	WALK_IN_SHADOWS(       1, 19 ),

	EVADE(                 1, 20 ),
	TAUNTED(               1, 21 ),
	XP_BOOST(              1, 22 ),
	REAGENT_GENERATOR(     1, 23 ),

	RES_PENALTY(           1, 24 ),
	IMMUNE_STUN(           1, 25 ),
	IMMUNE_DAZE(           1, 26 ),
	GM_FROZEN(             1, 27 ),

	GM_INVISIBLE(          1, 28 ),
	GM_SILENCED(           1, 29 ),

	UNKILLABLE(            1, 30 ),
	TRANSFORMED(           1, 31 ),

	// New array index
	INVISIBLE_EQUIPMENT(   2, 0 ),
	USABLE_BY_COMBATANT(   2, 1 );
	
	private int arr;
	private int shift;

	Effect(int arr, int shift) {
		this.arr = arr;
		this.shift = shift;
	}

	public final int getArr() {
		return arr;
	}

	public final int getShift() {
		return shift;
	}

	public final void setArr(int arr) {
		this.arr = arr;
	}

	public final void setShift(int shift) {
		this.shift = shift;
	}
}
