package com.intelligence.block;

import com.intelligence.Intelligence;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {


    public static final ResourceKey<Block> RESEARCH_TABLE_KEY =
            ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "research_table"));

    public static final Block RESEARCH_TABLE =
            registerBlock(RESEARCH_TABLE_KEY,
                    new ResearchTableBlock(BlockBehaviour.Properties.of()
                            .setId(RESEARCH_TABLE_KEY)
                            .strength(2.5f)
                            .sound(SoundType.WOOD)
                            .noOcclusion()
                    )
            );

    private static Block registerBlock(ResourceKey<Block> key, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, key, block);
        return block;
    }


    public static void register() {
        Intelligence.LOGGER.info("Registering Blocks for " + Intelligence.MOD_ID);
    }
}