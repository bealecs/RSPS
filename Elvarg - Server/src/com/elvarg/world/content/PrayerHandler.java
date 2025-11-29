package com.elvarg.world.content;

import java.util.HashMap;
import java.util.Map;

import com.elvarg.engine.task.Task;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.util.Misc;
import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Skill;
import com.elvarg.world.model.equipment.BonusManager;

/**
 * All of the prayers that can be activated and deactivated. This currently only
 * has support for prayers present in the <b>317 protocol</b>.
 * 
 * @author Swiffy
 */
public class PrayerHandler {

	/**
	 * Represents a prayer's configurations, such as their level requirement,
	 * buttonId, configId and drain rate.
	 * 
	 * @author relex lawl
	 */
	public enum PrayerData {
		THICK_SKIN(1, 1, 5609, 83), BURST_OF_STRENGTH(4, 1, 5610, 84), CLARITY_OF_THOUGHT(7, 1, 5611, 85), SHARP_EYE(8,
				1, 19812, 700), MYSTIC_WILL(9, 1, 19814, 701), ROCK_SKIN(10, 2, 5612, 86), SUPERHUMAN_STRENGTH(13, 1.5,
						5613, 87), IMPROVED_REFLEXES(16, 1.5, 5614, 88), RAPID_RESTORE(19, .4, 5615, 89), RAPID_HEAL(22,
								.6, 5616,
								90), PROTECT_ITEM(25, .6, 5617, 91), HAWK_EYE(26, 1.5, 19816, 702), MYSTIC_LORE(27, 1.5,
										19818, 703), STEEL_SKIN(28, 2.5, 5618, 92), ULTIMATE_STRENGTH(31, 2.8, 5619,
												93), INCREDIBLE_REFLEXES(34, 2.8, 5620, 94), PROTECT_FROM_MAGIC(37, 2.8,
														5621, 95, 2), PROTECT_FROM_MISSILES(40, 2.8, 5622, 96,
																1), PROTECT_FROM_MELEE(43, 2.8, 5623, 97, 0), EAGLE_EYE(
																		44, 3, 19821, 704), MYSTIC_MIGHT(45, 3, 19823,
																				705), RETRIBUTION(46, 1, 683, 98,
																						4), REDEMPTION(49, 2, 684, 99,
																								5), SMITE(52, 5, 685,
																										100, 100,
																										6), PRESERVE(55,
																												1,
																												28001,
																												708), CHIVALRY(
																														60,
																														5,
																														19825,
																														706), PIETY(
																																70,
																																6,
																																19827,
																																707), RIGOUR(
																																		74,
																																		6.4,
																																		28004,
																																		710), AUGURY(
																																				77,
																																				6.1,
																																				28007,
																																				712);

		private PrayerData(int requirement, double drainRate, int buttonId, int configId, int... hint) {
			this.requirement = requirement;
			this.drainRate = drainRate;
			this.buttonId = buttonId;
			this.configId = configId;
			if (hint.length > 0)
				this.hint = hint[0];
		}

		/**
		 * The prayer's level requirement for player to be able to activate it.
		 */
		private int requirement;

		/**
		 * The prayer's action button id in prayer tab.
		 */
		private int buttonId;

		/**
		 * The prayer's config id to switch their glow on/off by sending the
		 * sendConfig packet.
		 */
		private int configId;

		/**
		 * The prayer's drain rate as which it will drain the associated
		 * player's prayer points.
		 */
		private double drainRate;

		/**
		 * The prayer's head icon hint index.
		 */
		private int hint = -1;

		/**
		 * The prayer's formatted name.
		 */
		private String name;

		/**
		 * Gets the prayer's formatted name.
		 * 
		 * @return The prayer's name
		 */
		private final String getPrayerName() {
			if (name == null)
				return Misc.capitalizeWords(toString().toLowerCase().replaceAll("_", " "));
			return name;
		}

		/**
		 * Contains the PrayerData with their corresponding prayerId.
		 */
		private static HashMap<Integer, PrayerData> prayerData = new HashMap<Integer, PrayerData>();

