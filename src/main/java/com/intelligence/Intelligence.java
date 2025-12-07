package com.intelligence;

import com.intelligence.block.ModBlocks;
import com.intelligence.block.entity.ModBlockEntities;
import com.intelligence.block.entity.ModScreenHandlers;
import com.intelligence.block.entity.ResearchTableScreenHandler;
import com.intelligence.entity.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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

        // Register blocks and block entities
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModScreenHandlers.register();
        Items.register(ModBlocks.RESEARCH_TABLE);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(ModBlocks.RESEARCH_TABLE));
        IntelligenceManager.register();
        CraftingRestrictions.register();

        // Register research packet handler
        PayloadTypeRegistry.playC2S().register(ResearchPayload.ID, ResearchPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ResearchPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                if (player.currentScreenHandler instanceof ResearchTableScreenHandler handler) {
                    handler.tryResearch(player);
                }
            });
        });

        // Give players initial intelligence on join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (IntelligenceManager.getIntelligence(player) == 0) {
                IntelligenceManager.setIntelligence(player, 10); // Starting intelligence
                player.sendMessage(Text.literal("§6Thanks for using Intelligence!, You can gain Intelligence points by crafting books, bookshelves, and more."), false);
                player.sendMessage(Text.literal("§6You start with 10 Intelligence points."), false);
            }
        });

        // Award intelligence for breaking bookshelves (getting books)
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient && state.getBlock() == net.minecraft.block.Blocks.BOOKSHELF) {
                IntelligenceManager.addIntelligence((ServerPlayerEntity) player, 2);
                player.sendMessage(Text.literal("§a+2 Intelligence! (Bookshelf broken)"), true);
            }
        });
    }
}