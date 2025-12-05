package com.intelligence;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class IntelligenceNetworking {
    public static final Identifier INTELLIGENCE_UPDATE_ID = Identifier.of(Intelligence.MOD_ID, "intelligence_update");

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(IntelligenceUpdatePayload.ID, IntelligenceUpdatePayload.CODEC);
    }

    public static void sendIntelligenceUpdate(ServerPlayerEntity player, int intelligence) {
        ServerPlayNetworking.send(player, new IntelligenceUpdatePayload(intelligence));
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
}