package com.intelligence.mixin;

import com.intelligence.CraftingRestrictions;
import com.intelligence.client.IntelligenceModClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void addIntelligenceRequirement(Item.TooltipContext context, Player player, TooltipFlag type, CallbackInfoReturnable<List<Component>> cir) {
        if (CraftingRestrictions.hasRequirement(this.getItem())) {
            int required = CraftingRestrictions.getRequirement(this.getItem());
            int current = IntelligenceModClient.getClientIntelligence();

            List<Component> tooltip = cir.getReturnValue();
            tooltip.add(Component.literal("Crafting Info:"));
            if (current >= required) {
                tooltip.add(Component.literal("§7Intelligence Required: §a" + required));
            } else {
                tooltip.add(Component.literal("§7Intelligence Required: §c" + required + " §7(You have §c" + current + "§7)"));
            }
        }
    }
}