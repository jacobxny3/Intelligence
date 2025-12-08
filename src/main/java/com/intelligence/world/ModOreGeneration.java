package com.intelligence.world;

import com.intelligence.Intelligence;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModOreGeneration {
    public static final ResourceKey<PlacedFeature> CRYSTAL_ORE_PLACED_KEY = ResourceKey.create(
            Registries.PLACED_FEATURE,
            Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "crystal_ore")
    );

    public static void register() {
        // Add ore generation to all overworld biomes
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                CRYSTAL_ORE_PLACED_KEY
        );

        Intelligence.LOGGER.info("Registering ore generation for " + Intelligence.MOD_ID);
    }
}