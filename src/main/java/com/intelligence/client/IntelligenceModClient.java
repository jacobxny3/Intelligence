package com.intelligence.client;

import com.intelligence.IntelligenceNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class IntelligenceModClient implements ClientModInitializer {
    private static int clientIntelligence = 0;

    @Override
    public void onInitializeClient() {
        // Register packet receiver
        ClientPlayNetworking.registerGlobalReceiver(
                IntelligenceNetworking.IntelligenceUpdatePayload.ID,
                (payload, context) -> {
                    clientIntelligence = payload.intelligence();
                }
        );

        // Register HUD renderer
        HudRenderCallback.EVENT.register(IntelligenceModClient::renderIntelligenceHud);
    }

    public static void renderIntelligenceHud(GuiGraphics context, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null || client.options.hideGui) {
            return;
        }

        int screenWidth = context.guiWidth();
        int screenHeight = context.guiHeight();

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
        context.drawString(client.font, text, x, y, 0xFFFFFFFF);
    }

    public static int getClientIntelligence() {
        return clientIntelligence;
    }
}