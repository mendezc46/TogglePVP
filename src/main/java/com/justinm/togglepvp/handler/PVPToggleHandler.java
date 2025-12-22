package com.justinm.togglepvp.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public class PVPToggleHandler {
    private static final Map<UUID, Boolean> playerPVPStatus = new HashMap<>();
    private static final Map<UUID, Long> pvpCombatTime = new HashMap<>();
    private static final Set<UUID> playersToKill = new HashSet<>();
    private static final Map<UUID, Long> pvpCooldown = new HashMap<>();
    private static final long COMBAT_DURATION = 60000; // 60 segundos en milisegundos
    private static final long COOLDOWN_DURATION = 5000; // 5 segundos cooldown
    private static boolean serverShuttingDown = false;
    private static String globalPVPMode = "free"; // free, locked-on, locked-off
    
    // Rastrear entidades creadas por jugadores (rayos, proyectiles, etc.)
    private static final WeakHashMap<Entity, UUID> entityOwners = new WeakHashMap<>();
    private static final int MAX_TRACKED_ENTITIES = 500; // Límite de seguridad
    
    // Rastrear jugadores que acaban de atacar (para atribuir hechizos)
    private static final Map<UUID, Long> recentAttackers = new HashMap<>();
    private static final long ATTACK_ATTRIBUTION_WINDOW = 500; // 0.5 segundos (más corto para evitar falsos positivos)

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

    public static void trackEntity(Entity entity, UUID ownerUUID) {
        // Limitar tamaño para evitar crecimiento infinito
        if (entityOwners.size() >= MAX_TRACKED_ENTITIES) {
            entityOwners.clear(); // Reset de emergencia
        }
        entityOwners.put(entity, ownerUUID);
    }

    public static UUID getEntityOwner(Entity entity) {
        return entityOwners.get(entity);
    }

    public static void cleanupTrackedEntities() {
        // WeakHashMap limpia automáticamente, pero forzamos limpieza manual también
        entityOwners.entrySet().removeIf(entry -> entry.getKey() == null || !entry.getKey().isAlive());
        
        // Limpiar atacantes antiguos
        long now = System.currentTimeMillis();
        recentAttackers.entrySet().removeIf(entry -> (now - entry.getValue()) > ATTACK_ATTRIBUTION_WINDOW);
    }
    
    public static void markPlayerAttacking(UUID playerUUID) {
        recentAttackers.put(playerUUID, System.currentTimeMillis());
    }
    
    public static boolean isRecentAttacker(UUID playerUUID) {
        Long lastAttack = recentAttackers.get(playerUUID);
        if (lastAttack == null) return false;
        return (System.currentTimeMillis() - lastAttack) <= ATTACK_ATTRIBUTION_WINDOW;
    }
    
    public static UUID getRecentAttackerNearby(double x, double y, double z, MinecraftServer server, double maxDist) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (isRecentAttacker(player.getUUID())) {
                double dist = player.distanceToSqr(x, y, z);
                if (dist <= maxDist) {
                    return player.getUUID();
                }
            }
        }
        return null;
    }
}
