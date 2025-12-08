package com.intelligence.item;

import com.intelligence.Intelligence;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item INTELLIGENCE_SHARD = register("intelligence_shard",
            new IntelligenceShardItem(new Item.Settings().maxCount(16)));

    public static final Item INTELLIGENCE_SWORD = register("intelligence_sword",
            new IntelligenceSwordItem(
                    ToolMaterials.DIAMOND,
                    new Item.Settings()
                            .attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.DIAMOND, 3, -2.4f))
            ));

    public static final Item CRYSTAL = register("crystal",
            new CrystalItem(new Item.Settings()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Intelligence.MOD_ID, name), item);
    }

    public static void register() {
        Intelligence.LOGGER.info("Registering items for " + Intelligence.MOD_ID);
    }
}