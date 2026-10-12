package com.example.rdbmod.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RBDTransitionScreen extends Screen {

    private int ticks = 0;

    public RBDTransitionScreen() {
        super(Component.literal("Return by Death"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float progress = Math.min(1.0F, (ticks + partialTick) / 15.0F);

        int alpha = (int) (progress * 255);
        int blackOverlay = (alpha << 24);

        guiGraphics.fill(0, 0, this.width, this.height, blackOverlay);

        if (progress > 0.4F) {
            float textProgress = (progress - 0.4F) / 0.6F;
            int textAlpha = (int) (textProgress * 255);
            int textColor = (textAlpha << 24) | 0x8B0000;

            guiGraphics.drawCenteredString(this.font, "— RETURN BY DEATH —", this.width / 2, this.height / 2, textColor);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        ticks++;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}