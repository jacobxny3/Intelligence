package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.ResearchManager;
import com.intelligence.client.IntelligenceModClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin {

    @Unique
    public abstract Item getItem();


    @Unique
    Minecraft client = Minecraft.getInstance();



    @Inject(method = "renderSlot", at = @At("TAIL"))
    private void drawIntelligenceIndicator(GuiGraphics guiGraphics, Slot slot, int i, int j, CallbackInfo ci) {
        if (slot instanceof ResultSlot) {
            ItemStack stack = slot.getItem();

            boolean isUnlocked = client.player != null &&
                    ResearchManager.isUnlockedClient(client.player.getUUID(), stack.getItem());


            if (!stack.isEmpty() && CraftingRestrictions.hasRequirement(stack.getItem())) {
                int required = CraftingRestrictions.getRequirement(stack.getItem());
                int current = IntelligenceModClient.getClientIntelligence();

                if (current < required && !isUnlocked) {
                    // Draw a red overlay on the slot
                    int x = slot.x;
                    int y = slot.y;

                    // Semi-transparent red overlay
                    guiGraphics.fill(x, y, x + 16, y + 16, 0x88FF0000);

                    // Draw an X mark
                    guiGraphics.fill(x + 2, y + 2, x + 14, y + 3, 0xFFFF0000);
                    guiGraphics.fill(x + 2, y + 13, x + 14, y + 14, 0xFFFF0000);
                    guiGraphics.fill(x + 2, y + 2, x + 3, y + 14, 0xFFFF0000);
                    guiGraphics.fill(x + 13, y + 2, x + 14, y + 14, 0xFFFF0000);

                    // Draw diagonal lines for X
                    for (int a = 0; a < 12; a++) {
                        guiGraphics.fill(x + 2 + a, y + 2 + a, x + 3 + a, y + 3 + a, 0xFFFF0000);
                        guiGraphics.fill(x + 13 - a, y + 2 + a, x + 14 - a, y + 3 + a, 0xFFFF0000);
                    }
                } else {
                    // Draw a green checkmark overlay
                    int x = slot.x;
                    int y = slot.y;

                    // Semi-transparent green overlay (subtle)
                    guiGraphics.fill(x, y, x + 16, y + 16, 0x2200FF00);
                }
            }
        }
    }
}