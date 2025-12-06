package com.intelligence.entity;

import com.intelligence.Intelligence;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<FloatingItemEntity> FLOATING_ITEM = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Intelligence.MOD_ID, "floating_item"),
            EntityType.Builder.<FloatingItemEntity>create(FloatingItemEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(20)
                    .build()
    );

    public static void register() {
        Intelligence.LOGGER.info("Registering Entities for " + Intelligence.MOD_ID);
    }
}