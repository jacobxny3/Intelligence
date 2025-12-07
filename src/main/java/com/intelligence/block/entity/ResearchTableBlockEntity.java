package com.intelligence.block.entity;

import com.intelligence.Intelligence;
import com.intelligence.entity.FloatingItemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ResearchTableBlockEntity extends BlockEntity implements MenuProvider, Container {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    private FloatingItemEntity floatingItem;
    private ItemStack displayedStack = ItemStack.EMPTY; // tracks what is currently displayed
    private int updateCooldown = 0;

    public ResearchTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESEARCH_TABLE, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.intelligence.research_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new ResearchTableScreenHandler(syncId, playerInventory, this);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, ResearchTableBlockEntity blockEntity) {
        if (world.isClientSide()) return;

        // Update cooldown to prevent spam
        if (blockEntity.updateCooldown > 0) {
            blockEntity.updateCooldown--;
            return;
        }

        blockEntity.updateFloatingItem();
        blockEntity.updateCooldown = 10; // Check every half second
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(inventory, slot, amount);
        if (!result.isEmpty()) {
            setChanged();
            updateFloatingItem();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = ContainerHelper.takeItem(inventory, slot);
        updateFloatingItem();
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
        updateFloatingItem();
    }

    private void updateFloatingItem() {
        if (level == null || level.isClientSide()) return;

        // Clean up if floatingItem was removed elsewhere
        if (floatingItem != null && floatingItem.isRemoved()) {
            floatingItem = null;
            displayedStack = ItemStack.EMPTY;
        }

        ItemStack slotStack = inventory.get(0);

        // If displayed stack equals the slot stack (type + NBT), no change needed.
        boolean sameDisplayed;
        if (displayedStack == null || displayedStack.isEmpty()) {
            sameDisplayed = slotStack.isEmpty();
        } else {
            // Compare item and NBT, but ignore count
            sameDisplayed = !slotStack.isEmpty()
                    && slotStack.getItem() == displayedStack.getItem()
                    && ItemStack.isSameItem(displayedStack, slotStack); // fallback if available
            // If the helper above isn't available in your mappings, use a safe manual comparison:
            // sameDisplayed = !slotStack.isEmpty()
            //     && slotStack.getItem() == displayedStack.getItem()
            //     && net.minecraft.item.ItemStack.areNbtEqual(slotStack, displayedStack);
        }

        if (sameDisplayed && floatingItem != null && !floatingItem.isRemoved()) {
            // nothing to do
            return;
        }

        // If displayed is different, remove old floating item (if present)
        if (floatingItem != null && !floatingItem.isRemoved()) {
            floatingItem.discard();
            floatingItem = null;
        }

        // If the slot has an item, spawn one floating copy
        if (!slotStack.isEmpty()) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.2;
            double z = worldPosition.getZ() + 0.5;

            FloatingItemEntity newEntity = new FloatingItemEntity(level, x, y, z, slotStack.copy());
            // safety: ensure no initial motion and pickup disabled
            newEntity.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
            newEntity.setPickUpDelay(32767);

            if (level.addFreshEntity(newEntity)) {
                floatingItem = newEntity;
                displayedStack = slotStack.copy(); // remember what we are showing
            } else {
                // spawn failed — make sure displayedStack is reset so we'll retry later
                displayedStack = ItemStack.EMPTY;
            }
        } else {
            // slot empty -> nothing should be displayed
            displayedStack = ItemStack.EMPTY;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return worldPosition.closerToCenterThan(player.position(), 8.0);
    }

    @Override
    public void clearContent() {
        inventory.clear();
        updateFloatingItem();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (floatingItem != null && !floatingItem.isRemoved()) {
            floatingItem.discard();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput writeView) {
        super.saveAdditional(writeView);
        ContainerHelper.saveAllItems(writeView, inventory);
    }

    @Override
    protected void loadAdditional(ValueInput readView) {
        super.loadAdditional(readView);
        ContainerHelper.loadAllItems(readView, inventory);
        // Reset displayedStack so it will re-evaluate on next tick (prevents duplicate respawn on load)
        displayedStack = ItemStack.EMPTY;
    }
}
