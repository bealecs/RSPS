package com.elvarg.engine.task.impl;

import com.elvarg.engine.task.Task;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Skill;

/**
 * Handles player run energy depletion and repletion.
 *
 * - Depletes energy when running (scaled by agility level)
 * - Restores energy when not running or standing still (scaled by agility level)
 */
public class PlayerRunEnergyTask extends Task {

    public PlayerRunEnergyTask(Player player) {
        super(1, player, false); // Run every tick (600ms)
        this.player = player;
    }

    private final Player player;
    private int lastEnergy = -1;

    @Override
    public void execute() {
        if (player == null || !player.isRegistered()) {
            stop();
            return;
        }

        int currentEnergy = player.getRunEnergy();

        // Skip energy drain if player has unlimited run energy
        if (player.hasUnlimitedRunEnergy()) {
            // Ensure energy stays at 100
            if (currentEnergy < 100) {
                player.setRunEnergy(100);
                player.getPacketSender().sendRunEnergy(100);
                lastEnergy = 100;
            }
            return;
        }

        // Check if player is moving
        boolean isMoving = player.getMovementQueue().isMoving();
        boolean isRunning = player.isRunning();

        // Get agility level for scaling
        int agilityLevel = player.getSkillManager().getCurrentLevel(Skill.AGILITY);

        // Handle run energy depletion
        if (isMoving && isRunning && currentEnergy > 0) {
            int energyDrain = calculateEnergyDrain(agilityLevel);
            currentEnergy -= energyDrain;

            if (currentEnergy < 0) {
                currentEnergy = 0;
            }

            // If run energy reaches 0, disable running
            if (currentEnergy == 0) {
                player.setRunning(false);
                player.getPacketSender().sendRunStatus();
                player.getPacketSender().sendMessage("You have run out of energy!");
            }

            player.setRunEnergy(currentEnergy);
        }
        // Handle run energy repletion
        else if (currentEnergy < 100) {
            int energyGain = calculateEnergyRestore(agilityLevel);
            currentEnergy += energyGain;

            if (currentEnergy > 100) {
                currentEnergy = 100;
            }

            player.setRunEnergy(currentEnergy);
        }

        // Only send packet if energy changed
        if (currentEnergy != lastEnergy) {
            player.getPacketSender().sendRunEnergy(currentEnergy);
            lastEnergy = currentEnergy;
        }
    }

    /**
     * Calculates the energy drain per tick when running.
     * Higher agility = less drain
     *
     * Base drain: 1 energy per tick at level 1
     * Max efficiency: 0.33 energy per tick at level 99
     */
    private int calculateEnergyDrain(int agilityLevel) {
        // Base drain is 100 units per tick (we use 100x multiplier for precision)
        // Agility reduces drain: -0.67 per level
        // At level 1: 100 drain
        // At level 99: ~33 drain
        int drainBase = 100;
        int agilityReduction = (int) (agilityLevel * 0.67);
        int totalDrain = drainBase - agilityReduction;

        // Convert back to actual energy units (divide by 100)
        // We only drain every ~1-3 ticks depending on agility
        if (totalDrain <= 33) {
            // High agility: drain every 3 ticks
            return tickCounter % 3 == 0 ? 1 : 0;
        } else if (totalDrain <= 66) {
            // Medium agility: drain every 2 ticks
            return tickCounter % 2 == 0 ? 1 : 0;
        } else {
            // Low agility: drain every tick
            return 1;
        }
    }

    private int tickCounter = 0;

    {
        // Initialize tick counter in instance initializer
        tickCounter = 0;
    }

    /**
     * Calculates the energy restore per tick when not running.
     * Higher agility = faster restore
     *
     * Base restore: 1 energy per 12 ticks (~7.2 seconds) at level 1
     * Max restore: 1 energy per 4 ticks (~2.4 seconds) at level 99
     */
    private int calculateEnergyRestore(int agilityLevel) {
        tickCounter++;

        // Base restore frequency: 12 ticks at level 1, 4 ticks at level 99
        // Formula: 12 - (agility * 0.08) = ticks per restore
        int restoreFrequency = 12 - (int) (agilityLevel * 0.08);
        if (restoreFrequency < 4) {
            restoreFrequency = 4; // Minimum 4 ticks
        }

        // Restore 1 energy every restoreFrequency ticks
        if (tickCounter % restoreFrequency == 0) {
            return 1;
        }

        return 0;
    }
}
