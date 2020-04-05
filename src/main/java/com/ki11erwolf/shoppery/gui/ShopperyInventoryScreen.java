package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.config.ShopperyConfig;
import com.ki11erwolf.shoppery.config.categories.General;
import com.ki11erwolf.shoppery.packets.FullBalanceRecPacket;
import com.ki11erwolf.shoppery.packets.FullBalanceReqPacket;
import com.ki11erwolf.shoppery.packets.ItemPriceRecPacket;
import com.ki11erwolf.shoppery.packets.Packet;
import com.ki11erwolf.shoppery.util.CurrencyUtil;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import com.ki11erwolf.shoppery.util.WaitTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import static com.ki11erwolf.shoppery.item.ShopperyItems.*;

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
@OnlyIn(Dist.CLIENT)
public class ShopperyInventoryScreen extends InventoryScreen {

    /**
     * The texture map for the various GUI backgrounds
     * used by Shoppery.
     */
    public static final ResourceLocation SHOPPERY_GUIS
            = new ResourceLocation("shoppery", "textures/gui/shoppery_guis.png");

    /**
     * The error message displayed as the players balance, if any error
     * occurs and the balance could not be retrieved.
     */
    private static final String ERROR_MESSAGE = LocaleDomains.ERROR.get("balance");

    /**
     * WaitTimer that tracks and limits the sending of
     * multiple balance requests.
     */
    private static final WaitTimer BALANCE_REQUEST_TIMER
            = new WaitTimer(ShopperyConfig.GENERAL_CONFIG.getCategory(General.class).getPacketWaitTime());

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
     * The widget acting as a slot that can both, deposit currency items
     * into the wallet and request the price of any items put in the slot.
     */
    private InputSlot inputSlot;

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
     * @param player the player the inventory belongs/is showed to.
     */
    public ShopperyInventoryScreen(PlayerEntity player) {
        super(player);
        this.player = player;
    }

    /**
     * {@inheritDoc}
     * Constructs and initializes the gui screen
     * and all sub components.
     */
    @Override
    protected void init() {
        super.init();

        calculateOriginPosition();
        initCashSection();
        this.addButton(new WikiButton(relX, relY));
        this.addButton((this.inputSlot = new InputSlot(player, relX + 122, relY + 36)));
    }

    /**
     * Draws the inventory on the screen.
     *
     * <p/>First call in the rendering chain
     * (background, foreground), ultimately
     * resulting in a fully rendered screen.
     *
     * <p/>Handles position and dimension
     * calculating for the scene as well.
     *
     * @param x mouseX
     * @param y mouseY
     * @param time frame render time
     */
    @Override
    public void render(int x, int y, float time) {
        calculateOriginPosition();
        super.render(x, y, time);
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
        this.relX = guiLeft - ( WIDTH_DIFF / 2 ) - ( getRecipeGui().isVisible() ? 77 : 0 );
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
        Minecraft.getInstance().getTextureManager().bindTexture(SHOPPERY_GUIS);
        this.blit(relX, relY, 0, 0, WIDTH, HEIGHT);
    }

    // **********
    // Foreground
    // **********

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        drawTitles();

