package com.intelligence;

import com.intelligence.client.IntelligenceModClient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Intelligence implements ModInitializer {
    public static final String MOD_ID = "intelligence";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Intelligence Mod Initialized!");

        // Register networking payloads first
        IntelligenceNetworking.registerPayloads();

        IntelligenceManager.register();
        CraftingRestrictions.register();

        // Give players initial intelligence on join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (IntelligenceManager.getIntelligence(player) == 0) {
                IntelligenceManager.setIntelligence(player, 10);
                player.displayClientMessage(Component.literal("§6Thanks for using Intelligence!, You can gain Intelligence points by crafting books, bookshelves, and more."), false);
                player.displayClientMessage(Component.literal("§6You start with 10 Intelligence points."), false);
            }
        });

        // Award intelligence for breaking bookshelves (getting books)
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClientSide() && state.getBlock() == net.minecraft.world.level.block.Blocks.BOOKSHELF) {
                IntelligenceManager.addIntelligence((ServerPlayer) player, 2);
                player.displayClientMessage(Component.literal("§a+2 Intelligence! (Bookshelf broken)"), true);
            }
        });
    }
}