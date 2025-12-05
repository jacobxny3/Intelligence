package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.client.IntelligenceModClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class HandledScreenMixin {

    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void drawIntelligenceIndicator(GuiGraphics context, Slot slot, CallbackInfo ci) {
        if (slot instanceof ResultSlot) {
            ItemStack stack = slot.getItem();

            if (!stack.isEmpty() && CraftingRestrictions.hasRequirement(stack.getItem())) {
                int required = CraftingRestrictions.getRequirement(stack.getItem());
                int current = IntelligenceModClient.getClientIntelligence();

                if (current < required) {
                    // Draw a red overlay on the slot
                    int x = slot.x;
                    int y = slot.y;

                    // Semi-transparent red overlay
                    context.fill(x, y, x + 16, y + 16, 0x88FF0000);

                    // Draw an X mark
                    context.fill(x + 2, y + 2, x + 14, y + 3, 0xFFFF0000);
                    context.fill(x + 2, y + 13, x + 14, y + 14, 0xFFFF0000);
                    context.fill(x + 2, y + 2, x + 3, y + 14, 0xFFFF0000);
                    context.fill(x + 13, y + 2, x + 14, y + 14, 0xFFFF0000);

                    // Draw diagonal lines for X
                    for (int i = 0; i < 12; i++) {
                        context.fill(x + 2 + i, y + 2 + i, x + 3 + i, y + 3 + i, 0xFFFF0000);
                        context.fill(x + 13 - i, y + 2 + i, x + 14 - i, y + 3 + i, 0xFFFF0000);
                    }
                } else {
                    // Draw a green checkmark overlay
                    int x = slot.x;
                    int y = slot.y;

                    // Semi-transparent green overlay (subtle)
                    context.fill(x, y, x + 16, y + 16, 0x2200FF00);
                }
            }
        }
    }
}