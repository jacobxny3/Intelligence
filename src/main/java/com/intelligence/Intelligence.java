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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Intelligence implements ModInitializer {
    public static final String MOD_ID = "intelligence";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Intelligence Mod Initialized!");

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(entries -> entries.accept(ModBlocks.RESEARCH_TABLE.asItem()));


        // Register networking payloads first
        IntelligenceNetworking.registerPayloads();

        // Register blocks and block entities
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModScreenHandlers.register();
        Items.registerBlock(ModBlocks.RESEARCH_TABLE);
        Items.registerBlock(ModBlocks.CRYSTAL_ORE);
        Items.registerBlock(ModBlocks.DEEPSLATE_CRYSTAL_ORE);
        ModItems.register();
        ModSounds.register();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> entries.accept(ModItems.INTELLIGENCE_SHARD));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
                .register(entries -> entries.accept(ModItems.INTELLIGENCE_SWORD));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> entries.accept(ModItems.CRYSTAL));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register(entries -> entries.accept(ModBlocks.DEEPSLATE_CRYSTAL_ORE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS)
                .register(entries -> entries.accept(ModBlocks.CRYSTAL_ORE));;

        IntelligenceManager.register();
        CraftingRestrictions.register();
        ModOreGeneration.register();

        // Register research packet handler
        PayloadTypeRegistry.playC2S().register(ResearchPayload.ID, ResearchPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ResearchPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                if (player.containerMenu instanceof ResearchTableScreenHandler handler) {
                    handler.tryResearch(player);
                }
            });
        });

        // Give players initial intelligence on join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (IntelligenceManager.getIntelligence(player) == 0) {
                IntelligenceManager.setIntelligence(player, 10); // Starting intelligence
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
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // Get the loot table identifier
            Identifier id = key.identifier();

            // Add to various structure chests
            if (id.equals(Identifier.withDefaultNamespace("chests/ancient_city")) ||
                    id.equals(Identifier.withDefaultNamespace("chests/stronghold_library")) ||
                    id.equals(Identifier.withDefaultNamespace("chests/woodland_mansion")) ||
                    id.equals(Identifier.withDefaultNamespace("chests/end_city_treasure")) ||
                    id.equals(Identifier.withDefaultNamespace("chests/jungle_temple")) ||
                    id.equals(Identifier.withDefaultNamespace("chests/buried_treasure"))) {

                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.INTELLIGENCE_SHARD))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 3.0f)))
                        .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.4f)); // 40% chance

                tableBuilder.withPool(poolBuilder);
            }

            // Rarer spawn in common chests
        });
    }
}