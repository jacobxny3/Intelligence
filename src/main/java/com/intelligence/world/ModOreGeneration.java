package com.intelligence.world;

import com.intelligence.Intelligence;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModOreGeneration {
    public static final RegistryKey<PlacedFeature> CRYSTAL_ORE_PLACED_KEY = RegistryKey.of(
            RegistryKeys.PLACED_FEATURE,
            Identifier.of(Intelligence.MOD_ID, "crystal_ore")
    );

    public static void register() {
        // Add ore generation to all overworld biomes
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                CRYSTAL_ORE_PLACED_KEY
        );

        Intelligence.LOGGER.info("Registering ore generation for " + Intelligence.MOD_ID);
    }
}