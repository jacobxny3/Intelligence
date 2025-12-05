package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.IntelligenceManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin {

    @Shadow @Final private PlayerEntity player;

    @Unique
    private static final Random random = new Random();

    @Inject(method = "onTakeItem", at = @At("HEAD"), cancellable = true)
    private void checkIntelligenceBeforeTake(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            // Check intelligence requirement first
            if (CraftingRestrictions.hasRequirement(stack.getItem())) {
                if (!CraftingRestrictions.canCraft(serverPlayer, stack.getItem())) {
                    int required = CraftingRestrictions.getRequirement(stack.getItem());
                    int current = IntelligenceManager.getIntelligence(serverPlayer);

                    player.sendMessage(Text.literal("§cInsufficient Intelligence! Requires " + required + " (You have " + current + ")"), true);
                    ci.cancel();
                    return;
                }
            }

            // Award intelligence for crafting books (only if they can craft it)
            if (stack.getItem() == net.minecraft.item.Items.BOOK) {
                IntelligenceManager.addIntelligence(serverPlayer, 3);
                player.sendMessage(Text.literal("§a+3 Intelligence! (Crafted a book)"), true);
            } else if (stack.getItem() == net.minecraft.item.Items.BOOKSHELF) {
                IntelligenceManager.addIntelligence(serverPlayer, 5);
                player.sendMessage(Text.literal("§a+5 Intelligence! (Crafted a bookshelf)"), true);
            } else if (stack.getItem() == net.minecraft.item.Items.ENCHANTED_BOOK) {
                IntelligenceManager.addIntelligence(serverPlayer, 8);
                player.sendMessage(Text.literal("§a+8 Intelligence! (Enchanted a book)"), true);
            } else {
                // Lose 1-3 intelligence for crafting other items
                int loss = random.nextInt(1, 3); // Random between 1 and 3
                IntelligenceManager.addIntelligence(serverPlayer, -loss);
                player.sendMessage(Text.literal("§c-" + loss + " Intelligence! (Crafting drains focus)"), true);
            }
        }
    }

    @Inject(method = "takeStack", at = @At("HEAD"), cancellable = true)
    private void preventTakeStackIfNoIntelligence(int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player instanceof ServerPlayerEntity serverPlayer) {
            CraftingResultSlot slot = (CraftingResultSlot) (Object) this;
            ItemStack stack = slot.getStack();

            if (!stack.isEmpty() && CraftingRestrictions.hasRequirement(stack.getItem())) {
                if (!CraftingRestrictions.canCraft(serverPlayer, stack.getItem())) {
                    int required = CraftingRestrictions.getRequirement(stack.getItem());
                    int current = IntelligenceManager.getIntelligence(serverPlayer);

                    this.player.sendMessage(Text.literal("§cInsufficient Intelligence! Requires " + required + " (You have " + current + ")"), true);
                    cir.setReturnValue(ItemStack.EMPTY);
                }
            }
        }
    }
}