package com.justinm.togglepvp.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

public class PVPToggleHandler {
    private static final Map<UUID, Boolean> playerPVPStatus = new HashMap<>();
    private static final Map<UUID, Long> pvpCombatTime = new HashMap<>();
    private static final Set<UUID> playersToKill = new HashSet<>();
    private static final Map<UUID, Long> pvpCooldown = new HashMap<>();
    private static final long COMBAT_DURATION = 60000; // 60 segundos en milisegundos
    private static final long COOLDOWN_DURATION = 5000; // 5 segundos cooldown
    private static boolean serverShuttingDown = false;
    private static String globalPVPMode = "free"; // free, locked-on, locked-off

    public static boolean isPVPEnabled(UUID playerUUID) {
        return playerPVPStatus.getOrDefault(playerUUID, false);
    }

    public static void setPVPStatus(UUID playerUUID, boolean enabled) {
        playerPVPStatus.put(playerUUID, enabled);
    }

    public static void removePlayer(UUID playerUUID) {
        playerPVPStatus.remove(playerUUID);
        pvpCombatTime.remove(playerUUID);
    }

    public static void togglePVP(UUID playerUUID) {
        boolean currentStatus = isPVPEnabled(playerUUID);
        setPVPStatus(playerUUID, !currentStatus);
    }

    public static void enterCombat(UUID playerUUID) {
        pvpCombatTime.put(playerUUID, System.currentTimeMillis());
    }

    public static boolean isInCombat(UUID playerUUID) {
        Long combatTime = pvpCombatTime.get(playerUUID);
        if (combatTime == null) {
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - combatTime;
        if (elapsed >= COMBAT_DURATION) {
            pvpCombatTime.remove(playerUUID);
            return false;
        }
        return true;
    }

    public static long getCombatTimeRemaining(UUID playerUUID) {
        Long combatTime = pvpCombatTime.get(playerUUID);
        if (combatTime == null) {
            return 0;
        }
        
        long elapsed = System.currentTimeMillis() - combatTime;
        long remaining = COMBAT_DURATION - elapsed;
        return Math.max(0, remaining / 1000); // retorna segundos
    }

    public static void exitCombat(UUID playerUUID) {
        pvpCombatTime.remove(playerUUID);
    }

    public static void setServerShuttingDown(boolean shuttingDown) {
        serverShuttingDown = shuttingDown;
    }

    public static boolean isServerShuttingDown() {
        return serverShuttingDown;
    }

    public static void markForKill(UUID playerUUID) {
        playersToKill.add(playerUUID);
    }

    public static boolean shouldBeKilled(UUID playerUUID) {
        return playersToKill.contains(playerUUID);
    }

    public static void clearKillMark(UUID playerUUID) {
        playersToKill.remove(playerUUID);
    }

    public static void setGlobalPVPMode(String mode) {
        globalPVPMode = mode;
    }

    public static String getGlobalPVPMode() {
        return globalPVPMode;
    }

    public static boolean canTogglePVP(UUID playerUUID) {
        if (globalPVPMode.equals("locked-on") || globalPVPMode.equals("locked-off")) {
            return false;
        }
        return true;
    }

    public static boolean hasCooldown(UUID playerUUID) {
        Long lastToggle = pvpCooldown.get(playerUUID);
        if (lastToggle == null) {
            return false;
        }
        return (System.currentTimeMillis() - lastToggle) < COOLDOWN_DURATION;
    }

    public static void setCooldown(UUID playerUUID) {
        pvpCooldown.put(playerUUID, System.currentTimeMillis());
    }
}
