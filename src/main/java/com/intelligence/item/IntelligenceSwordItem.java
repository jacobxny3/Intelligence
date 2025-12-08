package com.intelligence.item;

import com.intelligence.IntelligenceManager;
import com.intelligence.sound.ModSounds;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.DragonBreathParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class IntelligenceSwordItem extends Item {
    private static final Random RANDOM = new Random();
    private final ToolMaterial material;

    public IntelligenceSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(settings.component(
                net.minecraft.component.DataComponentTypes.ATTRIBUTE_MODIFIERS,
                createAttributeModifiers(toolMaterial, 3, -2.4f)
        ));
        this.material = toolMaterial;
    }

    private static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, int baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                                (double)baseAttackDamage + material.attackDamageBonus(),
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(
                                Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                                (double)attackSpeed,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .build();
    }

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Check if the target is a player
        if (!attacker.getEntityWorld().isClient() && target instanceof ServerPlayerEntity targetPlayer) {
            // Remove 3-7 intelligence
            int intelligenceLost = 3 + RANDOM.nextInt(5); // 3 + (0-4) = 3-7

            int currentIntelligence = IntelligenceManager.getIntelligence(targetPlayer);
            int newIntelligence = Math.max(0, currentIntelligence - intelligenceLost); // Don't go below 0

            IntelligenceManager.setIntelligence(targetPlayer, newIntelligence);

            // Send message to victim
            targetPlayer.sendMessage(
                    Text.literal("§c-§4" + intelligenceLost + " §cIntelligence! §7(Intelligence Sword)"),
                    true
            );

            // Send message to attacker if they're a player
            if (attacker instanceof ServerPlayerEntity attackerPlayer) {
                attackerPlayer.sendMessage(
                        Text.literal("§6Drained §e" + intelligenceLost + " §6Intelligence from " + targetPlayer.getName().getString()),
                        true
                );
            }

            // Play drain sound
            attacker.getEntityWorld().playSound(
                    null,
                    target.getX(), target.getY(), target.getZ(),
                    ModSounds.INTELLIGENCE_SHARD_USE,
                    SoundCategory.PLAYERS,
                    0.7f, 0.8f
            );
        }

        super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, net.minecraft.entity.Entity entity, net.minecraft.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);


        // Only spawn particles when held in main hand or off hand
        // Reduced frequency: only 20% of ticks
        if (entity instanceof LivingEntity living &&
                (slot == net.minecraft.entity.EquipmentSlot.MAINHAND || slot == net.minecraft.entity.EquipmentSlot.OFFHAND) &&
                RANDOM.nextFloat() < 0.2f) {

            // Spawn particles around the sword tip
            Vec3d pos = living.getEntityPos();
            Vec3d look = living.getRotationVec(1.0f);

            // Calculate right vector (perpendicular to look direction)
            Vec3d right = new Vec3d(-look.z, 0, look.x).normalize().multiply(0.3);

            // Calculate position at sword tip (in front of player, shifted right)
            double offsetX = pos.x + look.x * 1.2 + right.x;
            double offsetY = pos.y + living.getStandingEyeHeight() - 0.3;
            double offsetZ = pos.z + look.z * 1.2 + right.z;

            // Spawn purple particles (DRAGON_BREATH for purple effect)
            for (int i = 0; i < 1; i++) {
                double spreadX = (RANDOM.nextDouble() - 0.5) * 0.3;
                double spreadY = (RANDOM.nextDouble() - 0.5) * 0.3;
                double spreadZ = (RANDOM.nextDouble() - 0.5) * 0.3;

                world.spawnParticles(
                        DragonBreathParticleEffect.of(ParticleTypes.DRAGON_BREATH, 1),
                        offsetX + spreadX,
                        offsetY + spreadY,
                        offsetZ + spreadZ,
                        1, // count
                        0, 0, 0, 0.01 // velocity// speed
                );
            }

            // Add some sparkles with enchanted hit particles (less frequent)
            if (RANDOM.nextFloat() < 0.15f) {
                world.spawnParticles(
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