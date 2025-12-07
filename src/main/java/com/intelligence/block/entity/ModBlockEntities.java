package com.intelligence.block.entity;

import com.intelligence.Intelligence;
import com.intelligence.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static final BlockEntityType<ResearchTableBlockEntity> RESEARCH_TABLE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "research_table"),
            FabricBlockEntityTypeBuilder.create(ResearchTableBlockEntity::new, ModBlocks.RESEARCH_TABLE).build()
    );

    public static void register() {
        Intelligence.LOGGER.info("Registering Block Entities for " + Intelligence.MOD_ID);
    }
}