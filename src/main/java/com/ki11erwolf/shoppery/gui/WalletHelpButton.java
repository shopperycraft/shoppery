package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.LocaleDomain;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import com.ki11erwolf.shoppery.util.RenderUtil.TimedAnimationStepper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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
 * The Wiki button, added to the {@link WalletInventoryScreen}
 * that links the player to the Wiki website.
 */
@OnlyIn(Dist.CLIENT)
class WalletHelpButton extends ImageButton implements WidgetFix {

    /**
     * The Wiki's URL to link to.
     */
    private static final String WIKI_URL = "https://shopperycraft.github.io/";

    /**
     * This widgets name in the language file.
     */
    private static final LocaleDomain WIDGET_DOMAIN = () -> "help_button";

    /**
     * The number of milliseconds a tooltip should be rendered
     * for before the next tooltip is cycled and rendered.
     */
    private static final int MILLISECONDS_PER_TOOLTIP = 2300;

    /**
     * Helper object that calculates what tooltip to display
     * based upon tooltip render time.
     */
    private static final TimedAnimationStepper TOOLTIP_ANIMATION_STEPPER = new TimedAnimationStepper(
        MILLISECONDS_PER_TOOLTIP, 2,  (frame) -> tooltipToRender = frame
    );

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
     * Which of the tooltips to render in the next
     * frame.
     */
    private static int tooltipToRender = 0;

    /**
     * Constructs a new Wiki Button with the given X and Y
     * starting positions.
     *
     * @param inventoryX X starting position.
     * @param inventoryY Y starting position.
     */
    public WalletHelpButton(int inventoryX, int inventoryY) {
        super(inventoryX + POSITION_X, inventoryY + POSITION_Y,
                WIDTH, HEIGHT, TEXTURE_POSITION_X, TEXTURE_POSITION_Y,
                HEIGHT + 1, WalletToggleButton.WALLET_BUTTON_TEXTURES,
                (button) -> { /* Not used - overridden */ }
        );
    }

    // #########
    // Rendering
    // #########

    @Override @ParametersAreNonnullByDefault
    public void render(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
        super.render(matrix, mouseXPos, mouseYPos, frameTime);
        renderTooltip(matrix, x, y, frameTime);
    }


    /**
     * Handles rendering the different tooltips on screen
     * when hovered over by mouse. Each tooltip is rendered
     * for a set amount of time until the next tooltip is
     * cycled to - repeats indefinitely.
     *
     * @param x mouse x position.
     * @param y mouse y position.
     * @param frameTime milliseconds taken to render the
     * last frame.
     */
    private void renderTooltip(MatrixStack matrixStack, int x, int y, float frameTime) {
        if(!(isHovered && WalletInventoryScreen.ALLOW_TOOLTIPS)) return;
        TOOLTIP_ANIMATION_STEPPER.onRender(frameTime);

        int offsetX = (tooltipToRender == 0) ? 51 : 45;
        String tooltipKey = (tooltipToRender == 0) ? "wiki" : "command";

        renderTooltip2(matrixStack, Minecraft.getInstance().fontRenderer,
                new StringTextComponent(LocaleDomains.TOOLTIP
                        .sub(LocaleDomains.WIDGET).sub(WIDGET_DOMAIN).get(tooltipKey)),
                (this.getXPos() + (this.getWidth() / 2) + offsetX) +
                        WalletInventoryScreen.COMPONENT_TOOLTIP_X_OFFSET,
                this.getYPos() + (this.getHeight() / 2) + 1, 0XF8F8F8
        );
    }

    // ############
    // Action Event
    // ############

    /**
     * Called when a mouse action is performed, to check
     * if this widget object is clicked. Will handle invoking
     * a click if the mouse if hovering this widget is clicked.
     *
     * @param x mouse X position when clicked.
     * @param y mouse Y position when clicked.
     * @param button the mouse button id.
     * @return {@code true} if a clicked event
     * was raised (effectively clicked).
     */
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if(isSelfClicked(x, y, button)){
            if (isMouseOver(x, y)) { //If Hovered
                if(button == 0 || button == 1) {
                    //If left/right button
                    onAction(button == 0);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * The event fired on left or right mouse click.
     *
     * @param isLeftClick {@code true} if left
     * button was clicked, {@code false} if
     * right button was clicked.
     */
    protected void onAction(boolean isLeftClick){
        if(isLeftClick) confirmOpenWikiLink();
        else runShopperyCommand();
    }

    // #######
    // Actions
    // #######

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

    /**
     * Runs the {@code /shoppery} command in the chat for the
     * client side player.
     */
    @OnlyIn(Dist.CLIENT)
    private static void runShopperyCommand() {
        ClientPlayerEntity player;

        if((player = Minecraft.getInstance().player) != null)
            player.sendChatMessage("/shoppery");
    }
}