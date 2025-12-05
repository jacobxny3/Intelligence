package com.intelligence.mixin;

import com.intelligence.IntelligenceManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Unique
    private static final String INTELLIGENCE_KEY = "IntelligencePoints";

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void saveIntelligence(WriteView view, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        int intelligence = IntelligenceManager.getIntelligence(player);
        view.putInt(INTELLIGENCE_KEY, intelligence);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void loadIntelligence(ReadView view, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        int intelligence = view.getInt(INTELLIGENCE_KEY, 0);
        if (intelligence != 0) {
            IntelligenceManager.setIntelligence(player, intelligence);
        }
    }
}