package com.intelligence.block.entity;

import com.intelligence.Intelligence;
import com.intelligence.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<ResearchTableBlockEntity> RESEARCH_TABLE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(Intelligence.MOD_ID, "research_table"),
            BlockEntityType.Builder.create(ResearchTableBlockEntity::new, ModBlocks.RESEARCH_TABLE).build()
    );

    public static void register() {
        Intelligence.LOGGER.info("Registering Block Entities for " + Intelligence.MOD_ID);
    }
}