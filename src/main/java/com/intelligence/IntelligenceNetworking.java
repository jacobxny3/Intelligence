package com.intelligence;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IntelligenceNetworking {
    public static final Identifier INTELLIGENCE_UPDATE_ID = Identifier.of(Intelligence.MOD_ID, "intelligence_update");
    public static final Identifier RESEARCH_SYNC_ID = Identifier.of(Intelligence.MOD_ID, "research_sync");

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(IntelligenceUpdatePayload.ID, IntelligenceUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ResearchSyncPayload.ID, ResearchSyncPayload.CODEC);
    }

    public static void sendIntelligenceUpdate(ServerPlayerEntity player, int intelligence) {
        ServerPlayNetworking.send(player, new IntelligenceUpdatePayload(intelligence));
    }

    public static void sendResearchSync(ServerPlayerEntity player, Set<Item> unlockedItems) {
        List<String> itemIds = new ArrayList<>();
        for (Item item : unlockedItems) {
            Identifier id = Registries.ITEM.getId(item);
            itemIds.add(id.toString());
        }
        ServerPlayNetworking.send(player, new ResearchSyncPayload(itemIds));
    }

    public record IntelligenceUpdatePayload(int intelligence) implements CustomPayload {
        public static final CustomPayload.Id<IntelligenceUpdatePayload> ID = new CustomPayload.Id<>(INTELLIGENCE_UPDATE_ID);
        public static final PacketCodec<RegistryByteBuf, IntelligenceUpdatePayload> CODEC =
                PacketCodec.tuple(
                        PacketCodecs.VAR_INT, IntelligenceUpdatePayload::intelligence,
                        IntelligenceUpdatePayload::new
                );

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record ResearchSyncPayload(List<String> unlockedItemIds) implements CustomPayload {
        public static final CustomPayload.Id<ResearchSyncPayload> ID = new CustomPayload.Id<>(RESEARCH_SYNC_ID);
        public static final PacketCodec<RegistryByteBuf, ResearchSyncPayload> CODEC =
                PacketCodec.tuple(
                        PacketCodecs.STRING.collect(PacketCodecs.toList()), ResearchSyncPayload::unlockedItemIds,
                        ResearchSyncPayload::new
                );

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}