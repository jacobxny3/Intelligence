package com.intelligence.entity;

import com.intelligence.Intelligence;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {

    static Identifier floatingItemId = Identifier.of(Intelligence.MOD_ID, "floating_item");

    // 2. Create the RegistryKey using the ID
    static RegistryKey<EntityType<?>> floatingItemKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, floatingItemId);
    public static final EntityType<FloatingItemEntity> FLOATING_ITEM = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Intelligence.MOD_ID, "floating_item"),
            EntityType.Builder.<FloatingItemEntity>create(FloatingItemEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(6)
                    .trackingTickInterval(20)
                    .build(floatingItemKey)
    );

    public static void register() {
        Intelligence.LOGGER.info("Registering Entities for " + Intelligence.MOD_ID);
    }
}