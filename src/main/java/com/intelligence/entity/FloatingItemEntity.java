package com.intelligence.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FloatingItemEntity extends ItemEntity {
    private float rotation = 0.0f;
    private float rotationSpeed = 2.0f;
    private int bobAge = 0;

    // Anchor position (the exact spot the item should float above)
    private double anchorX = Double.NaN;
    private double anchorY = Double.NaN;
    private double anchorZ = Double.NaN;

    public FloatingItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.noClip = true;

        // anchor defaults to current position (will be set after spawn / deserialization)
        this.anchorX = this.getX();
        this.anchorZ = this.getZ();

        this.setPickupDelay(32767);
    }

    public FloatingItemEntity(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
        this.setNoGravity(true);
        this.noClip = true;

        this.anchorX = x;
        this.anchorY = y;
        this.anchorZ = z;

        // Prevent immediate pickup and drifting
        this.setPickupDelay(32767);
        this.setVelocity(Vec3d.ZERO);
        this.setPosition(x, y, z);
    }
    public void tick() {
        // Server-side age/despawn
        if (!this.getWorld().isClient) {
            this.age++;
            if (this.age >= 6000) { // 5 minutes
                this.discard();
                return;
            }
        }

        // Smooth rotation
        rotation += rotationSpeed;
        if (rotation >= 360f) rotation -= 360f;
        this.setYaw(rotation);

        // Smooth bobbing using age and partial tick interpolation
        double time = this.age + (this.getWorld().isClient ? this.getWorld().getTime() % 1 : 0);
        double bobOffset = Math.sin(time * 0.1) * 0.06; // amplitude 0.06, speed 0.1

        // Ensure anchor fallback

        // Keep position locked to anchor + bob
        this.setPosition(anchorX, anchorY + bobOffset, anchorZ);

        // Zero velocity (prevent drifting)
        this.setVelocity(Vec3d.ZERO);

        // Prevent pickup
        this.setPickupDelay(32767);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("Rotation", this.rotation);
        nbt.putFloat("RotationSpeed", this.rotationSpeed);
        nbt.putInt("BobAge", this.bobAge);

        // Save anchors so after reload it stays in the same place
        nbt.putDouble("AnchorX", this.anchorX);
        nbt.putDouble("AnchorZ", this.anchorZ);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Rotation")) this.rotation = nbt.getFloat("Rotation");
        if (nbt.contains("RotationSpeed")) this.rotationSpeed = nbt.getFloat("RotationSpeed");
        if (nbt.contains("BobAge")) this.bobAge = nbt.getInt("BobAge");

        if (nbt.contains("AnchorX")) this.anchorX = nbt.getDouble("AnchorX");
        if (nbt.contains("AnchorZ")) this.anchorZ = nbt.getDouble("AnchorZ");
        // if anchors were missing, we'll fallback to current position in tick()
    }
}
