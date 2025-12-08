package com.intelligence;

import com.intelligence.block.ModBlocks;
import com.intelligence.block.entity.ModBlockEntities;
import com.intelligence.block.entity.ModScreenHandlers;
import com.intelligence.block.entity.ResearchTableScreenHandler;
import com.intelligence.entity.ModEntities;
import com.intelligence.item.ModItems;
import com.intelligence.sound.ModSounds;
import com.intelligence.world.ModOreGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Intelligence implements ModInitializer {
    public static final String MOD_ID = "intelligence";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Intelligence Mod Initialized!");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register(entries -> entries.add(ModBlocks.RESEARCH_TABLE.asItem()));


        // Register networking payloads first
        IntelligenceNetworking.registerPayloads();

        // Register blocks and block entities
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModScreenHandlers.register();
        Items.register(ModBlocks.RESEARCH_TABLE);
        Items.register(ModBlocks.CRYSTAL_ORE);
        Items.register(ModBlocks.DEEPSLATE_CRYSTAL_ORE);
        ModItems.register();
        ModSounds.register();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register(entries -> entries.add(ModItems.INTELLIGENCE_SHARD));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
                .register(entries -> entries.add(ModItems.INTELLIGENCE_SWORD));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register(entries -> entries.add(ModItems.CRYSTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
                .register(entries -> entries.add(ModBlocks.DEEPSLATE_CRYSTAL_ORE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
                .register(entries -> entries.add(ModBlocks.CRYSTAL_ORE));;

        IntelligenceManager.register();
        CraftingRestrictions.register();
        ModOreGeneration.register();

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
            if (!world.isClient() && state.getBlock() == net.minecraft.block.Blocks.BOOKSHELF) {
                IntelligenceManager.addIntelligence((ServerPlayerEntity) player, 2);
                player.sendMessage(Text.literal("§a+2 Intelligence! (Bookshelf broken)"), true);
            }
        });
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Get the loot table identifier
            Identifier id = key.getValue();

            // Add to various structure chests
            if (id.equals(Identifier.ofVanilla("chests/ancient_city")) ||
                    id.equals(Identifier.ofVanilla("chests/stronghold_library")) ||
                    id.equals(Identifier.ofVanilla("chests/woodland_mansion")) ||
                    id.equals(Identifier.ofVanilla("chests/end_city_treasure")) ||
                    id.equals(Identifier.ofVanilla("chests/jungle_temple")) ||
                    id.equals(Identifier.ofVanilla("chests/buried_treasure"))) {

                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(ModItems.INTELLIGENCE_SHARD))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 3.0f)))
                        .conditionally(net.minecraft.loot.condition.RandomChanceLootCondition.builder(0.4f)); // 40% chance

                tableBuilder.pool(poolBuilder);
            }

            // Rarer spawn in common chests
        });
    }
}