package com.intelligence.block.entity;

import com.intelligence.Intelligence;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ResearchTableScreenHandler> RESEARCH_TABLE =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Intelligence.MOD_ID, "research_table"),
                    new ScreenHandlerType<>(ResearchTableScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

    public static void register() {
        Intelligence.LOGGER.info("Registering Screen Handlers for " + Intelligence.MOD_ID);
    }
}