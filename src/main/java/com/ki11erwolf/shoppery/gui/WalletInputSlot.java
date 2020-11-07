package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.item.ICurrencyItem;
import com.ki11erwolf.shoppery.packets.DepositCashPacket;
import com.ki11erwolf.shoppery.packets.DepositInventoryPacket;
import com.ki11erwolf.shoppery.packets.ItemPriceReqPacket;
import com.ki11erwolf.shoppery.packets.Packet;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A type of widget, which can be treated as a
 * {@link net.minecraft.client.gui.widget.button.Button},
 * that acts a <b>client side only</b> inventory slot
 * that saves a ghost (client side only) copy of the
 * given ItemStack.
 *
 * <p/>Server side interaction, such as getting prices and
 * depositing ItemStacks, is done through packets.
 */
@OnlyIn(Dist.CLIENT)
public class WalletInputSlot extends Widget implements WidgetFix {

    /**
     * The number of pixels to render of the image
     * in both X & Y directions.
     */
    private static final int SIZE = 18;//px

    /**
     * The client side player interacting
     * with this widget.
     */
    private final PlayerEntity player;

    /**
     * The ghost ItemStack contained within
     * this slot.
     */
    private ItemStack containedItem;

    /**
     * @param player the client side player viewing
     *               and interaction with the widget.
     * @param xBegin starting X position of the slot
     *               on screen.
     * @param yBegin starting Y position of the slot
     *               on screen.
     */
    public WalletInputSlot(PlayerEntity player, int xBegin, int yBegin) {
        super(xBegin, yBegin, 18, 18, new StringTextComponent(""));
        this.player = player;
    }

    // #########
    // Rendering
    // #########

    /**
     * Obfuscated {@link #render(MatrixStack, int, int, float)}.
     */
    @Override @ParametersAreNonnullByDefault
    public void func_230431_b_(MatrixStack matrix, int mouseXPos, int mouseYPos, float frameTime) {
        this.render(matrix, mouseXPos, mouseYPos, frameTime);
    }

