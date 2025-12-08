package com.intelligence.item;

import com.intelligence.Intelligence;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final RegistryKey<Item> INTELLIGENCE_SHARD_KEY =
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Intelligence.MOD_ID, "intelligence_shard"));

    public static final RegistryKey<Item> INTELLIGENCE_SWORD_KEY =
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Intelligence.MOD_ID, "intelligence_sword"));

    public static final RegistryKey<Item> CRYSTAL_KEY =
            RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Intelligence.MOD_ID, "crystal"));
    public static final Item INTELLIGENCE_SHARD = register("intelligence_shard",
            new IntelligenceShardItem(new Item.Settings().maxCount(16).registryKey(INTELLIGENCE_SHARD_KEY)));

    public static final Item INTELLIGENCE_SWORD = register("intelligence_sword",
            new IntelligenceSwordItem(
                    ToolMaterial.DIAMOND,
                    new Item.Settings().registryKey(INTELLIGENCE_SWORD_KEY)
            ));

    public static final Item CRYSTAL = register("crystal",
            new CrystalItem(new Item.Settings().registryKey(CRYSTAL_KEY)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Intelligence.MOD_ID, name), item);
    }

    public static void register() {
        Intelligence.LOGGER.info("Registering items for " + Intelligence.MOD_ID);
    }
}