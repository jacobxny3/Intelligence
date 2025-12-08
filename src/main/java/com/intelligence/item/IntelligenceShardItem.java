package com.intelligence.item;

import com.intelligence.IntelligenceManager;
import com.intelligence.sound.ModSounds;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IntelligenceShardItem extends Item {
    private static final Random RANDOM = new Random();

    public IntelligenceShardItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (!world.isClientSide()) {
            // Generate random intelligence between 5 and 10 (inclusive)
            int intelligenceGained = 5 + RANDOM.nextInt(6); // 5 + (0-5) = 5-10

            ServerPlayer serverPlayer = (ServerPlayer) user;
            IntelligenceManager.addIntelligence(serverPlayer, intelligenceGained);

            // Send feedback message
            user.displayClientMessage(Component.literal("§b+§e" + intelligenceGained + " §bIntelligence! §7(Intelligence Shard)"), true);

            // Play custom sound effect
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    ModSounds.INTELLIGENCE_SHARD_USE, SoundSource.PLAYERS,
                    1.0f, 1.0f);

            // Consume the item
            if (!user.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }
}