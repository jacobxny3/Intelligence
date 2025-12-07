package com.intelligence.entity;

import com.intelligence.Intelligence;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    static Identifier floatingItemId = Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "floating_item");

    // 2. Create the RegistryKey using the ID
    static ResourceKey<EntityType<?>> floatingItemKey = ResourceKey.create(Registries.ENTITY_TYPE, floatingItemId);
    public static final EntityType<FloatingItemEntity> FLOATING_ITEM = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "floating_item"),
            EntityType.Builder.<FloatingItemEntity>of(FloatingItemEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(6)
                    .updateInterval(20)
                    .build(floatingItemKey)
    );

    public static void register() {
        Intelligence.LOGGER.info("Registering Entities for " + Intelligence.MOD_ID);
    }
}