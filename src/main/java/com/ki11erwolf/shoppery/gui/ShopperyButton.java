package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * The button displayed in the players
 * inventory that shows the players balance
 * and opens the Shoppery GUI.
 *
 * Written with a lot of help from the Baubles
 * Button Class:
 * https://github.com/Azanor/Baubles/blob/master/src/main/java/baubles/client/gui/GuiBaublesButton.java
 */
public class ShopperyButton extends GuiButtonImage {

    /**
     * Texture file for the bank button.
     */
    private static final ResourceLocation BANK_BUTTON_TEXTURE
            = new ResourceLocation(ShopperyMod.MODID, "textures/gui/shoppery_button.png");

    /**
     * The gui this button is attached to.
     */
    private final GuiContainer parent;

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
    }

    /**
     * Called when the user clicks the button.
     *
     * @param mouseX mouse x position.
     * @param mouseY mouse y position.
     */
    @Override
    public void onClick(double mouseX, double mouseY) {
        ShopperyMod.getNewLogger().info("Button Clicked!");
        super.onClick(mouseX, mouseY);
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

        this.drawCenteredString(
            Minecraft.getInstance().fontRenderer, getBalance(),
                x + 22, y + (this.height / 3) - 1, 0xffffff
        );

        this.x = oldX;
        this.y = oldY;
    }

    /**
     * @return the textual formatted balance of the
     * player viewing the button.
     */
    private static String getBalance(){
        EntityPlayer player = Minecraft.getInstance().player;
        return "$10.00";
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
                List<GuiButton> list = getBackingList(event.getButtonList());

                if(list == null)
                    throw new IllegalStateException("Cannot add shoppery button. List is null");

                list.add(
                    new ShopperyButton(
                            BANK_BUTTON_TEXTURE, gui, 82, 125, 22, 46, 18, 19
                    )
                );
            }
        }

        /**
         * Allows getting the backing (editable) list of an
         * {@link Collections.UnmodifiableRandomAccessList} through reflection.
         *
         * @param list the given {@link Collections.UnmodifiableRandomAccessList}.
         * @return the backing editable {@link List}.
         */
        @SuppressWarnings("JavadocReference")
        private static <T> List<T> getBackingList(List<T> list){
            try {
                Field f = list.getClass().getSuperclass().getDeclaredField("list");
                f.setAccessible(true);
                //Should always return the same object.
                //noinspection unchecked
                return (List<T>)f.get(list);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                ShopperyMod.getNewLogger().error("Failed to get button list", e);
            }

            return null;
        }
    }
}