		/**
		 * Contains the PrayerData with their corresponding buttonId.
		 */
		private static HashMap<Integer, PrayerData> actionButton = new HashMap<Integer, PrayerData>();

		/**
		 * Populates the prayerId and buttonId maps.
		 */
		static {
			for (PrayerData pd : PrayerData.values()) {
				prayerData.put(pd.ordinal(), pd);
				actionButton.put(pd.buttonId, pd);
			}
		}
	} // End of PrayerData enum

	private static final Map<Integer, Integer> QUICK_PRAYER_BUTTON_TO_ORDINAL = new HashMap<>();
	private static final Map<Integer, Integer> PRAYER_ORDINAL_TO_CLIENT_CONFIG = new HashMap<>();

	static {
		int prayerOrdinal = 0;
		// Buttons 17202-17228 map to ordinals, but skip 24, 27, 28 (bonus prayers use dedicated buttons)
		for (int buttonId = 17202, clientConfigId = 630; buttonId <= 17228; buttonId++, clientConfigId++) {
			// Skip ordinals 24, 27, 28 - they will use buttons 17234-17236
			while (prayerOrdinal == 24 || prayerOrdinal == 27 || prayerOrdinal == 28) {
				prayerOrdinal++;
			}
			if (prayerOrdinal < PrayerData.values().length) {
				QUICK_PRAYER_BUTTON_TO_ORDINAL.put(buttonId, prayerOrdinal);
				PRAYER_ORDINAL_TO_CLIENT_CONFIG.put(prayerOrdinal, clientConfigId);
				prayerOrdinal++;
			}
		}
		// Add the 3 bonus prayers with their dedicated buttons and configs
		QUICK_PRAYER_BUTTON_TO_ORDINAL.put(17234, 24); // Preserve button → ordinal 24
		PRAYER_ORDINAL_TO_CLIENT_CONFIG.put(24, 657); // Preserve uses config 657
		QUICK_PRAYER_BUTTON_TO_ORDINAL.put(17235, 27); // Rigour button → ordinal 27
		PRAYER_ORDINAL_TO_CLIENT_CONFIG.put(27, 658); // Rigour uses config 658
		QUICK_PRAYER_BUTTON_TO_ORDINAL.put(17236, 28); // Augury button → ordinal 28
		PRAYER_ORDINAL_TO_CLIENT_CONFIG.put(28, 659); // Augury uses config 659
	}

	/**
	 * Gets the protecting prayer based on the argued combat type.
	 * 
	 * @param type
	 *            the combat type.
	 * @return the protecting prayer.
	 */
	public static int getProtectingPrayer(CombatType type) {
		switch (type) {
		case MELEE:
			return PROTECT_FROM_MELEE;
		case MAGIC:
		case DRAGON_FIRE:
			return PROTECT_FROM_MAGIC;
		case RANGED:
			return PROTECT_FROM_MISSILES;
		default:
			throw new IllegalArgumentException("Invalid combat type: " + type);
		}
	}

	public static boolean isActivated(Player player, int prayer) {
		return player.getPrayerActive()[prayer];
	}

	/**
	 * Activates a prayer with specified <code>buttonId</code>.
	 * 
	 * @param player
	 *            The player clicking on prayer button.
	 * @param buttonId
	 *            The button the player is clicking.
	 */
	public static boolean togglePrayer(Player player, final int buttonId) {
		// Handle quick prayer toggle button
		if (buttonId == QUICK_PRAYERS_TOGGLE_BUTTON) {
			sendQuickPrayersClick(player);
			return true;
		}

		// Handle quick prayer selection interface button
		if (buttonId == QUICK_PRAYERS_SELECT_BUTTON) {
			sendQuickPrayersInterface(player); // Call the new method
			return true;
		}
		
		// Handle "Confirm Selection" button
		if (buttonId == 17231) {
			sendConfirmQuickPrayers(player);
			return true;
		}

		// Handle individual prayer selection buttons within the quick prayers interface
		if ((buttonId >= 17202 && buttonId <= 17228) || (buttonId >= 17234 && buttonId <= 17236)) {
			handleQuickPrayerSelectionButton(player, buttonId);
			return true;
		}

		PrayerData prayerData = PrayerData.actionButton.get(buttonId);
		if (prayerData != null) {
			if (!player.getPrayerActive()[prayerData.ordinal()])
				activatePrayer(player, prayerData.ordinal());
			else
				deactivatePrayer(player, prayerData.ordinal());
			return true;
		}
		return false;
	}

