package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.IntelligenceManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "canTakeItems", at = @At("HEAD"), cancellable = true)
    private void preventTakeIfInsufficientIntelligence(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot) (Object) this;

        // Only check for CraftingResultSlot
        if (slot instanceof CraftingResultSlot && player instanceof ServerPlayerEntity serverPlayer) {
            ItemStack stack = slot.getStack();

            if (!stack.isEmpty() && CraftingRestrictions.hasRequirement(stack.getItem())) {
                if (!CraftingRestrictions.canCraft(serverPlayer, stack.getItem())) {
                    int required = CraftingRestrictions.getRequirement(stack.getItem());
                    int current = IntelligenceManager.getIntelligence(serverPlayer);

                    player.sendMessage(Text.literal("§cInsufficient Intelligence! Requires " + required + " (You have " + current + ")"), true);
                    cir.setReturnValue(false);
                }
            }
            int amount = stack.getCount();
            if (stack.getItem() == net.minecraft.item.Items.BOOK) {
                IntelligenceManager.addIntelligence(serverPlayer, amount * 3);
                player.sendMessage(Text.literal("§a+3 Intelligence! (Crafted a book)"), true);
            } else if (stack.getItem() == net.minecraft.item.Items.BOOKSHELF) {
                IntelligenceManager.addIntelligence(serverPlayer, amount * 5);
                player.sendMessage(Text.literal("§a+5 Intelligence! (Crafted a bookshelf)"), true);
            } else if (stack.getItem() == net.minecraft.item.Items.ENCHANTED_BOOK) {
                IntelligenceManager.addIntelligence(serverPlayer,amount * 8);
                player.sendMessage(Text.literal("§a+8 Intelligence! (Enchanted a book)"), true);
            }
        }
    }
}