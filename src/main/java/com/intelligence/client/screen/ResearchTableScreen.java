package com.intelligence.client.screen;

import com.intelligence.CraftingRestrictions;
import com.intelligence.Intelligence;
import com.intelligence.ResearchManager;
import com.intelligence.block.entity.ResearchTableScreenHandler;
import com.intelligence.client.IntelligenceModClient;
import com.intelligence.ResearchPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class ResearchTableScreen extends HandledScreen<ResearchTableScreenHandler> {

    // Your 256×256 GUI texture
    private static final Identifier TEXTURE =
            Identifier.of(Intelligence.MOD_ID, "textures/gui/research_table.png");

    private ButtonWidget researchButton;

    public ResearchTableScreen(ResearchTableScreenHandler handler,
                               PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        // IMPORTANT → your texture is 256x256
        this.backgroundWidth = 256;
        this.backgroundHeight = 256;

        // moves the "Inventory" text into correct position (bottom left)
        this.playerInventoryTitleY = backgroundHeight - 94;
        this.playerInventoryTitleX = 8;
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // ⬇ Position the research button exactly under the 1-slot research square
        researchButton = ButtonWidget.builder(Text.literal("Research"), btn ->
                ClientPlayNetworking.send(new ResearchPayload())
        ).dimensions(
                x + 50,   // adjust horizontally until it lines up with the image
                y + 62,    // adjust vertically to sit under the research slot box
                60,
                20
        ).build();

        addDrawableChild(researchButton);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;


        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        // Update button activity state
        ItemStack stack = handler.getSlot(0).getStack();
        researchButton.active = !stack.isEmpty() &&
                CraftingRestrictions.hasRequirement(stack.getItem());
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // Draw no vanilla title (image already has title)
        // If you want to hide center title:
        // (Do nothing)

        // Inventory label (drawn bottom-left like vanilla)
        context.drawText(
                textRenderer,
                this.playerInventoryTitle,
                this.playerInventoryTitleX,
                this.playerInventoryTitleY - 49,
                0xFFFFFF,
                false
        );

        // Research cost text
        ItemStack stack = handler.getSlot(0).getStack();
        if (!stack.isEmpty() && CraftingRestrictions.hasRequirement(stack.getItem())) {

            int cost = ResearchManager.getResearchCost(stack.getItem());
            int current = IntelligenceModClient.getClientIntelligence();

            String costText = "Cost: " + cost + " Intelligence";
            int textWidth = textRenderer.getWidth(costText);

            // Position above the button + centered under research area
            int textX = (backgroundWidth - textWidth) / 2;
            int textY = 88;

            if (current >= cost) {
                context.drawText(textRenderer,
                        Text.literal("§a" + costText), textX, textY, 0xFFFFFF, false);
            } else {
                context.drawText(textRenderer,
                        Text.literal("§c" + costText), textX, textY, 0xFFFFFF, false);
            }
        }
    }
}
