package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.network.packets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Inventory GUI with an added Money gui that allows
 * the player to add/take money from their
 * {@link com.ki11erwolf.shoppery.bank.Wallet} through
 * the gui. This GUI acts like an extension to the inventory
 * GUI (like the recipe book) however it is a separate
 * GUI extending the original inventory GUI.
 */
public class MoneyGui extends GuiInventory {

    /**
     * The texture file containing the money gui
     * texture.
     */
    private static final ResourceLocation MONEY_GUI_TEXTURE
            = new ResourceLocation(ShopperyMod.MODID, "textures/gui/money_gui.png");

    /**
     * The height of the money gui texture in pixels.
     * NOT the size of the image file.
     */
    private static final int MONEY_GUI_HEIGHT = 68;

    /**
     * The width of the money gui texture in pixels.
     * NOT the size of the image file.
     */
    private static final int MONEY_GUI_WIDTH = 256;

    /**
     * The time when the last balance request
     * packet was sent. Used to prevent
     * sending packets in quick succession.
     */
    private static long lastPktSendTime = -1;

    /**
     * The player this gui instance belongs to.
     */
    private final EntityPlayer player;

    private final MoneyGuiSlotHandler slotHandler;

    private boolean last;

    /**
     * The x position of the money gui section relative
     * to the 0 position (top left of screen).
     */
    private int moneyGuiX;

    /**
     * The y position of the money gui section relative
     * to the 0 position (top left of screen).
     */
    private int moneyGuiY;

    /**
     * The x position of the money gui section relative
     * to the 0 position of the normal inventory gui
     * (top left corner of the inventory gui).
     */
    private int relMoneyGuiX;

    /**
     * The y position of the money gui section relative
     * to the 0 position of the normal inventory gui
     * (top left corner of the inventory gui).
     */
    private int relMoneyGuiY;

    /**
     * Constructs a new money inventory.
     *
     * @param player the player the gui belongs to.
     */
    MoneyGui(EntityPlayer player){
        super(player);

        this.slotHandler = new MoneyGuiSlotHandler(inventorySlots, player);
        this.player = player;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
    }

    private long lastPktSendTime2 = -1;

    /**
     * {@inheritDoc}
     *
     * Draws the money part of the gui background.
     * This method also calculates the new x/y positions
     * that accommodate the extra space taken by the
     * money gui.
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.mc.getTextureManager().bindTexture(MONEY_GUI_TEXTURE);

        if(System.currentTimeMillis() > lastPktSendTime2 + 250 /*Time between*/ || lastPktSendTime2 == -1){
            last = !last;
            lastPktSendTime2 = System.currentTimeMillis();
        }

        if(func_194310_f().isVisible()){
            if(!last){
                slotHandler.removeSlots();
            }

            this.drawTexturedModalRect(
                    this.guiLeft + 10 - (MONEY_GUI_WIDTH / 2),
                    this.guiTop - MONEY_GUI_HEIGHT - 4,
                    0, 0,
                    MONEY_GUI_WIDTH, MONEY_GUI_HEIGHT
            );

            last = true;
        } else {
            if(last){
                slotHandler.removeSlots();
            }

            this.drawTexturedModalRect(
                    this.guiLeft + (this.xSize / 2) - (MONEY_GUI_WIDTH / 2),
                    this.guiTop - MONEY_GUI_HEIGHT - 4,
                    0, 0,
                    MONEY_GUI_WIDTH, MONEY_GUI_HEIGHT
            );

            last = false;
        }

        calculatePositions();
        slotHandler.addSlots(relMoneyGuiX, relMoneyGuiY);
    }

    /**
     * {@inheritDoc}
     *
     * Draws the text over the gui (mod name & full player balance).
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        this.mc.fontRenderer.drawString("ShopperyCraft",
                this.relMoneyGuiX + 5, this.relMoneyGuiY + 5,
                4210752
        );

        this.mc.fontRenderer.drawString(getBalance(),
                this.relMoneyGuiX + 31, this.relMoneyGuiY + 31,
                4210752
        );
    }

    private void calculatePositions(){
        if(func_194310_f().isVisible()){
            this.moneyGuiX = this.guiLeft + 10 - (MONEY_GUI_WIDTH / 2);
            this.relMoneyGuiX = 0 - 78 + (this.xSize / 2) - (MONEY_GUI_WIDTH / 2);
        } else {
            this.moneyGuiX = this.guiLeft + (this.xSize / 2) - (MONEY_GUI_WIDTH / 2);
            this.relMoneyGuiX = (this.xSize / 2) - (MONEY_GUI_WIDTH / 2);
        }

        this.moneyGuiY = this.guiTop - MONEY_GUI_HEIGHT - 4;
        this.relMoneyGuiY = 0 - 4 - MONEY_GUI_HEIGHT;
    }

    /**
     * @return the textual formatted full balance of the
     * player viewing the button. Also handles requesting
     * the balance from the server.
     */
    protected static String getBalance(){
        EntityPlayer player = Minecraft.getInstance().player;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        //Timer to prevent spamming the server every gui redraw.
        if(System.currentTimeMillis() > lastPktSendTime + 500 /*Time between*/ || lastPktSendTime == -1){
            Packet.send(
                    PacketDistributor.SERVER.noArg(),
                    new PRequestFullPlayerBalance(player.getUniqueID().toString())
            );
            lastPktSendTime = System.currentTimeMillis();
        }

        return PReceiveFullPlayerBalance.getLastKnownBalance() == null ?
                "<ERROR>" : PReceiveFullPlayerBalance.getLastKnownBalance();
    }

    @Override
    public void onGuiClosed() {
        slotHandler.removeSlots();
        super.onGuiClosed();
    }
}