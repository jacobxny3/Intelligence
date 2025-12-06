package com.intelligence;

import com.intelligence.Intelligence;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ResearchPayload() implements CustomPayload {
    public static final CustomPayload.Id<ResearchPayload> ID = new CustomPayload.Id<>(Identifier.of(Intelligence.MOD_ID, "research"));
    public static final PacketCodec<RegistryByteBuf, ResearchPayload> CODEC =
            PacketCodec.unit(new ResearchPayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}