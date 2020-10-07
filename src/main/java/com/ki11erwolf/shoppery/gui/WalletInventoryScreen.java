package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.config.ModConfig;
import com.ki11erwolf.shoppery.config.categories.General;
import com.ki11erwolf.shoppery.packets.FullBalanceRecPacket;
import com.ki11erwolf.shoppery.packets.FullBalanceReqPacket;
import com.ki11erwolf.shoppery.packets.ItemPriceRecPacket;
import com.ki11erwolf.shoppery.packets.Packet;
import com.ki11erwolf.shoppery.util.CurrencyUtil;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import com.ki11erwolf.shoppery.util.WaitTimer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.ki11erwolf.shoppery.item.ModItems.*;

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
public class WalletInventoryScreen extends InventoryScreen implements WidgetFix{

    /**
     * The texture map for the various GUI backgrounds
     * used by Shoppery.
     */
    public static final ResourceLocation WALLET_GUI_TEXTURES
            = new ResourceLocation("shoppery", "textures/gui/wallet_guis.png");

    /**
     * The amount of pixes to shift each tooltip on the x-axis.
     * Helps to line up tooltips with the rest of the widgets.
     */
    public static final int COMPONENT_TOOLTIP_X_OFFSET = - 29;

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
            = new WaitTimer(ModConfig.GENERAL_CONFIG.getCategory(General.class).getPacketWaitTime());

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
    private WalletInputSlot inputSlot;

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
    public WalletInventoryScreen(PlayerEntity player) {
        super(player);
        this.player = player;
    }

    /** Obfuscated {@link #init()}. */
    @Override protected void func_231160_c_() {
        super.func_231160_c_();
        init();
    }

    /**
     * {@inheritDoc}
     * Constructs and initializes the gui screen
     * and all sub components.
     */
    protected void init() {
        calculateOriginPosition();
        initCashSection();
        this.addButton(new WalletWikiButton(relX, relY));
        this.addButton((this.inputSlot = new WalletInputSlot(player, relX + 122, relY + 36)));
    }

