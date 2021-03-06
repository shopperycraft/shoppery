package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.config.ModConfig;
import com.ki11erwolf.shoppery.config.categories.GeneralConfig;
import com.ki11erwolf.shoppery.packets.FormattedBalanceRecPacket;
import com.ki11erwolf.shoppery.packets.FormattedBalanceReqPacket;
import com.ki11erwolf.shoppery.packets.Packet;
import com.ki11erwolf.shoppery.util.CurrencyUtil;
import com.ki11erwolf.shoppery.util.WaitTimer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * The player inventory button added by shoppery to display
 * the players balance and allows them to deposit/withdraw
 * money by opening the wallet gui.
 *
 * <p/>Once initialized, the button will handle itself.
 */
@OnlyIn(Dist.CLIENT)
public abstract class WalletToggleButton extends ImageButton implements WidgetFix {

    /**
     * The textures map for Shoppery buttons.
     */
    public static final ResourceLocation WALLET_BUTTON_TEXTURES
            = new ResourceLocation("shoppery", "textures/gui/wallet_buttons.png");

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
            REL_INV_Y   = 61;             //Relative -Y position to half gui height.

    /**
     * WaitTimer that tracks and limits the sending of
     * multiple balance requests.
     */
    private static WaitTimer BALANCE_REQUEST_TIMER;

    static{
        if(BALANCE_REQUEST_TIMER == null)
            BALANCE_REQUEST_TIMER = new WaitTimer(
                    ModConfig.GENERAL_CONFIG.getCategory(GeneralConfig.class).getPacketWaitTime()
            );
    }

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
    private WalletToggleButton(int x, int y, InventoryScreen inventoryGUI) {
        super(
                x, y, 46, 18, 0, 0,
                19, WALLET_BUTTON_TEXTURES, WalletToggleButton::onPressed
        );

        this.inventoryGUI = inventoryGUI;
        this.isEnlarged = inventoryGUI.getRecipeGui().isVisible();
    }

    // ******
    // Render
    // ******

    /**
     * Handles position updates and drawing the text atop
     * the button that displays the players balance.
     *
     * @param mouseXPos mouse x position.
     * @param mouseYPos mouse y position.
     * @param frameTime the time taken to render the frame.
     */
    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
        super.render(matrix, mouseXPos, mouseYPos, frameTime);

        posUpdateCheck();//Have to respond to gui size changes first.

        //Button text
        renderTooltip1(matrix, Minecraft.getInstance().fontRenderer, new StringTextComponent(getShortenedBalance()),
                this.getXPos() + (this.getWidth() / 2), this.getYPos() + (this.getHeight() / 3) - 1, 0xffffff
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

            if(toEnlarged)          this.setXPos(this.getXPos() + 77);
            else                    this.setXPos(this.getXPos() - 77);

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
            Minecraft.getInstance().displayGuiScreen(new WalletInventoryScreen(
                    Minecraft.getInstance().player
            ));

        //To normal Minecraft inventory
        if(currentScreen instanceof WalletInventoryScreen)
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
        if(WalletInventoryScreen.isSurvivalInventoryDisplayed()
                || WalletInventoryScreen.isShopperyInventoryDisplayed()) {
            InventoryScreen screen = (InventoryScreen) event.getGui();

            //and we have a player
            if(player == null){
                LOGGER.warn("Player is null. Skipping addition of shoppery button to inventory...");
                return;
            }

            //First request balance, for button.
            Packet.send(PacketDistributor.SERVER.noArg(), new FormattedBalanceReqPacket(
                    player.getUniqueID().toString()
            ));

            //Then create and add new button
            WalletToggleButton button = makeButton(screen, player);
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
    private static WalletToggleButton makeButton(InventoryScreen screen, PlayerEntity player){
        return new WalletToggleButton(
                screen.getGuiLeft() + REL_X,
                (screen.getGuiTop() + REL_INV_Y),
                screen) {
            @Override
            protected String getShortenedBalance() {
                WalletToggleButton.requestBalance(player);
                return CurrencyUtil.CURRENCY_SYMBOL + FormattedBalanceRecPacket
                        .getLastKnownBalance();
            }
        };
    }

    /**
     * Will request an update of the players balance from
     * the server, within a WaitTimer.
     *
     * @param player the player who's balance we're requesting.
     */
    private static void requestBalance(PlayerEntity player){
        BALANCE_REQUEST_TIMER.time((x) -> {
            Packet.send(
                    PacketDistributor.SERVER.noArg(),
                    new FormattedBalanceReqPacket(player.getUniqueID().toString())
            );
            return null;
        });
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
