package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.network.packets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * The player inventory button added by shoppery to display
 * the players balance and allows them to deposit/withdraw
 * money. Once initialized, the button will handle itself.
 */
@OnlyIn(Dist.CLIENT)
public abstract class ShopperyButton extends ImageButton {

    /**
     * The textures for the button.
     */
    private static final ResourceLocation TEXTURE
            = new ResourceLocation("shoppery", "textures/gui/shoppery_button.png");

    /**
     * Constructor that allows specifying button position.
     *
     * @param x the x coordinate of the button.
     * @param y the y coordinate of the button.
     */
    private ShopperyButton(int x, int y) {
        super(
                x, y, 46, 18, 0, 0,
                19, TEXTURE, ShopperyButton::onPressed
        );
    }

    // ******
    // Render
    // ******

    /**
     * Handles drawing text atop the button as well
     * as tooltip text when the player hovers over
     * the button.
     *
     * @param x mouse x position.
     * @param y mouse y position.
     * @param z i have no clue.
     */
    public void renderButton(int x, int y, float z) {
        super.renderButton(x, y, z);

        FontRenderer renderer = Minecraft.getInstance().fontRenderer;

        //Button text
        drawCenteredString(
                renderer, getShortenedBalance(),
                this.x + (this.width / 2), this.y + (this.height / 3) - 1,
                0xffffff
        );

        //Button Tooltip
        if((x > this.x && x < this.x + this.width) && (y > this.y && y < this.y + height)){
            drawCenteredString(
                    renderer, getFullBalance(),
                    this.x + (this.width / 2), this.y + (this.height),
                    0xffffff
            );
        }
    }

    /**
     * Called by the renderer to get the text to
     * display on the button. This is normally
     * the players shortened balance. Called
     * very frequently!
     *
     * @return the text to display on the button -
     * the users balance in short form (e.g. $100K)
     */
    protected abstract String getShortenedBalance();

    /**
     * Called by the renderer to get the text to
     * display as the buttons tooltip. This is normally
     * the players full balance. Called very frequently!
     *
     * @return the text to display as the buttons tooltip -
     * the users full balance (e.g. $100,000.00).
     */
    protected abstract String getFullBalance();

    // On Pressed

    /**
     * Called when the button is pressed by the player.
     *
     * @param button the button instance that was pressed.
     */
    private static void onPressed(Button button){
        //TODO: add and open gui
    }

    // ***********************
    // Add button to inventory
    // ***********************

    /**
     * Called when a gui screen is initialized and displayed.
     *
     * <p/>Handles creating the shoppery button and adding it to the
     * players survival inventory gui screen.
     *
     * @param event forge event.
     */
    @OnlyIn(Dist.CLIENT)
    private static void guiInitialized(GuiScreenEvent.InitGuiEvent.Post event){
        Screen gui = event.getGui();

        if (event.getGui() instanceof InventoryScreen) {
            InventoryScreen screen = (InventoryScreen) event.getGui();

            //Request balance
            Packet.send(PacketDistributor.SERVER.noArg(), new PRequestFormattedPlayerBalance(
                    Minecraft.getInstance().player.getUniqueID().toString()
            ));
            Packet.send(PacketDistributor.SERVER.noArg(), new PRequestFullPlayerBalance(
                    Minecraft.getInstance().player.getUniqueID().toString()
            ));

            //Create and add new button
            ShopperyButton button = new ShopperyButton(
                    screen.getGuiLeft() + 104 + 21, screen.height / 2 - 22) {
                long requestWaitTime = 1000;//ms

                long lastSBalanceRequestTime = System.currentTimeMillis();
                @Override
                protected String getShortenedBalance() {
                    if(lastSBalanceRequestTime < System.currentTimeMillis()){
                        lastSBalanceRequestTime = System.currentTimeMillis() + requestWaitTime;
                        Packet.send(PacketDistributor.SERVER.noArg(), new PRequestFormattedPlayerBalance(
                                Minecraft.getInstance().player.getUniqueID().toString()
                        ));
                    }

                    return PReceiveFormattedPlayerBalance.getLastKnownBalance();
                }

                long lastFBalanceRequestTime;
                @Override
                protected String getFullBalance() {
                    if(lastFBalanceRequestTime < System.currentTimeMillis()){
                        lastFBalanceRequestTime = System.currentTimeMillis() + requestWaitTime;
                        Packet.send(PacketDistributor.SERVER.noArg(), new PRequestFullPlayerBalance(
                                Minecraft.getInstance().player.getUniqueID().toString()
                        ));
                    }

                    return PReceiveFullPlayerBalance.getLastKnownBalance();
                }
            };
            event.addWidget(button);
        }
    }

    // ****
    // Init
    // ****

    /**
     * Called once to initialize the functionality of the
     * button. Once initialized, it will add itself
     * to the needed gui screens and handle the rest.
     */
    public static void init(){
        Handler.init();
    }

    /**
     * Handler class used to register the forge
     * InitGuiEvent.Post event once using a static block.
     */
    private static class Handler {

        /**
         * Singleton instance that is registered.
         */
        private static final Handler INSTANCE = new Handler();

        /**
         * Allows the static block to run.
         */
        static void init(){/*NO-OP*/}

        /*
            Static block that handles registering
            the forge InitGuiEvent.Post event.
         */
        static {
            MinecraftForge.EVENT_BUS.register(INSTANCE);
        }

        /**
         * Called when a gui screen is initialized and displayed.
         *
         * @param event forge event.
         */
        @SubscribeEvent
        public void onGuiInitialized(GuiScreenEvent.InitGuiEvent.Post event){
            guiInitialized(event);
        }
    }
}
