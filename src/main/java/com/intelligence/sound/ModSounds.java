package com.intelligence.sound;

import com.intelligence.Intelligence;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent INTELLIGENCE_SHARD_USE = registerSound("intelligence_shard_use");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    public static void register() {
        Intelligence.LOGGER.info("Registering sounds for " + Intelligence.MOD_ID);
    }
}