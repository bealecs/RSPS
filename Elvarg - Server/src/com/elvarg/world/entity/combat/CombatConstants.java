package com.elvarg.world.entity.combat;

public class CombatConstants {

	/** The amount of time it takes for cached damage to timeout. */
	// Damage cached for currently 60 seconds will not be accounted for.
	public static final long DAMAGE_CACHE_TIMEOUT = 60000;

	/**
	 * The amount of damage that will be drained by combat protection prayer.
	 */
	public static final double PRAYER_DAMAGE_REDUCTION_AGAINST_PLAYERS = .5; // 50%
																				// damage
																				// reduction
	public static final double PRAYER_DAMAGE_REDUCTION_AGAINST_NPCS = .8; // 80%
																			// damage
																			// reduction

	/**
	 * The rate at which accuracy will be reduced by combat protection prayer.
	 */
	public static final double PRAYER_ACCURACY_REDUCTION_AGAINST_PLAYERS = .20; // -20%
																				// accuracy
	public static final double PRAYER_ACCURACY_REDUCTION_AGAINST_NPCS = .40; // -80%
																				// accuracy

	/** The amount of hitpoints the redemption prayer will heal. */
	// Currently at .25 meaning hitpoints will be healed by 25% of the remaining
	// prayer points when using redemption.
	public static final double REDEMPTION_PRAYER_HEAL = .25;

	/** The maximum amount of damage inflicted by retribution. */
	// Damage between currently 0-15 will be inflicted if in the specified
	// radius when the retribution prayer effect is activated.
	public static final int MAXIMUM_RETRIBUTION_DAMAGE = 15;

	/** The radius that retribution will hit players in. */

	// All players within currently 5 squares will get hit by the retribution
	// effect.
	public static final int RETRIBUTION_RADIUS = 5;

	// Recoil item id
	public static final int RING_OF_RECOIL_ID = 2550;

}
