package com.intelligence;

import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class ResearchManager {
    private static final Map<UUID, Set<Item>> unlockedItems = new HashMap<>();
    private static final Map<UUID, Set<Item>> clientUnlockedItems = new HashMap<>();

    public static boolean isUnlocked(ServerPlayerEntity player, Item item) {
        UUID uuid = player.getUuid();
        return unlockedItems.getOrDefault(uuid, new HashSet<>()).contains(item);
    }

    public static boolean isUnlockedClient(UUID playerUuid, Item item) {
        return clientUnlockedItems.getOrDefault(playerUuid, new HashSet<>()).contains(item);
    }

    public static void setClientUnlocked(UUID playerUuid, Set<Item> items) {
        clientUnlockedItems.put(playerUuid, new HashSet<>(items));
    }

    public static void unlock(ServerPlayerEntity player, Item item) {
        UUID uuid = player.getUuid();
        unlockedItems.computeIfAbsent(uuid, k -> new HashSet<>()).add(item);
    }

    public static int getResearchCost(Item item) {
        int baseCost = CraftingRestrictions.getRequirement(item);
        if (baseCost == 0) return 0;

        // Cost is 10-20 more than the requirement
        Random random = new Random(item.toString().hashCode());
        return baseCost + 10 + random.nextInt(10, 20); // +10 to +20
    }

    public static Set<Item> getUnlockedItems(ServerPlayerEntity player) {
        return new HashSet<>(unlockedItems.getOrDefault(player.getUuid(), new HashSet<>()));
    }

    public static void clear(ServerPlayerEntity player) {
        unlockedItems.remove(player.getUuid());
    }
}