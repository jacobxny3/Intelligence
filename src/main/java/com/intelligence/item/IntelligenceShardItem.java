package com.intelligence.item;

import com.intelligence.IntelligenceManager;
import com.intelligence.sound.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Random;

public class IntelligenceShardItem extends Item {
    private static final Random RANDOM = new Random();

    public IntelligenceShardItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            // Generate random intelligence between 5 and 10 (inclusive)
            int intelligenceGained = 5 + RANDOM.nextInt(6); // 5 + (0-5) = 5-10

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;
            IntelligenceManager.addIntelligence(serverPlayer, intelligenceGained);

            // Send feedback message
            user.sendMessage(Text.literal("§b+§e" + intelligenceGained + " §bIntelligence! §7(Intelligence Shard)"), true);

            // Play custom sound effect
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    ModSounds.INTELLIGENCE_SHARD_USE, SoundCategory.PLAYERS,
                    1.0f, 1.0f);

            // Consume the item
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return ActionResult.SUCCESS;
    }
}