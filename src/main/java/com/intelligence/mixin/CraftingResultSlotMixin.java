package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.IntelligenceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;

@Mixin(ResultSlot.class)
public abstract class CraftingResultSlotMixin {

    @Shadow @Final private Player player;

    @Unique
    private static final Random random = new Random();

    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void checkIntelligenceBeforeTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayer serverPlayer) {
            // Check intelligence requirement first
            if (CraftingRestrictions.hasRequirement(stack.getItem())) {
                if (!CraftingRestrictions.canCraft(serverPlayer, stack.getItem())) {
                    int required = CraftingRestrictions.getRequirement(stack.getItem());
                    int current = IntelligenceManager.getIntelligence(serverPlayer);

                    player.displayClientMessage(Component.literal("§cInsufficient Intelligence! Requires " + required + " (You have " + current + ")"), true);
                    ci.cancel();
                    return;
                }
            }

            // Award intelligence for crafting books (only if they can craft it)
            if (stack.getItem() == net.minecraft.world.item.Items.BOOK) {
                IntelligenceManager.addIntelligence(serverPlayer, 3);
                player.displayClientMessage(Component.literal("§a+3 Intelligence! (Crafted a book)"), true);
            } else if (stack.getItem() == net.minecraft.world.item.Items.BOOKSHELF) {
                IntelligenceManager.addIntelligence(serverPlayer, 5);
                player.displayClientMessage(Component.literal("§a+5 Intelligence! (Crafted a bookshelf)"), true);
            } else if (stack.getItem() == net.minecraft.world.item.Items.ENCHANTED_BOOK) {
                IntelligenceManager.addIntelligence(serverPlayer, 8);
                player.displayClientMessage(Component.literal("§a+8 Intelligence! (Enchanted a book)"), true);
            } else {
                // Lose 1-3 intelligence for crafting other items
                int loss = random.nextInt(1, 3); // Random between 1 and 3
                IntelligenceManager.addIntelligence(serverPlayer, -loss);
                player.displayClientMessage(Component.literal("§c-" + loss + " Intelligence! (Crafting drains focus)"), true);
            }
        }
    }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void preventTakeStackIfNoIntelligence(int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player instanceof ServerPlayer serverPlayer) {
            ResultSlot slot = (ResultSlot) (Object) this;
            ItemStack stack = slot.getItem();

            if (!stack.isEmpty() && CraftingRestrictions.hasRequirement(stack.getItem())) {
                if (!CraftingRestrictions.canCraft(serverPlayer, stack.getItem())) {
                    int required = CraftingRestrictions.getRequirement(stack.getItem());
                    int current = IntelligenceManager.getIntelligence(serverPlayer);

                    this.player.displayClientMessage(Component.literal("§cInsufficient Intelligence! Requires " + required + " (You have " + current + ")"), true);
                    cir.setReturnValue(ItemStack.EMPTY);
                }
            }
        }
    }
}