package com.intelligence.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class FloatingItemEntity extends ItemEntity {
    private float rotation = 0.0f;
    private float rotationSpeed = 2.0f;
    private int bobAge = 0;

    // Anchor position (the exact spot the item should float above)
    private double anchorX = Double.NaN;
    private double anchorY = Double.NaN;
    private double anchorZ = Double.NaN;

    public FloatingItemEntity(EntityType<? extends ItemEntity> entityType, Level world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.noPhysics = true;

        // anchor defaults to current position (will be set after spawn / deserialization)
        this.anchorX = this.getX();
        this.anchorZ = this.getZ();

        this.setPickUpDelay(32767);
    }

    public FloatingItemEntity(Level world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
        this.setNoGravity(true);
        this.noPhysics = true;

        this.anchorX = x;
        this.anchorY = y;
        this.anchorZ = z;

        // Prevent immediate pickup and drifting
        this.setPickUpDelay(32767);
        this.setDeltaMovement(Vec3.ZERO);
        this.setPos(x, y, z);
    }
    public void tick() {
        // Server-side age/despawn
        if (!this.level().isClientSide()) {
            this.tickCount++;
            if (this.tickCount >= 6000) { // 5 minutes
                this.discard();
                return;
            }
        }

        // Smooth rotation
        rotation += rotationSpeed;
        if (rotation >= 360f) rotation -= 360f;
        this.setYRot(rotation);

        // Smooth bobbing using age and partial tick interpolation
        double time = this.tickCount + (this.level().isClientSide() ? this.level().getGameTime() % 1 : 0);
        double bobOffset = Math.sin(time * 0.1) * 0.06; // amplitude 0.06, speed 0.1

        // Ensure anchor fallback

        // Keep position locked to anchor + bob
        this.setPos(anchorX, anchorY + bobOffset, anchorZ);

        // Zero velocity (prevent drifting)
        this.setDeltaMovement(Vec3.ZERO);

        // Prevent pickup
        this.setPickUpDelay(32767);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput writeView) {
        super.addAdditionalSaveData(writeView);
        writeView.putFloat("Rotation", this.rotation);
        writeView.putFloat("RotationSpeed", this.rotationSpeed);
        writeView.putInt("BobAge", this.bobAge);

        // Save anchors so after reload it stays in the same place
        writeView.putDouble("AnchorX", this.anchorX);
        writeView.putDouble("AnchorZ", this.anchorZ);
    }

    @Override
    public void readAdditionalSaveData(ValueInput readView) {
        super.readAdditionalSaveData(readView);



        this.rotation = readView.getFloatOr("Rotation", 0);
        this.rotationSpeed = readView.getFloatOr("RotationSpeed", 0);
        this.bobAge = readView.getIntOr("BobAge", 0);

        this.anchorX = readView.getDoubleOr("AnchorX", 0);
        this.anchorZ = readView.getDoubleOr("AnchorZ",0);
        // if anchors were missing, we'll fallback to current position in tick()
    }
}
