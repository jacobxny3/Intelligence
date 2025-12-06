package com.intelligence.block;

import com.intelligence.Intelligence;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block RESEARCH_TABLE = registerBlock("research_table",
            new ResearchTableBlock(AbstractBlock.Settings.create()
                    .strength(2.5f)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Intelligence.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Intelligence.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void register() {
        Intelligence.LOGGER.info("Registering Blocks for " + Intelligence.MOD_ID);
    }
}