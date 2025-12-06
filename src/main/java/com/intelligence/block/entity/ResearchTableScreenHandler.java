package com.intelligence.block.entity;

import com.intelligence.CraftingRestrictions;
import com.intelligence.IntelligenceManager;
import com.intelligence.ResearchManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ResearchTableScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    public ResearchTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public ResearchTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.RESEARCH_TABLE, syncId);
        this.inventory = inventory;

        checkSize(inventory, 1);
        inventory.onOpen(playerInventory.player);

        // --------------------------
        //  RESEARCH SLOT (Single)
        // --------------------------
        //
        // This matches the exact center of the small square in the GUI image
        //
        int researchSlotX = 121;
        int researchSlotY = 63;

        this.addSlot(new Slot(inventory, 0, researchSlotX, researchSlotY) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return CraftingRestrictions.hasRequirement(stack.getItem());
            }
        });


        // --------------------------
        //  PLAYER INVENTORY (27 slots)
        // --------------------------
        // Starting position matches the lower grid of your GUI
        //
        int invStartX = 23;
        int invStartY = 122;
        int slotSize = 25;
        int gap = 2;  // adjust to match your texture
        int spacing = slotSize + gap;
        int ySpacing = (slotSize + gap) - 1;

// Player inventory (3 rows, 9 columns)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9,
                        12 + j * spacing,
                        127 + i * ySpacing));
            }
        }

// Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i,
                    12 + i * spacing,
                    213));
        }


        // --------------------------
        //  HOTBAR (9 slots)
        // --------------------------
    }

    public boolean tryResearch(PlayerEntity player) {
        ItemStack stack = inventory.getStack(0);
        if (stack.isEmpty() || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return false;
        }

        if (!CraftingRestrictions.hasRequirement(stack.getItem())) {
            player.sendMessage(Text.literal("§cThis item doesn't require intelligence!"), true);
            return false;
        }

        if (ResearchManager.isUnlocked(serverPlayer, stack.getItem())) {
            player.sendMessage(Text.literal("§cYou've already researched this item!"), true);
            return false;
        }

        int cost = ResearchManager.getResearchCost(stack.getItem());
        int current = IntelligenceManager.getIntelligence(serverPlayer);

        if (current < cost) {
            player.sendMessage(Text.literal("§cInsufficient Intelligence! Requires " + cost + " (You have " + current + ")"), true);
            return false;
        }

        // Unlock the item
        ResearchManager.unlock(serverPlayer, stack.getItem());
        IntelligenceManager.addIntelligence(serverPlayer, -cost);
        inventory.removeStack(0);

        String itemName = stack.getName().getString();
        player.sendMessage(Text.literal("§aResearched " + itemName + "! Cost: " + cost + " Intelligence"), false);
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();

            if (index == 0) {
                // From research slot to inventory
                if (!this.insertItem(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From inventory to research slot
                if (CraftingRestrictions.hasRequirement(slotStack.getItem())) {
                    if (!this.insertItem(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
}