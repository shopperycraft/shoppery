package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.packets.FormattedBalanceRecPacket;
import com.ki11erwolf.shoppery.packets.FormattedBalanceReqPacket;
import com.ki11erwolf.shoppery.packets.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.Logger;

/**
 * The player inventory button added by shoppery to display
 * the players balance and allows them to deposit/withdraw
 * money.
 *
 * <p/>Once initialized, the button will handle itself.
 */
@OnlyIn(Dist.CLIENT)
public abstract class ShopperyButton extends ImageButton {

    /**
     * The textures map for Shoppery buttons.
     */
    public static final ResourceLocation BUTTON_TEXTURES
            = new ResourceLocation("shoppery", "textures/gui/shoppery_buttons.png");

    /**
     * Logging Object
     */
    private static final Logger LOGGER = ShopperyMod.getNewLogger();

    /**
     * Constant value defining button dimensions,
     * positions, and layout.
     */
    private static final int
            SIZE_DIFF   = 77,             //Normal vs Expanded GUI size diff.
            WIDTH       = 46,             //Button width.
            HEIGHT      = 18,             //Button height.
            REL_X       = 125,            //Relative X position to gui.
            REL_INV_Y   = 22;             //Relative -Y position to half gui height.

    /**
     * The inventory gui screen this button is attached to.
     */
    private final InventoryScreen inventoryGUI;

    /**
     * {@code true} if the inventory gui screen had the
     * recipe gui open when opened, and is therefore
     * bigger.
     *
     * Used to determine if the gui changed size
     * and the button needs to be repositioned.
     */
    private boolean isEnlarged;

    /**
     * Constructor that allows specifying button position.
     *
     * @param x the x coordinate of the button.
     * @param y the y coordinate of the button.
     */
    private ShopperyButton(int x, int y, InventoryScreen inventoryGUI) {
        super(
                x, y, 46, 18, 0, 0,
                19, BUTTON_TEXTURES, ShopperyButton::onPressed
        );

        this.inventoryGUI = inventoryGUI;
        this.isEnlarged = inventoryGUI.getRecipeGui().isVisible();
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
        FontRenderer renderer = Minecraft.getInstance().fontRenderer;
        super.renderButton(x, y, z);

        //Have to respond to gui size chnages here.
        posUpdateCheck();

        //Button text
        drawCenteredString(
                renderer, getShortenedBalance(), this.x + (this.width / 2),
                this.y + (this.height / 3) - 1, 0xffffff
        );
    }

    /**
     * Checks if the inventories size has changed,
     * and if so, updates the buttons position
     * accordingly.
     */
    private void posUpdateCheck(){
        if(inventoryGUI.getRecipeGui().isVisible() != isEnlarged){
            boolean toEnlarged = inventoryGUI.getRecipeGui().isVisible();
            if(toEnlarged)          this.x += 77;
            else                    this.x -= 77;

            //reset size change flag.
            this.isEnlarged = !isEnlarged;
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

    // **********
    // On Pressed
    // **********

    /**
     * Called when the button is pressed by the player.
     *
     * @param button the button instance that was pressed.
     */
    private static void onPressed(Button button){
        PlayerEntity player = Minecraft.getInstance().player;
        Screen currentScreen = Minecraft.getInstance().currentScreen;

        //No player, no screens.
        if(player == null)
            return;

        //To Shoppery inventory
        if(currentScreen instanceof InventoryScreen)
            Minecraft.getInstance().displayGuiScreen(new ShopperyInventoryScreen(
                    Minecraft.getInstance().player
            ));

        //To normal Minecraft inventory
        if(currentScreen instanceof ShopperyInventoryScreen)
            Minecraft.getInstance().displayGuiScreen(new InventoryScreen(
                    Minecraft.getInstance().player
            ));

        //We won't do anything if we don't know what the gui screen is.
    }

    // ***********************
    // Add button to inventory
    // ***********************

    /**
     * Called when a gui screen is initialized and displayed. Acts
     * on ShopperyInventoryScreen and Survival InventoryScreen. Handles
     * creating the shoppery button and adding it to the players
     * survival/shoppery inventory gui screen.
     *
     * @param event forge event.
     */
    @OnlyIn(Dist.CLIENT)
    private static void guiInitialized(GuiScreenEvent.InitGuiEvent.Post event){
        PlayerEntity player = Minecraft.getInstance().player;
        Screen gui = event.getGui();

        //If either Shoppery or survival inventory is up
        if(ShopperyInventoryScreen.isSurvivalInventoryDisplayed()
                || ShopperyInventoryScreen.isShopperyInventoryDisplayed()) {
            InventoryScreen screen = (InventoryScreen) event.getGui();

            //and we have a player
            if(player == null){
                LOGGER.warn("Player is null, skipping shoppery button injection...");
                return;
            }

            //First request balance, for button.
            Packet.send(PacketDistributor.SERVER.noArg(), new FormattedBalanceReqPacket(
                    player.getUniqueID().toString()
            ));

            //Then create and add new button
            ShopperyButton button = makeButton(screen, player);
            event.addWidget(button);
        }
    }

    /**
     * Creates the new Shoppery button, for the Inventory
     * GUI, with the calculated layout for the specific
     * Inventory GUI.
     *
     * @param screen the ShopperyButtons inventory screen
     *               displayed to the user.
     * @return the newly created ShopperyButton for the
     * given InventoryScreen.
     */
    private static ShopperyButton makeButton(InventoryScreen screen, PlayerEntity player){
        return new ShopperyButton(
                screen.getGuiLeft() + REL_X, screen.height / 2 - REL_INV_Y, screen) {
            long requestWaitTime = 1000;//Wait time, in ms

            //Short balance.
            long lastSBalanceRequestTime = System.currentTimeMillis();

            @Override
            protected String getShortenedBalance() {
                if(lastSBalanceRequestTime < System.currentTimeMillis()){
                    lastSBalanceRequestTime = System.currentTimeMillis() + requestWaitTime;
                    Packet.send(PacketDistributor.SERVER.noArg(), new FormattedBalanceReqPacket(
                            player.getUniqueID().toString()
                    ));
                }

                return FormattedBalanceRecPacket.getLastKnownBalance();
            }
        };
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
