package com.example.rdbmod.client;

import com.example.rdbmod.RBDConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RBDConfigScreen extends Screen {

    private final Screen parent;
    private Checkbox enabledCheckbox;
    private EditBox rewindEditBox;
    private EditBox witchCallEditBox;

    public RBDConfigScreen(Screen parent) {
        super(Component.literal("Return by Death Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;

        // 1. "Enable Return by Death" checkbox
        this.enabledCheckbox = Checkbox.builder(Component.literal("Enable Return by Death"), this.font)
                .pos(centerX - 100, startY)
                .selected(RBDConfig.ENABLED.get())
                .build();
        this.addRenderableWidget(this.enabledCheckbox);

        // 2. Rewind delay text input
        this.rewindEditBox = new EditBox(this.font, centerX - 100, startY + 40, 200, 20, Component.literal("Rewind Ticks"));
        this.rewindEditBox.setValue(String.valueOf(RBDConfig.REWIND_DELAY_TICKS.get()));
        this.addRenderableWidget(this.rewindEditBox);

        // 3. Witch Call delay text input
        this.witchCallEditBox = new EditBox(this.font, centerX - 100, startY + 80, 200, 20, Component.literal("Witch Call Ticks"));
        this.witchCallEditBox.setValue(String.valueOf(RBDConfig.WITCH_CALL_DELAY_TICKS.get()));
        this.addRenderableWidget(this.witchCallEditBox);

        // 4. "Save" button
        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveAndClose())
                .bounds(centerX - 105, startY + 120, 100, 20)
                .build());

        // 5. "Cancel" button
        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreen(this.parent))
                .bounds(centerX + 5, startY + 120, 100, 20)
                .build());
    }

    private void saveAndClose() {
        RBDConfig.ENABLED.set(this.enabledCheckbox.selected());

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
        int startY = this.height / 4;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, 15, 0xFFFFFF);
        guiGraphics.drawString(this.font, "Rewind Sound Delay (in ticks, 20 = 1s):", centerX - 100, startY + 28, 0xA0A0A0);
        guiGraphics.drawString(this.font, "Witch Call Delay (in ticks, 20 = 1s):", centerX - 100, startY + 68, 0xA0A0A0);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}