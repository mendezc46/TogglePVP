package com.justinm.togglepvp.handler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.LinkedHashMap;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import net.minecraftforge.fml.loading.FMLPaths;

public class PVPStatsManager {
    private static final File statsFile = new File(FMLPaths.GAMEDIR.get().toFile(), "pvptoggle/stats.yaml");
    private static final Map<UUID, Integer> kills = new HashMap<>();
    private static final Map<UUID, Integer> deaths = new HashMap<>();
    private static final Map<UUID, String> playerNames = new HashMap<>();

    public static void loadStats() {
        if (!statsFile.exists()) {
            return;
        }

        try {
            Yaml yaml = new Yaml();
            try (FileReader reader = new FileReader(statsFile)) {
                Map<String, Object> data = yaml.load(reader);
                
                if (data != null) {
                    if (data.containsKey("players")) {
                        Map<String, Map<String, Object>> players = (Map<String, Map<String, Object>>) data.get("players");
                        players.forEach((name, stats) -> {
                            String uuidStr = (String) stats.get("uuid");
                            UUID uuid = UUID.fromString(uuidStr);
                            playerNames.put(uuid, name);
                            
                            if (stats.containsKey("kills")) {
                                kills.put(uuid, (Integer) stats.get("kills"));
                            }
                            if (stats.containsKey("deaths")) {
                                deaths.put(uuid, (Integer) stats.get("deaths"));
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveStats() {
        try {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            
            Yaml yaml = new Yaml(options);
            
            Map<String, Object> data = new LinkedHashMap<>();
            Map<String, Map<String, Object>> players = new LinkedHashMap<>();
            
            // Combinar todos los UUIDs
            HashMap<UUID, String> allPlayers = new HashMap<>(playerNames);
            kills.keySet().forEach(uuid -> allPlayers.putIfAbsent(uuid, "Unknown"));
            deaths.keySet().forEach(uuid -> allPlayers.putIfAbsent(uuid, "Unknown"));
            
            allPlayers.forEach((uuid, name) -> {
                Map<String, Object> playerData = new LinkedHashMap<>();
                playerData.put("uuid", uuid.toString());
                playerData.put("kills", kills.getOrDefault(uuid, 0));
                playerData.put("deaths", deaths.getOrDefault(uuid, 0));
                players.put(name, playerData);
            });
            
            data.put("players", players);
            
            statsFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(statsFile)) {
                yaml.dump(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPlayerName(UUID playerUUID, String name) {
        playerNames.put(playerUUID, name);
    }

    public static void addKill(UUID playerUUID) {
        kills.put(playerUUID, kills.getOrDefault(playerUUID, 0) + 1);
        saveStats();
    }

    public static void addDeath(UUID playerUUID) {
        deaths.put(playerUUID, deaths.getOrDefault(playerUUID, 0) + 1);
        saveStats();
    }

    public static int getKills(UUID playerUUID) {
        return kills.getOrDefault(playerUUID, 0);
    }

    public static int getDeaths(UUID playerUUID) {
        return deaths.getOrDefault(playerUUID, 0);
    }
}
