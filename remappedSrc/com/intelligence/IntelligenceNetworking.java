package com.intelligence;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class IntelligenceNetworking {
    public static final ResourceLocation INTELLIGENCE_UPDATE_ID = ResourceLocation.fromNamespaceAndPath(Intelligence.MOD_ID, "intelligence_update");

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(IntelligenceUpdatePayload.ID, IntelligenceUpdatePayload.CODEC);
    }

    public static void sendIntelligenceUpdate(ServerPlayer player, int intelligence) {
        ServerPlayNetworking.send(player, new IntelligenceUpdatePayload(intelligence));
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
}