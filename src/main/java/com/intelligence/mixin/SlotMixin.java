package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.IntelligenceManager;
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
                if (!CraftingRestrictions.canCraft(serverPlayer, stack.getItem())) {
                    int required = CraftingRestrictions.getRequirement(stack.getItem());
                    int current = IntelligenceManager.getIntelligence(serverPlayer);

                    player.displayClientMessage(Component.literal("§cInsufficient Intelligence! Requires " + required + " (You have " + current + ")"), true);
                    cir.setReturnValue(false);
                }
            }
            int amount = stack.getCount();
            if (stack.getItem() == net.minecraft.world.item.Items.BOOK) {
                IntelligenceManager.addIntelligence(serverPlayer, amount * 3);
                player.displayClientMessage(Component.literal("§a+3 Intelligence! (Crafted a book)"), true);
            } else if (stack.getItem() == net.minecraft.world.item.Items.BOOKSHELF) {
                IntelligenceManager.addIntelligence(serverPlayer, amount * 5);
                player.displayClientMessage(Component.literal("§a+5 Intelligence! (Crafted a bookshelf)"), true);
            } else if (stack.getItem() == net.minecraft.world.item.Items.ENCHANTED_BOOK) {
                IntelligenceManager.addIntelligence(serverPlayer,amount * 8);
                player.displayClientMessage(Component.literal("§a+8 Intelligence! (Enchanted a book)"), true);
            }
        }
    }
}