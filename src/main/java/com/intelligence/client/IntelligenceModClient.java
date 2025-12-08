package com.intelligence.client;

import com.intelligence.IntelligenceNetworking;
import com.intelligence.ResearchManager;
import com.intelligence.block.entity.ModScreenHandlers;
import com.intelligence.client.screen.ResearchTableScreen;
import com.intelligence.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class IntelligenceModClient implements ClientModInitializer {
    private static int clientIntelligence = 0;

    @Override
    public void onInitializeClient() {
        // Register payload type for client
        // Register packet receiver
        ClientPlayNetworking.registerGlobalReceiver(
                IntelligenceNetworking.IntelligenceUpdatePayload.ID,
                (payload, context) -> {
                    clientIntelligence = payload.intelligence();
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                IntelligenceNetworking.ResearchSyncPayload.ID,
                (payload, context) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        Set<Item> unlockedItems = new HashSet<>();
                        for (String itemId : payload.unlockedItemIds()) {
                            Item item = Registries.ITEM.get(Identifier.of(itemId));
                            if (item != null) {
                                unlockedItems.add(item);
                            }
                        }
                        ResearchManager.setClientUnlocked(client.player.getUuid(), unlockedItems);
                    }
                }
        );

        // Register HUD renderer
        HudRenderCallback.EVENT.register(IntelligenceModClient::renderIntelligenceHud);



        // Register screen
        HandledScreens.register(ModScreenHandlers.RESEARCH_TABLE, ResearchTableScreen::new);

        EntityRendererRegistry.register(ModEntities.FLOATING_ITEM, ItemEntityRenderer::new);

}

    private static void renderIntelligenceHud(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.options.hudHidden) {
            return;
        }

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        // Position: Bottom left, above hotbar
        int x = 50;
        int y = screenHeight - 17;

        // Draw background
        context.fill(x - 2, y - 2, x + 90, y + 12, 0x80000000);

        // Draw border
        context.fill(x - 3, y - 3, x + 91, y - 2, 0xFF4A90E2); // Top
        context.fill(x - 3, y + 12, x + 91, y + 13, 0xFF4A90E2); // Bottom
        context.fill(x - 3, y - 2, x - 2, y + 12, 0xFF4A90E2); // Left
        context.fill(x + 90, y - 2, x + 91, y + 12, 0xFF4A90E2); // Right

        // Draw text
        String text = "§6Intelligence: §f" + clientIntelligence;
        context.drawTextWithShadow(client.textRenderer, text, x, y, 0xFFFFFFFF);
    }

    public static int getClientIntelligence() {
        return clientIntelligence;
    }
}