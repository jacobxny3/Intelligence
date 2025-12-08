package com.intelligence.item;

import com.intelligence.IntelligenceManager;
import com.intelligence.sound.ModSounds;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.Vec3;
import java.util.Random;

public class IntelligenceSwordItem extends Item {
    private static final Random RANDOM = new Random();
    private final ToolMaterial material;

    public IntelligenceSwordItem(ToolMaterial toolMaterial, Properties settings) {
        super(settings.component(
                net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(toolMaterial, 3, -2.4f)
        ));
        this.material = toolMaterial;
    }

    private static ItemAttributeModifiers createAttributeModifiers(ToolMaterial material, int baseAttackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_ID,
                                (double)baseAttackDamage + material.attackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                Item.BASE_ATTACK_SPEED_ID,
                                (double)attackSpeed,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Check if the target is a player
        if (!attacker.level().isClientSide() && target instanceof ServerPlayer targetPlayer) {
            // Remove 3-7 intelligence
            int intelligenceLost = 3 + RANDOM.nextInt(5); // 3 + (0-4) = 3-7

            int currentIntelligence = IntelligenceManager.getIntelligence(targetPlayer);
            int newIntelligence = Math.max(0, currentIntelligence - intelligenceLost); // Don't go below 0

            IntelligenceManager.setIntelligence(targetPlayer, newIntelligence);

            // Send message to victim
            targetPlayer.displayClientMessage(
                    Component.literal("§c-§4" + intelligenceLost + " §cIntelligence! §7(Intelligence Sword)"),
                    true
            );

            // Send message to attacker if they're a player
            if (attacker instanceof ServerPlayer attackerPlayer) {
                attackerPlayer.displayClientMessage(
                        Component.literal("§6Drained §e" + intelligenceLost + " §6Intelligence from " + targetPlayer.getName().getString()),
                        true
                );
            }

            // Play drain sound
            attacker.level().playSound(
                    null,
                    target.getX(), target.getY(), target.getZ(),
                    ModSounds.INTELLIGENCE_SHARD_USE,
                    SoundSource.PLAYERS,
                    0.7f, 0.8f
            );
        }

        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, net.minecraft.world.entity.Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);


        // Only spawn particles when held in main hand or off hand
        // Reduced frequency: only 20% of ticks
        if (entity instanceof LivingEntity living &&
                (slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND || slot == net.minecraft.world.entity.EquipmentSlot.OFFHAND) &&
                RANDOM.nextFloat() < 0.2f) {

            // Spawn particles around the sword tip
            Vec3 pos = living.position();
            Vec3 look = living.getViewVector(1.0f);

            // Calculate right vector (perpendicular to look direction)
            Vec3 right = new Vec3(-look.z, 0, look.x).normalize().scale(0.3);

            // Calculate position at sword tip (in front of player, shifted right)
            double offsetX = pos.x + look.x * 1.2 + right.x;
            double offsetY = pos.y + living.getEyeHeight() - 0.3;
            double offsetZ = pos.z + look.z * 1.2 + right.z;

            // Spawn purple particles (DRAGON_BREATH for purple effect)
            for (int i = 0; i < 1; i++) {
                double spreadX = (RANDOM.nextDouble() - 0.5) * 0.3;
                double spreadY = (RANDOM.nextDouble() - 0.5) * 0.3;
                double spreadZ = (RANDOM.nextDouble() - 0.5) * 0.3;

                world.sendParticles(
                        PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1),
                        offsetX + spreadX,
                        offsetY + spreadY,
                        offsetZ + spreadZ,
                        1, // count
                        0, 0, 0, 0.01 // velocity// speed
                );
            }

            // Add some sparkles with enchanted hit particles (less frequent)
            if (RANDOM.nextFloat() < 0.15f) {
                world.sendParticles(
                        ParticleTypes.ENCHANTED_HIT,
                        offsetX,
                        offsetY,
                        offsetZ,
                        1,
                        0.1, 0.1, 0.1,
                        0.01
                );
            }
        }
    }
}