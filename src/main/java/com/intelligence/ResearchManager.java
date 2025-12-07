package com.intelligence;

import java.util.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

public class ResearchManager {
    private static final Map<UUID, Set<Item>> unlockedItems = new HashMap<>();
    private static final Map<UUID, Set<Item>> clientUnlockedItems = new HashMap<>();

    public static boolean isUnlocked(ServerPlayer player, Item item) {
        UUID uuid = player.getUUID();
        return unlockedItems.getOrDefault(uuid, new HashSet<>()).contains(item);
    }

    public static boolean isUnlockedClient(UUID playerUuid, Item item) {
        return clientUnlockedItems.getOrDefault(playerUuid, new HashSet<>()).contains(item);
    }

    public static void setClientUnlocked(UUID playerUuid, Set<Item> items) {
        clientUnlockedItems.put(playerUuid, new HashSet<>(items));
    }

    public static void unlock(ServerPlayer player, Item item) {
        UUID uuid = player.getUUID();
        unlockedItems.computeIfAbsent(uuid, k -> new HashSet<>()).add(item);
    }

    public static int getResearchCost(Item item) {
        int baseCost = CraftingRestrictions.getRequirement(item);
        if (baseCost == 0) return 0;

        // Cost is 10-20 more than the requirement
        Random random = new Random(item.toString().hashCode());
        return baseCost + 10 + random.nextInt(10, 20); // +10 to +20
    }

    public static Set<Item> getUnlockedItems(ServerPlayer player) {
        return new HashSet<>(unlockedItems.getOrDefault(player.getUUID(), new HashSet<>()));
    }

    public static void clear(ServerPlayer player) {
        unlockedItems.remove(player.getUUID());
    }
}