	/**
	 * Activates said prayer with specified <code>prayerId</code> and
	 * de-activates all non-stackable prayers.
	 * 
	 * @param player
	 *            The player activating prayer.
	 * @param prayerId
	 *            The id of the prayer being turned on, also known as the
	 *            ordinal in the respective enum.
	 */
	public static void activatePrayer(Player player, final int prayerId) {
		if (player.getPrayerActive()[prayerId])
			return;
		/*
		 * if(Dueling.checkRule(player, DuelRule.NO_PRAYER)) {
		 * player.getPacketSender().
		 * sendMessage("Prayer has been disabled in this duel.");
		 * PrayerHandler.deactivateAll(player); return; }
		 */
		PrayerData pd = PrayerData.prayerData.get(prayerId);
		if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender()
					.sendMessage("You do not have enough Prayer points. You can recharge your points at an altar.");
			return;
		}
		if (player.getSkillManager().getMaxLevel(Skill.PRAYER) < (pd.requirement)) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage(
					"You need a Prayer level of at least " + pd.requirement + " to use " + pd.getPrayerName() + ".");
			return;
		}
		if (prayerId == CHIVALRY && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 60) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Defence level of at least 60 to use Chivalry.");
			return;
		}
		if (prayerId == PIETY && player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 70) {
			player.getPacketSender().sendConfig(pd.configId, 0);
			player.getPacketSender().sendMessage("You need a Defence level of at least 70 to use Piety.");
			return;
		}
		if (!player.getCombat().getPrayerBlockTimer().finished()) {
			if (prayerId == PROTECT_FROM_MELEE || prayerId == PROTECT_FROM_MISSILES || prayerId == PROTECT_FROM_MAGIC) {
				player.getPacketSender().sendConfig(pd.configId, 0);
				player.getPacketSender()
						.sendMessage("You have been disabled and can no longer use protection prayers.");
				return;
			}
		}

		// Prayer locks
		boolean locked = false;

		if (prayerId == PRESERVE && !player.isPreserveUnlocked() || prayerId == RIGOUR && !player.isRigourUnlocked()
				|| prayerId == AUGURY && !player.isAuguryUnlocked()) {
			locked = true;
		}

		if (locked) {
			player.getPacketSender().sendMessage("You have not unlocked that Prayer yet.");
			return;
		}

		switch (prayerId) {
		case THICK_SKIN:
		case ROCK_SKIN:
		case STEEL_SKIN:
			resetPrayers(player, DEFENCE_PRAYERS, prayerId);
			break;
		case BURST_OF_STRENGTH:
		case SUPERHUMAN_STRENGTH:
		case ULTIMATE_STRENGTH:
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case CLARITY_OF_THOUGHT:
		case IMPROVED_REFLEXES:
		case INCREDIBLE_REFLEXES:
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case SHARP_EYE:
		case HAWK_EYE:
		case EAGLE_EYE:
		case MYSTIC_WILL:
		case MYSTIC_LORE:
		case MYSTIC_MIGHT:
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case CHIVALRY:
		case PIETY:
		case RIGOUR:
		case AUGURY:
			resetPrayers(player, DEFENCE_PRAYERS, prayerId);
			resetPrayers(player, STRENGTH_PRAYERS, prayerId);
			resetPrayers(player, ATTACK_PRAYERS, prayerId);
			resetPrayers(player, RANGED_PRAYERS, prayerId);
			resetPrayers(player, MAGIC_PRAYERS, prayerId);
			break;
		case PROTECT_FROM_MAGIC:
		case PROTECT_FROM_MISSILES:
		case PROTECT_FROM_MELEE:
			resetPrayers(player, OVERHEAD_PRAYERS, prayerId);
			break;
		case RETRIBUTION:
		case REDEMPTION:
		case SMITE:
			resetPrayers(player, OVERHEAD_PRAYERS, prayerId);
			break;
		}
		player.setPrayerActive(prayerId, true);
		player.getPacketSender().sendConfig(pd.configId, 1);
		if (hasNoPrayerOn(player, prayerId) && !player.isDrainingPrayer())
			startDrain(player);
		if (pd.hint != -1) {
			int hintId = getHeadHint(player);
			player.getAppearance().setHeadHint(hintId);
		}

		BonusManager.update(player);

		// Sounds.sendSound(player, Sound.ACTIVATE_PRAYER_OR_CURSE);
	}

	/**
	 * Deactivates said prayer with specified <code>prayerId</code>.
	 * 
	 * @param player
	 *            The player deactivating prayer.
	 * @param prayerId
	 *            The id of the prayer being deactivated.
	 */
	public static void deactivatePrayer(Player player, int prayerId) {
		if (!player.getPrayerActive()[prayerId]) {
			return;
		}
		PrayerData pd = PrayerData.prayerData.get(prayerId);
		player.getPrayerActive()[prayerId] = false;
		player.getPacketSender().sendConfig(pd.configId, 0);
		if (pd.hint != -1) {
			int hintId = getHeadHint(player);
			player.getAppearance().setHeadHint(hintId);
		}

		BonusManager.update(player);
		// Sounds.sendSound(player, Sound.DEACTIVATE_PRAYER_OR_CURSE);
	}

	/**
	 * Deactivates every prayer in the player's prayer book.
	 * 
	 * @param player
	 *            The player to deactivate prayers for.
	 */
	public static void deactivatePrayers(Player player) {
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i]) {
				deactivatePrayer(player, i);
			}
		}
	}

	public static void resetAll(Player player) {
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			PrayerData pd = PrayerData.prayerData.get(i);
			if (pd == null)
				continue;
			player.getPrayerActive()[i] = false;
			player.getPacketSender().sendConfig(pd.configId, 0);
			if (pd.hint != -1) {
				int hintId = getHeadHint(player);
				player.getAppearance().setHeadHint(hintId);
			}
		}
	}

	/**
	 * Gets the player's current head hint if they activate or deactivate a head
	 * prayer.
	 * 
	 * @param player
	 *            The player to fetch head hint index for.
	 * @return The player's current head hint index.
	 */
	private static int getHeadHint(Player player) {
		boolean[] prayers = player.getPrayerActive();
		if (prayers[PROTECT_FROM_MELEE])
			return 0;
		if (prayers[PROTECT_FROM_MISSILES])
			return 1;
		if (prayers[PROTECT_FROM_MAGIC])
			return 2;
		if (prayers[RETRIBUTION])
			return 3;
		if (prayers[SMITE])
			return 4;
		if (prayers[REDEMPTION])
			return 5;
		return -1;
	}

	/**
	 * Initializes the player's prayer drain once a first prayer has been
	 * selected.
	 * 
	 * @param player
	 *            The player to start prayer drain for.
	 */
	private static void startDrain(final Player player) {
		if (getDrain(player) <= 0 && !player.isDrainingPrayer())
			return;
		player.setDrainingPrayer(true);
		TaskManager.submit(new Task(1, player, false) {
			@Override
			public void execute() {

				double drainAmount = getDrain(player);

				if (drainAmount <= 0) {
					this.stop();
					return;
				}

				if (player.getPrayerPointDrain() < 0) {
					int total = player.getSkillManager().getCurrentLevel(Skill.PRAYER) - 1;
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, total, true);
					player.setPrayerPointDrain(1.0);
				}

				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
					deactivatePrayers(player);
					player.getPacketSender().sendMessage("You have run out of Prayer points!");
					this.stop();
					return;
				}

				player.setPrayerPointDrain(player.getPrayerPointDrain() - drainAmount);

			}

			@Override
			public void stop() {
				setEventRunning(false);
				player.setDrainingPrayer(false);
			}
		});
	}

	/**
	 * Gets the amount of prayer to drain for <code>player</code>.
	 * 
	 * @param player
	 *            The player to get drain amount for.
	 * @return The amount of prayer that will be drained from the player.
	 */
	private static final double getDrain(Player player) {
		double toRemove = 0.0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i]) {
				PrayerData prayerData = PrayerData.prayerData.get(i);
				toRemove += prayerData.drainRate / 10;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1 + (0.05 * player.getBonusManager().getOtherBonus()[1]));
		}
		return toRemove;
	}

	/**
	 * Checks if a player has no prayer on.
	 * 
	 * @param player
	 *            The player to check prayer status for.
	 * @param exceptionId
	 *            The prayer id currently being turned on/activated.
	 * @return if <code>true</code>, it means player has no prayer on besides
	 *         <code>exceptionId</code>.
	 */
	private final static boolean hasNoPrayerOn(Player player, int exceptionId) {
		int prayersOn = 0;
		for (int i = 0; i < player.getPrayerActive().length; i++) {
			if (player.getPrayerActive()[i] && i != exceptionId)
				prayersOn++;
		}
		return prayersOn == 0;
	}

	/**
	 * Resets <code> prayers </code> with an exception for
	 * <code> prayerID </code>
	 * 
	 * @param prayers
	 *            The array of prayers to reset
	 * @param prayerID
	 *            The prayer ID to not turn off (exception)
	 */
	public static void resetPrayers(Player player, int[] prayers, int prayerID) {
		for (int i = 0; i < prayers.length; i++) {
			if (prayers[i] != prayerID)
				deactivatePrayer(player, prayers[i]);
		}
	}

	/**
	 * Resets prayers in the array
	 * 
	 * @param player
	 * @param prayers
	 */
	public static void resetPrayers(Player player, int[] prayers) {
		for (int i = 0; i < prayers.length; i++) {
			deactivatePrayer(player, prayers[i]);
		}
	}









	public static void sendQuickPrayersClick(Player player) {
		player.setQuickPrayersActive(!player.isQuickPrayersActive());
		player.getPacketSender().sendConfig(QUICK_PRAYERS_CONFIG_ID, player.isQuickPrayersActive() ? 1 : 0);

		// Activate/deactivate selected quick prayers
		boolean[] quickPrayers = player.getQuickPrayersSelection();
		for (int i = 0; i < quickPrayers.length; i++) {
			if (quickPrayers[i]) { // If this prayer is selected for quick prayers
				PrayerData pd = PrayerData.prayerData.get(i);
				if (pd != null) {
					if (player.isQuickPrayersActive()) {
						activatePrayer(player, i); // Activate it
						player.getPacketSender().sendConsoleMessage("Quick Prayer ON: " + pd.getPrayerName() + " (Ordinal: " + i + ")");
					} else {
						deactivatePrayer(player, i); // Deactivate it
						player.getPacketSender().sendConsoleMessage("Quick Prayer OFF: " + pd.getPrayerName() + " (Ordinal: " + i + ")");
					}
				}
			}
		}
	}

