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


    public static final RegistryKey<Block> RESEARCH_TABLE_KEY =
            RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Intelligence.MOD_ID, "research_table"));

    public static final Block RESEARCH_TABLE =
            registerBlock(RESEARCH_TABLE_KEY,
                    new ResearchTableBlock(AbstractBlock.Settings.create()
                            .registryKey(RESEARCH_TABLE_KEY)
                            .strength(2.5f)
                            .sounds(BlockSoundGroup.WOOD)
                            .nonOpaque()
                    )
            );

    private static Block registerBlock(RegistryKey<Block> key, Block block) {
        Registry.register(Registries.BLOCK, key, block);
        return block;
    }


    public static void register() {
        Intelligence.LOGGER.info("Registering Blocks for " + Intelligence.MOD_ID);
    }
}