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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {


    public static final Block RESEARCH_TABLE = registerBlock("research_table",
            new ResearchTableBlock(AbstractBlock.Settings.create()
                    .strength(2.5f)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()));



    public static final Block CRYSTAL_ORE = registerBlock("crystal_ore",
            new Block(AbstractBlock.Settings.create()
                    .strength(3.0f, 3.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .requiresTool()));

    public static final Block DEEPSLATE_CRYSTAL_ORE = registerBlock("deepslate_crystal_ore1",
            new Block(AbstractBlock.Settings.create()
                    .strength(4.5f, 3.0f)
                    .sounds(BlockSoundGroup.DEEPSLATE)
                    .requiresTool()));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Intelligence.MOD_ID, name), block);
    }

    public static void register() {
        Intelligence.LOGGER.info("Registering Blocks for " + Intelligence.MOD_ID);
    }
}