package com.intelligence;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IntelligenceNetworking {
    public static final Identifier INTELLIGENCE_UPDATE_ID = Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "intelligence_update");
    public static final Identifier RESEARCH_SYNC_ID = Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "research_sync");

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(IntelligenceUpdatePayload.ID, IntelligenceUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ResearchSyncPayload.ID, ResearchSyncPayload.CODEC);
    }

    public static void sendIntelligenceUpdate(ServerPlayer player, int intelligence) {
        ServerPlayNetworking.send(player, new IntelligenceUpdatePayload(intelligence));
    }

    public static void sendResearchSync(ServerPlayer player, Set<Item> unlockedItems) {
        List<String> itemIds = new ArrayList<>();
        for (Item item : unlockedItems) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            itemIds.add(id.toString());
        }
        ServerPlayNetworking.send(player, new ResearchSyncPayload(itemIds));
    }

    public record IntelligenceUpdatePayload(int intelligence) implements CustomPacketPayload {
        public static final Type<IntelligenceUpdatePayload> ID = new Type<>(INTELLIGENCE_UPDATE_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, IntelligenceUpdatePayload> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, IntelligenceUpdatePayload::intelligence,
                        IntelligenceUpdatePayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public record ResearchSyncPayload(List<String> unlockedItemIds) implements CustomPacketPayload {
        public static final Type<ResearchSyncPayload> ID = new Type<>(RESEARCH_SYNC_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, ResearchSyncPayload> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ResearchSyncPayload::unlockedItemIds,
                        ResearchSyncPayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }
}