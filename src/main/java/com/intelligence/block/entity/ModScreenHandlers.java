package com.intelligence.block.entity;

import com.intelligence.Intelligence;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModScreenHandlers {
    public static final MenuType<ResearchTableScreenHandler> RESEARCH_TABLE =
            Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "research_table"),
                    new MenuType<>(ResearchTableScreenHandler::new, FeatureFlags.VANILLA_SET));

    public static void register() {
        Intelligence.LOGGER.info("Registering Screen Handlers for " + Intelligence.MOD_ID);
    }
}