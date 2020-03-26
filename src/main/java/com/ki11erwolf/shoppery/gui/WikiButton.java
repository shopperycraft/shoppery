package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.Util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The Wiki button, added to the {@link ShopperyInventoryScreen}
 * that links the player to the Wiki website.
 */
class WikiButton extends ImageButton {

    /**
     * The Wiki's URL to link to.
     */
    private static final String WIKI_URL = "https://shopperycraft.github.io/";

    /**
     * X and Y starting positions of the button.
     */
    private static final int POSITION_X = 6, POSITION_Y = 36;

    /**
     * The width and height of the button.
     */
    private static final int WIDTH = 20, HEIGHT = 18;

    /**
     * Coordinates on the texture map where the buttons
     * texture starts.
     */
    private static final int TEXTURE_POSITION_X = 47, TEXTURE_POSITION_Y = 0;

    /**
     * Constructs a new Wiki Button with the given X and Y
     * starting positions.
     *
     * @param inventoryX X starting position.
     * @param inventoryY Y starting position.
     */
    public WikiButton(int inventoryX, int inventoryY) {
        super(inventoryX + POSITION_X, inventoryY + POSITION_Y,
                WIDTH, HEIGHT, TEXTURE_POSITION_X, TEXTURE_POSITION_Y,
                HEIGHT + 1, ShopperyButton.BUTTON_TEXTURES,
                (button) -> confirm()
        );
    }

    /**
     * Displays Minecraft's link warning to the player
     * as confirmation before opening the Wiki URL.
     */
    private static void confirm(){
        Minecraft.getInstance().displayGuiScreen(new ConfirmOpenLinkScreen(
                (bool) -> {
                    if(bool)
                        openLink();

                    Minecraft.getInstance().displayGuiScreen(null);
                },
                WIKI_URL,
                true
                )
        );
    }

    /**
     * Opens a new web browser tab to the
     * Wiki.
     */
    private static void openLink(){
        try {
            Util.getOSType().openURI(new URI(WIKI_URL));
        } catch (URISyntaxException e) {
            ShopperyMod.getNewLogger().error("Failed to open wiki url", e);
        }
    }

    /**
     * {@inheritDoc}
     * Extended to provide tooltip rendering.
     */
    @Override
    public void renderButton(int x, int y, float z) {
        super.renderButton(x, y, z);

        if(this.isHovered)
            drawTooltip(x, y);
    }

    /**
     * Draws the buttons tooltip on screen.
     *
     * @param x mouse x position.
     * @param y mouse y position.
     */
    private void drawTooltip(int x, int y){
        drawCenteredString(
                Minecraft.getInstance().fontRenderer,
                LocaleDomains.TOOLTIP.sub(LocaleDomains.BUTTON).get("wiki"),
                this.x + (width / 2) + 67,
                this.y + (height / 2) - 4,
                0XF8F8F8
        );
    }
}