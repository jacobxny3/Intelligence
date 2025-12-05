package com.intelligence.mixin;

import com.intelligence.IntelligenceManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Unique
    private static final String INTELLIGENCE_KEY = "IntelligencePoints";

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void saveIntelligence(NbtCompound nbt, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        int intelligence = IntelligenceManager.getIntelligence(player);
        nbt.putInt(INTELLIGENCE_KEY, intelligence);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void loadIntelligence(NbtCompound nbt, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (nbt.contains(INTELLIGENCE_KEY)) {
            int intelligence = nbt.getInt(INTELLIGENCE_KEY);
            IntelligenceManager.setIntelligence(player, intelligence);
        }
    }
}