    /** Obfuscated {@link #render(MatrixStack, int, int, float)}. */
    @Override @ParametersAreNonnullByDefault
    public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float ticks) {
        render(matrix, mouseX, mouseY, ticks);
        super.func_230430_a_(matrix, mouseX, mouseY, ticks);
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
     * @param mouseXPos mouseX
     * @param mouseYPos mouseY
     * @param frameTime frame render time
     */
    @Override
    public void render(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
        calculateOriginPosition();
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
        boolean isRecipeBookOpen;

        try { isRecipeBookOpen = getRecipeGui().isVisible(); }
        catch (ReportedException | NullPointerException e){
            isRecipeBookOpen = false;
        }

        //Centers Money section atop the inventory sections, recipe book included.
        this.relX = guiLeft - ( WIDTH_DIFF / 2 ) - ( isRecipeBookOpen ? 77 : 0 );
        this.relY = guiTop - HEIGHT - 1;//-1 for spacing.

        this.trueX = (isRecipeBookOpen ? -77 : 0) + (this.xSize / 2) - (WIDTH / 2);
        this.trueY = -4 - HEIGHT;
    }

    // **********
    // Background
    // **********

    /** Obfuscated {@link #renderBackgroundLayer(MatrixStack, float, int, int)} */
    @Override @ParametersAreNonnullByDefault
    protected void func_230450_a_(MatrixStack matrix, float ticks, int mouseXPos, int mouseYPos) {
        super.func_230450_a_(matrix, ticks, mouseXPos, mouseXPos);
        renderBackgroundLayer(matrix, ticks, mouseXPos, mouseYPos);
    }

    /**
     * Draws the background image of the money section of
     * the gui. The image is always centered atop the normal gui.
     */
    protected void renderBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        renderImage(
                matrix, WALLET_GUI_TEXTURES, relX, relY, 0, 0,
                WIDTH, HEIGHT, 256, 256
        );
    }

    // **********
    // Foreground
    // **********

    /** Obfuscated {@link #drawGuiContainerForegroundLayer(MatrixStack, int, int)} */
    @Override @ParametersAreNonnullByDefault
    protected void func_230451_b_(MatrixStack stack, int mouseX, int mouseY) {
        super.func_230451_b_(stack, mouseX, mouseY);
        drawGuiContainerForegroundLayer(stack, mouseX, mouseY);
    }

    /**
     * {@inheritDoc}
     */
    protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY) {
        drawTitles(matrix);

        //Info screen: balance/prices
        if(inputSlot.isOccupied()) drawItemPrices(matrix);
        else drawPlayerBalance(matrix);
    }

    /**
     * Handles rendering the GUIs title(s)
     * and other static text.
     */
    protected void drawTitles(MatrixStack matrix){
        FontRenderer fr = this.field_230712_o_;

        if(inputSlot.isOccupied()){
            renderTooltip2(matrix, fr, new StringTextComponent(
                    LocaleDomains.TEXT.sub(LocaleDomains.SCREEN).get("buy")
            ), X(24), Y(7), 0xff1212);

            renderTooltip2(matrix, fr, new StringTextComponent(
                    LocaleDomains.TEXT.sub(LocaleDomains.SCREEN).get("sell")
            ), X(81), Y(7), 0x23ff17);
        } else {
            renderTooltip2(matrix, fr, new StringTextComponent(
                    LocaleDomains.TITLE.sub(LocaleDomains.SCREEN).format("wallet", player.getDisplayName().getString())
            ), X(5), Y(7), 0x42ecf5);
        }
    }

    /**
     * Draws the requested item prices onto the gui
     * within the information screen section. Also
     * draws the buy and sell headers atop the
     * information screen.
     */
    protected void drawItemPrices(MatrixStack matrix){
        if(!ItemPriceRecPacket.doesLastReceivedHavePrice()){
            func_238471_a_(matrix, field_230712_o_,
                    LocaleDomains.TEXT.sub(LocaleDomains.SCREEN).get("no_price"), X(73), Y(23), 0x9C1313
            );
            return;
        }

        func_238471_a_( matrix, field_230712_o_, CurrencyUtil.CURRENCY_SYMBOL
                        + CurrencyUtil.toFullString(ItemPriceRecPacket.getLastReceivedBuyPrice()),
                X(38), Y(23), 0xD11F1F);

        func_238471_a_(matrix, field_230712_o_, CurrencyUtil.CURRENCY_SYMBOL
                        + CurrencyUtil.toFullString(ItemPriceRecPacket.getLastReceivedSellPrice()),
                X(108), Y(23), 0x00E500);
    }

    /**
     * Draws the players balance onto the gui within the
     * information screen section.
     */
    protected void drawPlayerBalance(MatrixStack matrix){
        func_238471_a_(
                matrix, field_230712_o_,
                CurrencyUtil.CURRENCY_SYMBOL + getBalance(),
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
     * {@link WalletInventoryScreen} bounds into account.
     *
     * @return {@code true} if the click was outside of the gui
     * bounds, {@code false} otherwise.
     */
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        if(!super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton)) return false;

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
    @SuppressWarnings("DuplicatedCode")
    protected void initCashSection(){
        int beginX = relX + 144;
        int beginY = relY + 6;

        //Row 1
        this.addButton(new WalletMoneySlot(beginX, beginY, COIN_ONE, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, COIN_FIVE, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, COIN_TEN, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, COIN_TWENTY, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, COIN_FIFTY, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, COIN_EIGHTY, player));

        //Row 2
        beginX = relX + 144; beginY += 18;

        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_ONE, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_FIVE, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_TEN, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_TWENTY, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_FIFTY, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_ONE_HUNDRED, player));

        //Row 3
        beginX = relX + 144; beginY += 18;

        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_FIVE_HUNDRED, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_ONE_K, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_FIVE_K, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_TEN_K, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_FIFTY_K, player));
        beginX += 18;
        this.addButton(new WalletMoneySlot(beginX, beginY, NOTE_ONE_HUNDRED_K, player));
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
        return Minecraft.getInstance().currentScreen instanceof WalletInventoryScreen;
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

    /**
     * Adds a button, as a widget, to this screens list of components,
     * so that it may be displayed on screen.
     *
     * @param button the button widget to add to the screen.
     */
    protected void addButton(Widget button){ this.func_230480_a_(button); }
}
