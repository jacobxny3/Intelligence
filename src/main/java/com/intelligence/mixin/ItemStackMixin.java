package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.client.IntelligenceModClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void addIntelligenceRequirement(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        if (CraftingRestrictions.hasRequirement(this.getItem())) {
            int required = CraftingRestrictions.getRequirement(this.getItem());
            int current = IntelligenceModClient.getClientIntelligence();

            List<Text> tooltip = cir.getReturnValue();
            tooltip.add(Text.literal("Crafting Info:"));
            if (current >= required) {
                tooltip.add(Text.literal("§7Intelligence Required: §a" + required));
            } else {
                tooltip.add(Text.literal("§7Intelligence Required: §c" + required + " §7(You have §c" + current + "§7)"));
            }
        }
    }
}