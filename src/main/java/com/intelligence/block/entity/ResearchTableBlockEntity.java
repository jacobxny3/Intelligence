package com.intelligence.block.entity;

import com.intelligence.Intelligence;
import com.intelligence.entity.FloatingItemEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ResearchTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private FloatingItemEntity floatingItem;
    private ItemStack displayedStack = ItemStack.EMPTY; // tracks what is currently displayed
    private int updateCooldown = 0;

    public ResearchTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESEARCH_TABLE, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.intelligence.research_table");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ResearchTableScreenHandler(syncId, playerInventory, this);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ResearchTableBlockEntity blockEntity) {
        if (world.isClient()) return;

        // Update cooldown to prevent spam
        if (blockEntity.updateCooldown > 0) {
            blockEntity.updateCooldown--;
            return;
        }

        blockEntity.updateFloatingItem();
        blockEntity.updateCooldown = 10; // Check every half second
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
            updateFloatingItem();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack removed = Inventories.removeStack(inventory, slot);
        updateFloatingItem();
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
        updateFloatingItem();
    }

    private void updateFloatingItem() {
        if (world == null || world.isClient()) return;

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
                    && ItemStack.areItemsEqual(displayedStack, slotStack); // fallback if available
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
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.2;
            double z = pos.getZ() + 0.5;

            FloatingItemEntity newEntity = new FloatingItemEntity(world, x, y, z, slotStack.copy());
            // safety: ensure no initial motion and pickup disabled
            newEntity.setVelocity(net.minecraft.util.math.Vec3d.ZERO);
            newEntity.setPickupDelay(32767);

            if (world.spawnEntity(newEntity)) {
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
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.isWithinDistance(player.getEntityPos(), 8.0);
    }

    @Override
    public void clear() {
        inventory.clear();
        updateFloatingItem();
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (floatingItem != null && !floatingItem.isRemoved()) {
            floatingItem.discard();
        }
    }

    @Override
    protected void writeData(WriteView writeView) {
        super.writeData(writeView);
        Inventories.writeData(writeView, inventory);
    }

    @Override
    protected void readData(ReadView readView) {
        super.readData(readView);
        Inventories.readData(readView, inventory);
        // Reset displayedStack so it will re-evaluate on next tick (prevents duplicate respawn on load)
        displayedStack = ItemStack.EMPTY;
    }
}
