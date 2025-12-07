package com.intelligence.block.entity;

import com.intelligence.CraftingRestrictions;
import com.intelligence.IntelligenceManager;
import com.intelligence.ResearchManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ResearchTableScreenHandler extends AbstractContainerMenu {

    private final Container inventory;

    public ResearchTableScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(1));
    }

    public ResearchTableScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
        super(ModScreenHandlers.RESEARCH_TABLE, syncId);
        this.inventory = inventory;

        checkContainerSize(inventory, 1);
        inventory.startOpen(playerInventory.player);

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
            public boolean mayPlace(ItemStack stack) {
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

    public boolean tryResearch(Player player) {
        ItemStack stack = inventory.getItem(0);
        if (stack.isEmpty() || !(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        if (!CraftingRestrictions.hasRequirement(stack.getItem())) {
            player.displayClientMessage(Component.literal("§cThis item doesn't require intelligence!"), true);
            return false;
        }

        if (ResearchManager.isUnlocked(serverPlayer, stack.getItem())) {
            player.displayClientMessage(Component.literal("§cYou've already researched this item!"), true);
            return false;
        }

        int cost = ResearchManager.getResearchCost(stack.getItem());
        int current = IntelligenceManager.getIntelligence(serverPlayer);

        if (current < cost) {
            player.displayClientMessage(Component.literal("§cInsufficient Intelligence! Requires " + cost + " (You have " + current + ")"), true);
            return false;
        }

        // Unlock the item
        ResearchManager.unlock(serverPlayer, stack.getItem());
        IntelligenceManager.addIntelligence(serverPlayer, -cost);
        inventory.removeItemNoUpdate(0);

        String itemName = stack.getHoverName().getString();
        player.displayClientMessage(Component.literal("§aResearched " + itemName + "! Cost: " + cost + " Intelligence"), false);
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index == 0) {
                // From research slot to inventory
                if (!this.moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From inventory to research slot
                if (CraftingRestrictions.hasRequirement(slotStack.getItem())) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.inventory.stopOpen(player);
    }
}