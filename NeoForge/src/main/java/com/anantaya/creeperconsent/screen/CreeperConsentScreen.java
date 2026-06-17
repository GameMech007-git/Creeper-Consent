package com.anantaya.creeperconsent.screen;

import com.anantaya.creeperconsent.network.CreeperConsentResponsePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CreeperConsentScreen extends Screen {

    private static final int BOX_WIDTH = 230;
    private static final int BOX_HEIGHT = 118;

    private static final int BUTTON_WIDTH = 84;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_GAP = 12;

    // Cryptic labels
    private static final Component ALLOW_TEXT =
            Component.literal("ᛒᛟᛟᛗ").withStyle(ChatFormatting.RED);

    private static final Component DENY_TEXT =
            Component.literal("ᛋᛏᛟᛈ").withStyle(ChatFormatting.GREEN);

    private final int creeperEntityId;

    public CreeperConsentScreen(int creeperEntityId) {
        super(Component.literal("Creeper Consent"));
        this.creeperEntityId = creeperEntityId;
    }

    @Override
    protected void init() {
        int allowX = getAllowButtonX();
        int denyX = getDenyButtonX();
        int buttonY = getButtonY();

        this.addRenderableWidget(
                Button.builder(
                                ALLOW_TEXT,
                                button -> onAllowButtonPressed()
                        )
                        .bounds(allowX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(
                                DENY_TEXT,
                                button -> onDenyButtonPressed()
                        )
                        .bounds(denyX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        graphics.fill(0, 0, this.width, this.height, 0xAA050A05);

        int boxX = this.width / 2 - BOX_WIDTH / 2;
        int boxY = this.height / 2 - BOX_HEIGHT / 2;

        // Outer glow
        graphics.fill(boxX - 2, boxY - 2, boxX + BOX_WIDTH + 2, boxY + BOX_HEIGHT + 2, 0xCC0A1A0A);

        // Main panel
        graphics.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + BOX_HEIGHT, 0xFF102610);

        // Inner darker panel
        graphics.fill(boxX + 3, boxY + 3, boxX + BOX_WIDTH - 3, boxY + BOX_HEIGHT - 3, 0xFF0B180B);

        // Green outline
        graphics.outline(boxX, boxY, BOX_WIDTH, BOX_HEIGHT, 0xFF3CFF5A);

        // Accent strip
        graphics.fill(boxX + 4, boxY + 4, boxX + BOX_WIDTH - 4, boxY + 12, 0x882DFF57);

        graphics.centeredText(
                this.font,
                Component.literal("ᛏ ᚫ ᛟ ᛉ ᛇ ᛈ ᚦ ᛚ ᛗ"),
                this.width / 2,
                boxY + 17,
                0xFF6BFF7C
        );

        graphics.centeredText(
                this.font,
                Component.literal("ᚺᛁᛊᛊ ᛟᚠ ᚲᚺᛟᛁᚲᛖ"),
                this.width / 2,
                boxY + 35,
                0xFF98FF98
        );

        graphics.centeredText(
                this.font,
                Component.literal("⟡ ◈ ⬢ ◇ ◆ ◉ ⬡ ◍"),
                this.width / 2,
                boxY + 53,
                0xFF49FF6D
        );

        graphics.centeredText(
                this.font,
                Component.literal("ssss..."),
                this.width / 2,
                boxY + 69,
                0xFF3CFF5A
        );

        // Creepy aura panels behind buttons
        drawButtonAura(graphics, getAllowButtonX(), getButtonY(), BUTTON_WIDTH, BUTTON_HEIGHT, true);
        drawButtonAura(graphics, getDenyButtonX(), getButtonY(), BUTTON_WIDTH, BUTTON_HEIGHT, false);

        super.extractRenderState(graphics, mouseX, mouseY, deltaTicks);
    }

    private void drawButtonAura(GuiGraphicsExtractor graphics, int x, int y, int width, int height, boolean allowButton) {
        if (allowButton) {
            // Red creepy boom button
            graphics.fill(x - 2, y - 2, x + width + 2, y + height + 2, 0xAA3A0000);
            graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0x884F0A0A);
            graphics.outline(x - 2, y - 2, width + 4, height + 4, 0xFFAA2222);
        } else {
            // Green creepy stop button
            graphics.fill(x - 2, y - 2, x + width + 2, y + height + 2, 0xAA063A06);
            graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0x8832AA32);
            graphics.outline(x - 2, y - 2, width + 4, height + 4, 0xFF3CFF5A);
        }
    }

    private int getButtonY() {
        int boxY = this.height / 2 - BOX_HEIGHT / 2;
        return boxY + BOX_HEIGHT - 26;
    }

    private int getAllowButtonX() {
        int totalButtonWidth = BUTTON_WIDTH * 2 + BUTTON_GAP;
        return this.width / 2 - totalButtonWidth / 2;
    }

    private int getDenyButtonX() {
        return getAllowButtonX() + BUTTON_WIDTH + BUTTON_GAP;
    }

    private void onAllowButtonPressed() {
        sendResponse(true);
        this.onClose();
    }

    private void onDenyButtonPressed() {
        sendResponse(false);
        this.onClose();
    }

    private void sendResponse(boolean allow) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.getConnection() == null) {
            return;
        }

        minecraft.getConnection().send(
                new ServerboundCustomPayloadPacket(
                        new CreeperConsentResponsePayload(creeperEntityId, allow)
                )
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}