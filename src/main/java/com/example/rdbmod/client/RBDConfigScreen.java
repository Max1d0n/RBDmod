package com.example.rdbmod.client;

import com.example.rdbmod.RBDConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RBDConfigScreen extends Screen {

    private final Screen parent;
    private Checkbox enabledCheckbox;
    private VolumeSlider volumeSlider;
    private EditBox rewindEditBox;
    private EditBox witchCallEditBox;
    private Checkbox witchCallCheckbox;

    public RBDConfigScreen(Screen parent) {
        super(Component.literal("Return by Death Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 6;

        // 1. "Enable Return by Death" checkbox
        this.enabledCheckbox = Checkbox.builder(Component.literal("Enable Return by Death"), this.font)
                .pos(centerX - 100, startY)
                .selected(RBDConfig.ENABLED.get())
                .build();
        this.addRenderableWidget(this.enabledCheckbox);

        // 2. Sound Volume Slider
        this.volumeSlider = new VolumeSlider(centerX - 100, startY + 30, 200, 20, RBDConfig.SOUND_VOLUME.get());
        this.addRenderableWidget(this.volumeSlider);

        // 3. Rewind delay text input
        this.rewindEditBox = new EditBox(this.font, centerX - 100, startY + 80, 200, 20, Component.literal("Rewind Ticks"));
        this.rewindEditBox.setValue(String.valueOf(RBDConfig.REWIND_DELAY_TICKS.get()));
        this.addRenderableWidget(this.rewindEditBox);

        // 4. Witch Call delay text input
        this.witchCallEditBox = new EditBox(this.font, centerX - 100, startY + 130, 200, 20, Component.literal("Witch Call Ticks"));
        this.witchCallEditBox.setValue(String.valueOf(RBDConfig.WITCH_CALL_DELAY_TICKS.get()));
        this.addRenderableWidget(this.witchCallEditBox);

        this.witchCallCheckbox = Checkbox.builder(Component.literal("Enable Witch Call Sound"), this.font)
                .pos(centerX - 100, startY + 24)
                .selected(RBDConfig.WITCH_CALL_ENABLED.get())
                .build();
        this.addRenderableWidget(this.witchCallCheckbox);

        // 5. "Save" button
        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveAndClose())
                .bounds(centerX - 105, startY + 170, 100, 20)
                .build());

        // 6. "Cancel" button
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreen(this.parent))
                .bounds(centerX + 5, startY + 170, 100, 20)
                .build());
    }

    private void saveAndClose() {
        RBDConfig.ENABLED.set(this.enabledCheckbox.selected());
        RBDConfig.SOUND_VOLUME.set(this.volumeSlider.getValue());

        try {
            int rewind = Integer.parseInt(this.rewindEditBox.getValue());
            RBDConfig.REWIND_DELAY_TICKS.set(rewind);
        } catch (NumberFormatException ignored) {}

        try {
            int witchCall = Integer.parseInt(this.witchCallEditBox.getValue());
            RBDConfig.WITCH_CALL_DELAY_TICKS.set(witchCall);
        } catch (NumberFormatException ignored) {}

        RBDConfig.SPEC.save();
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int startY = this.height / 5;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, 15, 0xFFFFFF);
        guiGraphics.drawString(this.font, "Rewind Sound Delay (in ticks, 20 = 1s):", centerX - 100, startY + 68, 0xA0A0A0);
        guiGraphics.drawString(this.font, "Witch Call Delay (in ticks, 20 = 1s):", centerX - 100, startY + 118, 0xA0A0A0);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

     //Custom slider component for volume control

    private static class VolumeSlider extends AbstractSliderButton {

        public VolumeSlider(int x, int y, int width, int height, double value) {
            super(x, y, width, height, Component.empty(), value);
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            int percent = (int) (this.value * 100.0);
            this.setMessage(Component.literal("Sound Volume: " + percent + "%"));
        }

        @Override
        protected void applyValue() {
        }

        public double getValue() {
            return this.value;
        }
    }
}