        //Info screen: balance/prices
        if(inputSlot.isOccupied()) drawItemPrices();
        else drawPlayerBalance();
    }

    /**
     * Handles rendering the GUIs title(s)
     * and other static text.
     */
    protected void drawTitles(){
        if(inputSlot.isOccupied()){
            font.drawString(LocaleDomains.TEXT.sub(LocaleDomains.SCREEN)
                    .get("buy"), X(24), Y(7), 0x3F3F3F
            );

            font.drawString(LocaleDomains.TEXT.sub(LocaleDomains.SCREEN)
                    .get("sell"), X(81), Y(7), 0x3F3F3F
            );
        } else {
            font.drawString(LocaleDomains.TITLE.sub(LocaleDomains.SCREEN)
                    .format("wallet", player.getDisplayName().getString()),
                    X(5), Y(7), 0x3F3F3F
            );
        }
    }

    /**
     * Draws the requested item prices onto the gui
     * within the information screen section. Also
     * draws the buy and sell headers atop the
     * information screen.
     */
    protected void drawItemPrices(){
        if(!ItemPriceRecPacket.doesLastReceivedHavePrice()){
            drawCenteredString(font, LocaleDomains.TEXT.sub(LocaleDomains.SCREEN)
                            .get("no_price"),
                    X(73), Y(23), 0x9C1313
            );
            return;
        }

        drawCenteredString(font, CurrencyUtil.CURRENCY_SYMBOL
                        + CurrencyUtil.toFullString(ItemPriceRecPacket.getLastReceivedBuyPrice()),
                X(38), Y(23), 0xD11F1F
        );

        drawCenteredString(font, CurrencyUtil.CURRENCY_SYMBOL
                        + CurrencyUtil.toFullString(ItemPriceRecPacket.getLastReceivedSellPrice()),
                X(108), Y(23), 0x00E500
        );
    }

    /**
     * Draws the players balance onto the gui within the
     * information screen section.
     */
    protected void drawPlayerBalance(){
        drawCenteredString(font, CurrencyUtil.CURRENCY_SYMBOL + getBalance(),
                X(73), Y(23), 0x00E500
        );
    }

    // *****
    // Logic
    // *****

    /**
     * Gets the players last known balance from server.
     * Will also request a balance update if enough time
     * has elapsed.
     */
    protected static String getBalance(){
        PlayerEntity player = Minecraft.getInstance().player;
        if(player == null) //No player, no balance.
            return ERROR_MESSAGE;

        BALANCE_REQUEST_TIMER.time((x) -> {
            Packet.send(PacketDistributor.SERVER.noArg(),
                    new FullBalanceReqPacket(player.getUniqueID().toString())
            );
            return null;
        });

        return FullBalanceRecPacket.getLastKnownBalance() == null ?
                ERROR_MESSAGE : FullBalanceRecPacket.getLastKnownBalance();
    }

    /**
     * Checks to see the player has clicked outside of the gui
     * bounds.
     *
     * <p/>This has been extended to take the extended
     * {@link ShopperyInventoryScreen} bounds into account.
     *
     * @return {@code true} if the click was outside of the gui
     * bounds, {@code false} otherwise.
     */
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        if(super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton)){
            guiLeft = guiLeft - ( WIDTH_DIFF / 2 ) - ( getRecipeGui().isVisible() ? 77 : 0 );
            guiTop = guiTop - HEIGHT - 1;

            return mouseX < (double)guiLeft
                    || mouseY < (double) guiTop
                    || mouseX >= (double) (guiLeft + WIDTH)
                    || mouseY >= (double)(guiTop + HEIGHT);
        } else return false;
    }

    /**
     * Initializes and places the cash withdraw
     * buttons on the gui.
     */
    protected void initCashSection(){
        int beginX = relX + 144;
        int beginY = relY + 6;

        //Row 1
        this.addButton(new MoneyButton(beginX, beginY, COIN_ONE, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, COIN_FIVE, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, COIN_TEN, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, COIN_TWENTY, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, COIN_FIFTY, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, COIN_EIGHTY, player));

        //Row 2
        beginX = relX + 144; beginY += 18;

        this.addButton(new MoneyButton(beginX, beginY, NOTE_ONE, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_FIVE, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_TEN, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_TWENTY, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_FIFTY, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_ONE_HUNDRED, player));

        //Row 3
        beginX = relX + 144; beginY += 18;

        this.addButton(new MoneyButton(beginX, beginY, NOTE_FIVE_HUNDRED, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_ONE_K, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_FIVE_K, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_TEN_K, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_FIFTY_K, player));
        beginX += 18;
        this.addButton(new MoneyButton(beginX, beginY, NOTE_ONE_HUNDRED_K, player));
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
