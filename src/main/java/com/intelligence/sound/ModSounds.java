package com.intelligence.sound;

import com.intelligence.Intelligence;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent INTELLIGENCE_SHARD_USE = registerSound("intelligence_shard_use");

    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(Intelligence.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void register() {
        Intelligence.LOGGER.info("Registering sounds for " + Intelligence.MOD_ID);
    }
}