package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.IntelligenceManager;
import com.intelligence.ResearchManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void preventTakeIfInsufficientIntelligence(Player player, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot) (Object) this;

        // Only check for CraftingResultSlot
        if (slot instanceof ResultSlot && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = slot.getItem();

            if (!stack.isEmpty() && CraftingRestrictions.hasRequirement(stack.getItem())) {
                // Check if already researched
                if (ResearchManager.isUnlocked(serverPlayer, stack.getItem())) {
                    return; // Allow crafting
                }

                if (!CraftingRestrictions.canCraft(serverPlayer, stack.getItem())) {
                    int required = CraftingRestrictions.getRequirement(stack.getItem());
                    int current = IntelligenceManager.getIntelligence(serverPlayer);

                    player.displayClientMessage(Component.literal("§cInsufficient Intelligence! Requires " + required + " (You have " + current + ") §7[Research at Research Table]"), true);
                    cir.setReturnValue(false);
                }
            }
        }
    }
}