    /**
     * Draws the widget on screen within the
     * current frame.
     *
     * @param mouseX mouse X position.
     * @param mouseY mouse Y position
     * @param renderTime the amount of time the
     *                   frame took to render.
     */
    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float renderTime) {
        renderBackgroundLayer(matrix, renderTime, mouseX, mouseY);
        renderContainedItem();
        renderTooltip(matrix, mouseX, mouseY);
    }

    /**
     * Handles rendering the {@link #containedItem}
     * on top of the widget.
     */
    private void renderContainedItem(){
        if(containedItem != null){
            renderItemStackAt(containedItem, getXPos() + 1, getYPos() + 1);
        }
    }

    /**
     * Handles rendering the correct background image
     * of this slot of screen.
     */
    private void renderBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        renderImage(
                matrix, WalletInventoryScreen.WALLET_GUI_TEXTURE, getXPos(), getYPos(),
                /* X */((WidgetFix.isHovered(this) || containedItem != null) ? 35 : 16),
                /* Y */65, SIZE, SIZE, 256, 256
        );
    }

    /**
     * Handles drawing the tooltip on the screen
     * if the widget is moused over.
     *
     * @param x mouse X position at time of render.
     * @param y mouse Y position at time of render.
     */
    private void renderTooltip(MatrixStack matrix, int x, int y) {
        if(!WalletInventoryScreen.ALLOW_TOOLTIPS) return;

        if(this.isHovered() || isOccupied()) {
            this.renderTooltip2(
                    matrix, Minecraft.getInstance().fontRenderer,
                    new StringTextComponent(LocaleDomains.TOOLTIP.sub(LocaleDomains.WIDGET).get("input_slot_1")),
                    this.getXPos() + (getWidth() - 66) + WalletInventoryScreen.COMPONENT_TOOLTIP_X_OFFSET,
                    this.getYPos() + ((getHeight() / 2) - 4) - 5,
                    0XF8F8F8
            );
        }

        if(this.isHovered() && !isOccupied()){
            this.renderTooltip2(
                    matrix, Minecraft.getInstance().fontRenderer,
                    new StringTextComponent(LocaleDomains.TOOLTIP.sub(LocaleDomains.WIDGET).get("input_slot_2")),
                    (this.getXPos() + getWidth() - 67) + (WalletInventoryScreen.COMPONENT_TOOLTIP_X_OFFSET - 8),
                    this.getYPos() + (getHeight() / 2) - 4 + 5,
                    0XF8F8F8
            );
        }
    }

    // ############
    // Action Event
    // ############

    /** Obfuscated {@link #onMouseAction(double, double, int)} */
    @Override public boolean func_231044_a_(double x, double y, int button) { return onMouseAction(x, y, button); }

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
    public boolean onMouseAction(double x, double y, int button) {
        if(isSelfClicked(x, y, button)){
            if (func_230992_c_(x, y)) { //If Hovered
                this.onClick(x, y, button);
                return true;
            }
        }

        return false;
    }

    /**
     * Called when the slot is clicked.
     *
     * <p/>Handles getting and containing the ItemStack
     * the player is holding.
     *
     * @param xClickPos the X position of the mouse when
     * the button was clicked.
     * @param yClickPos the Y position of the mouse when
     * the button was clicked.
     */
    protected void onClick(double xClickPos, double yClickPos, int button) {
        ItemStack heldStack = player.inventory.getItemStack().copy();

        if(button == 2) { //Inventory Deposit
            Packet.send(PacketDistributor.SERVER.noArg(),
                    new DepositInventoryPacket(player.getUniqueID().toString())
            );
            playDepositSound();
        } else if(heldStack.getItem() instanceof ICurrencyItem){ //Cash Deposit
            Packet.send(PacketDistributor.SERVER.noArg(),
                    new DepositCashPacket(player.getUniqueID().toString(), button == 0)
            );
            playDepositSound();

            if (button == 0)
                player.inventory.setItemStack(ItemStack.EMPTY);
            else
                player.inventory.getItemStack().shrink(1);

            if(containedItem != null)
                setContainedItem(null);
        } else if (button == 1){ //Clear Price Check
            setContainedItem(null);
        } else { //Price Check
            setContainedItem((heldStack == ItemStack.EMPTY || heldStack.getItem() == Items.AIR) ? null : heldStack);
            if(containedItem != null)
                ItemPriceReqPacket.send(containedItem.getItem().getRegistryName());
        }
    }

    /**
     * Effectively sets the ghost ItemStack held
     * within this slot.
     *
     * @param item the (new) ItemStack to contain.
     */
    private void setContainedItem(ItemStack item){
        if(item == null || item == ItemStack.EMPTY
                || item.getItem().getRegistryName() == null){
            containedItem = null;
            return;
        }

        this.containedItem = item;
    }

    /**
     * Called when a mouse button is released
     * over the widget.
     *
     * @param x mouse X position when clicked.
     * @param y mouse Y position when clicked.
     * @param button the mouse button id.
     * @return {@code true} if a supported button
     * was released over the button.
     */
    public boolean mouseReleased(double x, double y, int button) {
        if (button >= 0 && button < 3) {
            this.func_231047_b_(x, y); //this.onRelease(x, y);
            return true;
        } else {
            return false;
        }
    }

    // #####
    // Other
    // #####

    /**
     * Allows us to check if this slot is currently
     * holding an item. If it is, we know it's doing
     * a price check.
     *
     * @return {@code true} if and only if the slot
     * is holding an item, {@code false} otherwise.
     */
    public boolean isOccupied(){
        return containedItem != null;
    }

    /**
     * Plays the deposit sound effect and a
     * set pitch and volume.
     */
    private static void playDepositSound(){
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(ShopperySoundEvents.DEPOSIT,
                RandomUtils.nextFloat(0.8F, 1.0F), 0.10F
        ));
    }
}
