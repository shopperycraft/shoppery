package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The Wiki button, added to the {@link ShopperyInventoryScreen}
 * that links the player to the Wiki website.
 */
@OnlyIn(Dist.CLIENT)
class WikiButton extends ImageButton implements WidgetFix {

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
                (button) -> confirmOpenWikiLink()
        );
    }

    // #########
    // Rendering
    // #########

    /**
     * Obfuscated {@link #render(MatrixStack, int, int, float)}.
     */
    @Override @ParametersAreNonnullByDefault
    public void func_230431_b_(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
        super.func_230431_b_(matrix, mouseXPos, mouseYPos, frameTime);
        this.render(matrix, mouseXPos, mouseYPos, frameTime);
    }

    /**
     * {@inheritDoc}
     * Extended to provide tooltip rendering.
     */
    @Override
    public void render(MatrixStack matrixStack, int mouseXPos, int mouseYPos, float frameTime) {
        if (this.isHovered())
            drawTooltip(matrixStack, mouseXPos, mouseYPos);
    }

    /**
     * Draws the buttons tooltip on screen.
     *
     * @param x mouse x position.
     * @param y mouse y position.
     */
    private void drawTooltip(MatrixStack matrixStack, int x, int y) {
        renderTooltip2(
                matrixStack,
                Minecraft.getInstance().fontRenderer,
                new StringTextComponent(LocaleDomains.TOOLTIP.sub(LocaleDomains.WIDGET).get("wiki_button")),
                (this.getXPos() + (this.getWidth() / 2) + 56) + ShopperyInventoryScreen.COMPONENT_TOOLTIP_X_OFFSET,
                this.getYPos() + (this.getHeight() / 2) + 1, 0XF8F8F8
        );
    }

    // ############
    // Action Event
    // ############

    /**
     * Displays Minecraft's link warning to the player
     * as confirmation before opening the Wiki URL.
     */
    private static void confirmOpenWikiLink() {
        Minecraft.getInstance().displayGuiScreen(
                new ConfirmOpenLinkScreen((bool) -> {
                    if (bool)
                        openLinkToWiki();

                    Minecraft.getInstance().displayGuiScreen(null);
                }, WIKI_URL, true)
        );
    }

    /**
     * Opens a new web browser tab to the Wiki link: {@link #WIKI_URL}.
     */
    private static void openLinkToWiki() {
        try {
            Util.getOSType().openURI(new URI(WIKI_URL));
        } catch (URISyntaxException e) {
            ShopperyMod.getNewLogger().error("Failed to open wiki url", e);
        }
    }
}