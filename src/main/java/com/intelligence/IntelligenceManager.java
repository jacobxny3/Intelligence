package com.intelligence;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class IntelligenceManager {
    private static final Map<UUID, Integer> intelligenceData = new HashMap<>();

    public static void register() {
        // Sync intelligence to clients periodically
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 == 0) { // Every second
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (player.connection != null) {
                        syncToClient(player);
                    }

                }
            }
        });
    }

    public static int getIntelligence(ServerPlayer player) {
        UUID uuid = player.getUUID();
        return intelligenceData.getOrDefault(uuid, 0);
    }

    public static void setIntelligence(ServerPlayer player, int amount) {
        intelligenceData.put(player.getUUID(), amount);

        if (player.connection != null) { // ✔ only send when connection exists
            syncToClient(player);
        }
    }

    public static void addIntelligence(ServerPlayer player, int amount) {
        int current = getIntelligence(player);
        setIntelligence(player, current + amount);
    }

    private static void syncToClient(ServerPlayer player) {
        // Send intelligence value and research data to client for HUD display
        IntelligenceNetworking.sendIntelligenceUpdate(player, getIntelligence(player));
        IntelligenceNetworking.sendResearchSync(player, ResearchManager.getUnlockedItems(player));
    }
}