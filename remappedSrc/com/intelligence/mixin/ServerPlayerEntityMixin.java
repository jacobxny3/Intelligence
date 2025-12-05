package com.intelligence.mixin;

import com.intelligence.IntelligenceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin {

    @Unique
    private static final String INTELLIGENCE_KEY = "IntelligencePoints";

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveIntelligence(ValueOutput view, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        int intelligence = IntelligenceManager.getIntelligence(player);
        view.putInt(INTELLIGENCE_KEY, intelligence);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadIntelligence(ValueInput view, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        int intelligence = view.getIntOr(INTELLIGENCE_KEY, 0);
        if (intelligence != 0) {
            IntelligenceManager.setIntelligence(player, intelligence);
        }
    }
}