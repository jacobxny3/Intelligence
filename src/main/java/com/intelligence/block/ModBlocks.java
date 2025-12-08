package com.intelligence.block;

import com.intelligence.Intelligence;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {


    public static final ResourceKey<Block> RESEARCH_TABLE_KEY =
            ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "research_table"));

    public static final ResourceKey<Block> CRYSTAL_ORE_KEY =
            ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "crystal_ore"));

    public static final ResourceKey<Block> DEEPSLATE_CRYSTAL_ORE_KEY =
            ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "deepslate_crystal_ore1"));

    public static final Block RESEARCH_TABLE = registerBlock("research_table",
            new ResearchTableBlock(BlockBehaviour.Properties.of()
                    .setId(RESEARCH_TABLE_KEY)
                    .strength(2.5f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));



    public static final Block CRYSTAL_ORE = registerBlock("crystal_ore",
            new Block(BlockBehaviour.Properties.of()
                    .setId(CRYSTAL_ORE_KEY)
                    .strength(3.0f, 3.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final Block DEEPSLATE_CRYSTAL_ORE = registerBlock("deepslate_crystal_ore1",
            new Block(BlockBehaviour.Properties.of()
                    .setId(DEEPSLATE_CRYSTAL_ORE_KEY)
                    .strength(4.5f, 3.0f)
                    .sound(SoundType.DEEPSLATE)
                    .requiresCorrectToolForDrops()));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, name), block);
    }

    public static void register() {
        Intelligence.LOGGER.info("Registering Blocks for " + Intelligence.MOD_ID);
    }
}