public static void handleQuickPrayerSelectionButton(Player player, int buttonId) {
    Integer prayerOrdinal = QUICK_PRAYER_BUTTON_TO_ORDINAL.get(buttonId);
    if (prayerOrdinal == null) {
        player.getPacketSender().sendConsoleMessage("ERROR: Unmapped quick prayer button clicked: " + buttonId);
        return; // Not a quick prayer selection button
    }

    // Toggle the prayer in the player's quickPrayersSelection
    boolean[] selection = player.getQuickPrayersSelection();
    if (prayerOrdinal < selection.length) {
        PrayerData pd = PrayerData.prayerData.get(prayerOrdinal);
        if (pd != null) {
            player.getPacketSender().sendConsoleMessage(
                "Clicked Quick Prayer: " + pd.getPrayerName() + " (Ordinal: " + prayerOrdinal + ")"
            );

            // If selecting (not deselecting), check for conflicts with other prayers
            // Use the same conflict logic as activatePrayer
            if (!selection[prayerOrdinal]) {
                switch (prayerOrdinal) {
                case THICK_SKIN:
                case ROCK_SKIN:
                case STEEL_SKIN:
                    uncheckPrayersInSelection(player, selection, DEFENCE_PRAYERS, prayerOrdinal);
                    break;
                case BURST_OF_STRENGTH:
                case SUPERHUMAN_STRENGTH:
                case ULTIMATE_STRENGTH:
                    uncheckPrayersInSelection(player, selection, STRENGTH_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, RANGED_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, MAGIC_PRAYERS, prayerOrdinal);
                    break;
                case CLARITY_OF_THOUGHT:
                case IMPROVED_REFLEXES:
                case INCREDIBLE_REFLEXES:
                    uncheckPrayersInSelection(player, selection, ATTACK_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, RANGED_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, MAGIC_PRAYERS, prayerOrdinal);
                    break;
                case SHARP_EYE:
                case HAWK_EYE:
                case EAGLE_EYE:
                case MYSTIC_WILL:
                case MYSTIC_LORE:
                case MYSTIC_MIGHT:
                    uncheckPrayersInSelection(player, selection, STRENGTH_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, ATTACK_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, RANGED_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, MAGIC_PRAYERS, prayerOrdinal);
                    break;
                case CHIVALRY:
                case PIETY:
                case RIGOUR:
                case AUGURY:
                    uncheckPrayersInSelection(player, selection, DEFENCE_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, STRENGTH_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, ATTACK_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, RANGED_PRAYERS, prayerOrdinal);
                    uncheckPrayersInSelection(player, selection, MAGIC_PRAYERS, prayerOrdinal);
                    break;
                case PROTECT_FROM_MAGIC:
                case PROTECT_FROM_MISSILES:
                case PROTECT_FROM_MELEE:
                    uncheckPrayersInSelection(player, selection, OVERHEAD_PRAYERS, prayerOrdinal);
                    break;
                case RETRIBUTION:
                case REDEMPTION:
                case SMITE:
                    uncheckPrayersInSelection(player, selection, OVERHEAD_PRAYERS, prayerOrdinal);
                    break;
                }
            }

            selection[prayerOrdinal] = !selection[prayerOrdinal]; // Toggle the prayer

            // Send config to update the client-side checkmark
            // Array logic: true = selected, false = unselected
            // Config logic: 0 = checked, 1 = unchecked
            Integer clientConfigId = PRAYER_ORDINAL_TO_CLIENT_CONFIG.get(prayerOrdinal);
            if (clientConfigId != null) {
                player.getPacketSender().sendConfig(clientConfigId, selection[prayerOrdinal] ? 0 : 1);
            }
        }
    }
}

	/**
	 * Unchecks prayers in a category from the quick prayers selection array
	 * and updates their client configs
	 *
	 * @param player The player
	 * @param selection The quick prayers selection array
	 * @param prayers The category of prayers to uncheck
	 * @param exceptPrayerId The prayer to exclude from unchecking
	 */
	private static void uncheckPrayersInSelection(Player player, boolean[] selection, int[] prayers, int exceptPrayerId) {
		for (int prayerId : prayers) {
			if (prayerId != exceptPrayerId && prayerId < selection.length) {
				if (selection[prayerId]) {
					selection[prayerId] = false;
					// Update client config for unchecked prayer
					// Array logic: true = selected, false = unselected
					// Config logic: 0 = checked, 1 = unchecked
					Integer configId = PRAYER_ORDINAL_TO_CLIENT_CONFIG.get(prayerId);
					if (configId != null) {
						player.getPacketSender().sendConfig(configId, 1);
					}
				}
			}
		}
	}

	public static void sendConfirmQuickPrayers(Player player) {
		// Restore the normal prayer book interface to tab 5
		player.getPacketSender().sendTabInterface(5, 5608);
		player.getPacketSender().sendMessage("Your quick prayers have been saved.");

		// Update the quick prayers orb config to reflect any changes
		player.getPacketSender().sendConfig(QUICK_PRAYERS_CONFIG_ID, player.isQuickPrayersActive() ? 1 : 0);
	}

	public static void sendQuickPrayersInterface(Player player) {
		// Switch to prayer tab and set the quick prayers interface
		player.getPacketSender().sendTab(5);
		player.getPacketSender().sendTabInterface(5, 17200);

		// Initialize ALL quick prayer configs to unchecked (1) first
		for (int configId = 630; configId <= 659; configId++) {
			player.getPacketSender().sendConfig(configId, 1);
		}

		// Then set the selected prayers to checked (0)
		// Array logic: true = selected, false = unselected
		// Config logic: 0 = checked, 1 = unchecked
		boolean[] selection = player.getQuickPrayersSelection();
		for (int prayerOrdinal = 0; prayerOrdinal < selection.length; prayerOrdinal++) {
			Integer clientConfigId = PRAYER_ORDINAL_TO_CLIENT_CONFIG.get(prayerOrdinal);
			if (clientConfigId != null && selection[prayerOrdinal]) {
				// If true (selected), send 0 (checked)
				player.getPacketSender().sendConfig(clientConfigId, 0);
			}
		}
	}

	/**
	 * Checks if action button ID is a prayer button.
	 * 
	 * @param buttonId
	 *            action button being hit.
	 */
	public static final boolean isButton(final int actionButtonID) {
		return PrayerData.actionButton.containsKey(actionButtonID);
	}

	public static final int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3,
			MYSTIC_WILL = 4, ROCK_SKIN = 5, SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8,
			RAPID_HEAL = 9, PROTECT_ITEM = 10, HAWK_EYE = 11, MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14,
			INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16, PROTECT_FROM_MISSILES = 17, PROTECT_FROM_MELEE = 18,
			EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22, SMITE = 23, PRESERVE = 24,
			CHIVALRY = 25, PIETY = 26, RIGOUR = 27, AUGURY = 28;

	/**
	 * Contains every prayer that counts as a defense prayer.
	 */
	private static final int[] DEFENCE_PRAYERS = { THICK_SKIN, ROCK_SKIN, STEEL_SKIN, CHIVALRY, PIETY, RIGOUR, AUGURY };

	/**
	 * Contains every prayer that counts as a strength prayer.
	 */
	private static final int[] STRENGTH_PRAYERS = { BURST_OF_STRENGTH, SUPERHUMAN_STRENGTH, ULTIMATE_STRENGTH, CHIVALRY,
			PIETY };

	/**
	 * Contains every prayer that counts as an attack prayer.
	 */
	private static final int[] ATTACK_PRAYERS = { CLARITY_OF_THOUGHT, IMPROVED_REFLEXES, INCREDIBLE_REFLEXES, CHIVALRY,
			PIETY };

	/**
	 * Contains every prayer that counts as a ranged prayer.
	 */
	private static final int[] RANGED_PRAYERS = { SHARP_EYE, HAWK_EYE, EAGLE_EYE, RIGOUR };

	/**
	 * Contains every prayer that counts as a magic prayer.
	 */
	private static final int[] MAGIC_PRAYERS = { MYSTIC_WILL, MYSTIC_LORE, MYSTIC_MIGHT, AUGURY };

	/**
	 * Contains every prayer that counts as an overhead prayer, excluding
	 * protect from summoning.
	 */
	public static final int[] OVERHEAD_PRAYERS = { PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE,
			RETRIBUTION, REDEMPTION, SMITE };

	/**
	 * Contains every protection prayer
	 */
	public static final int[] PROTECTION_PRAYERS = { PROTECT_FROM_MAGIC, PROTECT_FROM_MISSILES, PROTECT_FROM_MELEE };

	// Custom quick prayer constants
	public static final int QUICK_PRAYERS_TOGGLE_BUTTON = 20000;
	public static final int QUICK_PRAYERS_SELECT_BUTTON = 20001;
	public static final int QUICK_PRAYERS_CONFIG_ID = 40000;
}
