package com.intelligence.item;

import com.intelligence.Intelligence;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class ModItems {

    public static final ResourceKey<Item> INTELLIGENCE_SHARD_KEY =
            ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "intelligence_shard"));

    public static final ResourceKey<Item> INTELLIGENCE_SWORD_KEY =
            ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "intelligence_sword"));

    public static final ResourceKey<Item> CRYSTAL_KEY =
            ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "crystal"));
    public static final Item INTELLIGENCE_SHARD = register("intelligence_shard",
            new IntelligenceShardItem(new Item.Properties().stacksTo(16).setId(INTELLIGENCE_SHARD_KEY)));

    public static final Item INTELLIGENCE_SWORD = register("intelligence_sword",
            new IntelligenceSwordItem(
                    ToolMaterial.DIAMOND,
                    new Item.Properties().setId(INTELLIGENCE_SWORD_KEY)
            ));

    public static final Item CRYSTAL = register("crystal",
            new CrystalItem(new Item.Properties().setId(CRYSTAL_KEY)));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, name), item);
    }

    public static void register() {
        Intelligence.LOGGER.info("Registering items for " + Intelligence.MOD_ID);
    }
}