package com.intelligence;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IntelligenceManager {
    private static final Map<UUID, Integer> intelligenceData = new HashMap<>();

    public static void register() {
        // Sync intelligence to clients periodically
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 20 == 0) { // Every second
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    syncToClient(player);
                }
            }
        });
    }

    public static int getIntelligence(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        return intelligenceData.getOrDefault(uuid, 0);
    }

    public static void setIntelligence(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUuid();
        intelligenceData.put(uuid, amount);
        syncToClient(player);
    }

    public static void addIntelligence(ServerPlayerEntity player, int amount) {
        int current = getIntelligence(player);
        setIntelligence(player, current + amount);
    }

    private static void syncToClient(ServerPlayerEntity player) {
        // Send intelligence value to client for HUD display
        IntelligenceNetworking.sendIntelligenceUpdate(player, getIntelligence(player));
    }
}