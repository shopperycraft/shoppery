package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.network.packets.PReceivePlayerBalance;
import com.ki11erwolf.shoppery.network.packets.PRequestPlayerBalance;
import com.ki11erwolf.shoppery.network.packets.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * The button displayed in the players
 * inventory that shows the players balance
 * and opens the Shoppery GUI.
 *
 * Written with a lot of help from the Baubles
 * Button Class:
 * https://github.com/Azanor/Baubles/blob/master/src/main/java/baubles/client/gui/GuiBaublesButton.java
 */
@OnlyIn(Dist.CLIENT)
public class ShopperyButton extends GuiButtonImage {

    /**
     * Texture file for the bank button.
     */
    private static final ResourceLocation BANK_BUTTON_TEXTURE
            = new ResourceLocation(ShopperyMod.MODID, "textures/gui/shoppery_button.png");

    /**
     * The time when the last balance request
     * packet was sent. Used to prevent
     * sending packets in quick succession.
     */
    private static long lastPktSendTime = -1;

    /**
     * Latest instance of the shoppery button to be
     * created. Used to "destroy" duplicate instances.
     */
    private static ShopperyButton CURRENT_BUTTON = null;

    /**
     * The gui this button is attached to.
     */
    private final GuiContainer parent;

    /**
     * Check if the mouse is hovering over the button
     * AND if it is pressable.
     */
    private boolean isHovering;

    /**
     * Flag used to check if this
     * button is still in use. Only
     * one ShopperyButton may be in use
     * for a player.
     */
    private boolean isDestroyed = false;

    /**
     * Number of times the left click
     * mouse button has been pressed
     * over the button. Used to filter
     * out double-presses.
     */
    private int presses = 0;

    /*
     * Registers this class to the forge event bus.
     */
    static{
        MinecraftForge.EVENT_BUS.register(GuiEventHandler.INSTANCE);
    }

    /**
     * Initializes this class. This sets it
     * up to put the button on the player
     * inventory gui.
     */
    //Dummy method that allows us to statically
    //load the class.
    @OnlyIn(Dist.CLIENT)
    public static void init(){}

    /**
     * Creates a new shoppery button.
     *
     * @param buttonId the unique ID for the button.
     * @param x x position relative to the parent window.
     * @param y y position relative to the parent window.
     * @param width button width.
     * @param height button height.
     */
    private ShopperyButton(ResourceLocation texture, GuiContainer parent, int buttonId,
                           int x, int y, int width, int height, int offset) {
        super(buttonId, x, y, width, height, 0, 0, offset, texture);
        this.parent = parent;
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Called when the user clicks the button.
     *
     * @param mouseX mouse x position.
     * @param mouseY mouse y position.
     */
    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        ShopperyMod.getNewLogger().info("Button Clicked!");
        this.playPressSound(Minecraft.getInstance().getSoundHandler());

        if(parent instanceof MoneyGui){
            parent.close();
            this.destroy();

            GuiInventory guiInventory = new GuiInventory(Minecraft.getInstance().player);
            Minecraft.getInstance().displayGuiScreen(guiInventory);
        } else {
            parent.close();
            this.destroy();

            MoneyGui guiMoney = new MoneyGui(Minecraft.getInstance().player);
            Minecraft.getInstance().displayGuiScreen(guiMoney);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        int oldX = this.x;
        int oldY = this.y;

        this.x = parent.getGuiLeft() + this.x;
        this.y = (parent.height / 2) - this.y;

        super.render(mouseX, mouseY, partialTicks);

        if(this.visible){
            this.drawCenteredString(
                    Minecraft.getInstance().fontRenderer, getBalance(),
                    x + 22, y + (this.height / 3) - 1, 0xffffff
            );

            this.hovered = mouseX >= x && mouseY >= this.y && mouseX < x + this.width
                    && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            if(k != 1)
                isHovering = hovered;
            else
                isHovering = false;
        } else {
            isHovering = false;
        }

        this.x = oldX;
        this.y = oldY;
    }

    /**
     * Forge hook to subscribe to mouse events.
     *
     * Used to call this buttons {@link #onClick(double, double)}
     * method when the mouse is hovering over it
     * as it isn't called by default
     *
     * @param event forge mouse event.
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(InputEvent.MouseInputEvent event) {
        if(presses == 0 || presses == 2){
            if(event.getButton() == 0 && isHovering && visible && !isDestroyed &&
                    Minecraft.getInstance().currentScreen instanceof GuiInventory)
                this.onClick(
                        Minecraft.getInstance().mouseHelper.getMouseX(),
                        Minecraft.getInstance().mouseHelper.getMouseY()
                );
            presses = 0;
        }

        presses++;
    }

    /**
     * Call to tell this button
     * to it's no longer needed
     * and must stop working.
     */
    private void destroy(){
        MinecraftForge.EVENT_BUS.unregister(CURRENT_BUTTON);
        this.visible = false;
        this.isDestroyed = true;
    }

    /**
     * @return the textual formatted balance of the
     * player viewing the button. Also handles requesting
     * the balance from the server.
     */
    private static String getBalance(){
        EntityPlayer player = Minecraft.getInstance().player;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        //Timer to prevent spamming the server every gui redraw.
        if(System.currentTimeMillis() > lastPktSendTime + 1000 /*Time between*/ || lastPktSendTime == -1){
            Packet.send(PacketDistributor.SERVER.noArg(), new PRequestPlayerBalance(player.getUniqueID().toString()));
            lastPktSendTime = System.currentTimeMillis();
        }

        return PReceivePlayerBalance.getLastKnownBalance() == null ?
                "<ERROR>" : PReceivePlayerBalance.getLastKnownBalance();
    }

    /**
     * Singleton enum class that is registered
     * to the forge event bus.
     */
    private enum GuiEventHandler {

        /**
         * Singleton instance.
         */
        INSTANCE;

        /**
         * Called when a gui has finished being
         * initialized.
         *
         * Adds the Shoppery Button to the
         * players inventory gui.
         *
         * @param event forge event.
         */
        @SubscribeEvent
        public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
            if (event.getGui() instanceof GuiInventory) {
                GuiContainer gui = (GuiContainer) event.getGui();

                ShopperyButton button = new ShopperyButton(
                        BANK_BUTTON_TEXTURE, gui, 82, 125, 22, 46, 18, 19
                );

                if(CURRENT_BUTTON != null)
                    CURRENT_BUTTON.destroy();

                event.addButton(
                   button
                );
                event.removeButton(CURRENT_BUTTON);
                CURRENT_BUTTON = button;
            }
        }

    }
}
