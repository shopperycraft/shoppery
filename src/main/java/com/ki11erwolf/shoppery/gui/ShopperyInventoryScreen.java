package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.bank.Wallet;
import com.ki11erwolf.shoppery.network.packets.PReceiveFullPlayerBalance;
import com.ki11erwolf.shoppery.network.packets.PRequestFullPlayerBalance;
import com.ki11erwolf.shoppery.network.packets.Packet;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import com.ki11erwolf.shoppery.util.LocaleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Shoppery's Money section of players survival inventory GUI.
 *
 * <p/>Designed as an extension of the Vanilla survival GUI
 * that will also draw & build the Money section along side
 * the Vanilla survival GUI.
 *
 * <p/>This approach allows us to build, show, and operate
 * the Money section as though it were our own GUI, without
 * interfering with the Vanilla survival inventory GUI.
 */
public class ShopperyInventoryScreen extends InventoryScreen {

    /**
     * The texture for the money section of the Shoppery inventory.
     */
    private static final ResourceLocation BACKGROUND_TEXTURE
            = new ResourceLocation("shoppery", "textures/gui/money_gui.png");

    /**
     * The width of the money section background in pixels.
     */
    private static final int WIDTH = 256;

    /**
     * The height of the money section background in pixels.
     */
    private static final int HEIGHT = 64;

    /**
     * The size difference between the money section background
     * and the normal survival inventory gui background.
     */
    private static final int WIDTH_DIFF = 80;

    /**
     * The player this inventory belongs to.
     */
    private final PlayerEntity player;

    /**
     * The origin X and Y positions
     * relative to {@code guiTop}
     * & {@code guiLeft}, or X and Y
     * respectively.
     */
    private int relX, relY;

    /**
     * The origin X and Y positions of
     * the screen.
     */
    private int trueX, trueY;

    /**
     * The time when the lastMoneySlotRefreshTime
     * full balance request packet was sent. Used
     * to prevent sending packets in quick succession.
     */
    private static long lastPktSendTime = -1;

    /**
     * @param player the player the inventory belongs/is showed to.
     */
    public ShopperyInventoryScreen(PlayerEntity player) {
        super(player);
        this.player = player;
    }

    /**
     * Instruction to render one frame of this screen.
     *
     * <p/>First call in the rendering chain
     * (background, foreground), ultimately
     * resulting in a fully rendered screen.
     *
     * <p/>Handles position and dimension
     * calculating for the scene as well.
     *
     * @param x Obfuscation
     * @param y is
     * @param z wonderful :)
     */
    @Override
    public void render(int x, int y, float z) {
        calculateOriginPosition();
        super.render(x, y, z);
    }

    /**
     * Calculates the X and Y positions of
     * the Money section, both relative
     * and true.
     *
     * This is done each render to allow
     * the Money section to react to the
     * changing inventory gui screen.
     */
    private void calculateOriginPosition(){
        //Centers Money section atop the inventory sections, recipe book included.
        this.relX = guiLeft -  ( WIDTH_DIFF / 2 ) - ( getRecipeGui().isVisible() ? 77 : 0 );
        this.relY = guiTop - HEIGHT - 1;//-1 for spacing.

        this.trueX = (getRecipeGui().isVisible() ? -77 : 0) + (this.xSize / 2) - (WIDTH / 2);
        this.trueY = -4 - HEIGHT;
    }

    // **********
    // Background
    // **********

    /**
     * {@inheritDoc}.
     *
     * <p/>Also delegates the drawing of the money section of the
     * gui background to {@link #drawMoneyBackgroundLayer(float, int, int)}.
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        drawMoneyBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    /**
     * Draws the money section background image to
     * the gui background, centered atop the normal gui.
     */
    private void drawMoneyBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.blit(relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    // **********
    // Foreground
    // **********

    /**
     * {@inheritDoc}, specifically the text displayed atop
     * the background.
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        //Title
        font.drawString(LocaleUtil.get(LocaleDomains.TITLE.sub(LocaleDomains.SCREEN),
                "player_bank"), X(5), Y(7), 0x3F3F3F
        );

        //Balance
        drawCenteredString(font, Wallet.CURRENCY_SYMBOL + getBalance(),
                X(70), Y(23), 0x00E500
        );
    }

    /**
     * Gets the players last known balance from server.
     * Will also request a balance update if enough time
     * has elapsed.
     */
    protected static String getBalance(){
        PlayerEntity player = Minecraft.getInstance().player;
        if(player == null) //No player, no balance.
            return "Not a player!";//TODO: I18n

        //Timer to prevent spamming the server every gui redraw.
        //TODO: Config packet lag.
        if(System.currentTimeMillis() > lastPktSendTime + 250 /*Time between*/ || lastPktSendTime == -1){
            Packet.send(
                    PacketDistributor.SERVER.noArg(),
                    new PRequestFullPlayerBalance(player.getUniqueID().toString())
            );
            lastPktSendTime = System.currentTimeMillis();
        }

        return PReceiveFullPlayerBalance.getLastKnownBalance() == null ?
                "<ERROR>" : PReceiveFullPlayerBalance.getLastKnownBalance();
    }

    // ************
    // Util Methods
    // ************

    /**
     * Shorthand for  {@link #trueX} {@code + input}.
     */
    private int X(Number i) { return trueX + i.intValue(); }

    /**
     * Shorthand for  {@link #trueY} {@code + input}.
     */
    private int Y(Number i) { return trueY + i.intValue(); }

    /**
     * Checks if the current screen displayed
     * ({@link Minecraft#currentScreen})
     * is specifically Shoppery's Inventory
     * screen..
     *
     * @return {@code true} if and only if the
     * screen currently displayed is Shoppery's
     * Inventory screen, {@code false} otherwise.
     */
    public static boolean isShopperyInventoryDisplayed(){
        return Minecraft.getInstance().currentScreen instanceof ShopperyInventoryScreen;
    }

    /**
     * Checks if the current screen displayed
     * ({@link Minecraft#currentScreen}) is
     * specifically Vanilla's Survival Inventory
     * screen.
     *
     * @return {@code true} if and only if the
     * screen currently displayed is Vanilla's
     * Survival Inventory screen, {@code false}
     * otherwise.
     */
    public static boolean isSurvivalInventoryDisplayed(){
        if(Minecraft.getInstance().currentScreen instanceof CreativeScreen)
            return false;

        if(Minecraft.getInstance().currentScreen instanceof InventoryScreen)
            try{
                //Ensure it isn't the creative screen by invoking method on survival only recipe gui.
                ((InventoryScreen) Minecraft.getInstance().currentScreen).getRecipeGui().isVisible();
                return true;//Passed - Survival screen.
            } catch (NullPointerException e) { return false; /* Failed - Creative screen. */ }

        return false;
    }
}
