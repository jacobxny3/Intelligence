package com.intelligence.client.screen;

import com.intelligence.CraftingRestrictions;
import com.intelligence.Intelligence;
import com.intelligence.ResearchManager;
import com.intelligence.block.entity.ResearchTableScreenHandler;
import com.intelligence.client.IntelligenceModClient;
import com.intelligence.ResearchPayload;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;


public class ResearchTableScreen extends AbstractContainerScreen<ResearchTableScreenHandler> {

    // Your 256×256 GUI texture
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(Intelligence.MOD_ID, "textures/gui/research_table.png");

    private Button researchButton;

    public ResearchTableScreen(ResearchTableScreenHandler handler,
                               Inventory inventory, Component title) {
        super(handler, inventory, title);

        // IMPORTANT → your texture is 256x256
        this.imageWidth = 256;
        this.imageHeight = 256;

        // moves the "Inventory" text into correct position (bottom left)
        this.inventoryLabelY = imageHeight - 94;
        this.inventoryLabelX = 8;
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // ⬇ Position the research button exactly under the 1-slot research square
        researchButton = Button.builder(Component.literal("Research"), btn ->
                ClientPlayNetworking.send(new ResearchPayload())
        ).bounds(
                x + 50,   // adjust horizontally until it lines up with the image
                y + 62,    // adjust vertically to sit under the research slot box
                60,
                20
        ).build();

        addRenderableWidget(researchButton);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;



        context.blit(
                RenderPipelines.GUI_TEXTURED,  // NEW in 1.21.6
                TEXTURE,
                x, y,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight,
                0xFFFFFFFF
        );
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTooltip(context, mouseX, mouseY);

        // Update button activity state
        ItemStack stack = menu.getSlot(0).getItem();
        researchButton.active = !stack.isEmpty() &&
                CraftingRestrictions.hasRequirement(stack.getItem());
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        // Draw no vanilla title (image already has title)
        // If you want to hide center title:
        // (Do nothing)

        // Inventory label (drawn bottom-left like vanilla)
        context.drawString(
                font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY - 49,
                0xFFFFFFFF,
                false
        );

        // Research cost text
        ItemStack stack = menu.getSlot(0).getItem();
        if (!stack.isEmpty() && CraftingRestrictions.hasRequirement(stack.getItem())) {

            int cost = ResearchManager.getResearchCost(stack.getItem());
            int current = IntelligenceModClient.getClientIntelligence();

            String costText = "Cost: " + cost + " Intelligence";
            int textWidth = font.width(costText);

            // Position above the button + centered under research area
            int textX = (imageWidth - textWidth) / 2;
            int textY = 88;

            if (current >= cost) {
                context.drawString(font,
                        Component.literal("§a" + costText), textX, textY, 0xFFFFFFFF, false);
            } else {
                context.drawString(font,
                        Component.literal("§c" + costText), textX, textY, 0xFFFFFFFF, false);
            }
        }
    }
}
