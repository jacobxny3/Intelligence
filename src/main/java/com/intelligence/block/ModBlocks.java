package com.intelligence.block;

import com.intelligence.Intelligence;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {


    public static final RegistryKey<Block> RESEARCH_TABLE_KEY =
            RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Intelligence.MOD_ID, "research_table"));

    public static final RegistryKey<Block> CRYSTAL_ORE_KEY =
            RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Intelligence.MOD_ID, "crystal_ore"));

    public static final RegistryKey<Block> DEEPSLATE_CRYSTAL_ORE_KEY =
            RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Intelligence.MOD_ID, "deepslate_crystal_ore1"));

    public static final Block RESEARCH_TABLE = registerBlock("research_table",
            new ResearchTableBlock(AbstractBlock.Settings.create()
                    .registryKey(RESEARCH_TABLE_KEY)
                    .strength(2.5f)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()));



    public static final Block CRYSTAL_ORE = registerBlock("crystal_ore",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(CRYSTAL_ORE_KEY)
                    .strength(3.0f, 3.0f)
                    .sounds(BlockSoundGroup.STONE)
                    .requiresTool()));

    public static final Block DEEPSLATE_CRYSTAL_ORE = registerBlock("deepslate_crystal_ore1",
            new Block(AbstractBlock.Settings.create()
                    .registryKey(DEEPSLATE_CRYSTAL_ORE_KEY)
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