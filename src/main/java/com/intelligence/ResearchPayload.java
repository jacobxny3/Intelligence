package com.intelligence;

import com.intelligence.Intelligence;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ResearchPayload() implements CustomPacketPayload {
    public static final Type<ResearchPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "research"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchPayload> CODEC =
            StreamCodec.unit(new ResearchPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}