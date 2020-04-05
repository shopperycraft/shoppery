package com.ki11erwolf.shoppery.gui;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.item.CurrencyItem;
import com.ki11erwolf.shoppery.packets.DepositCashPacket;
import com.ki11erwolf.shoppery.packets.DepositInventoryPacket;
import com.ki11erwolf.shoppery.packets.ItemPriceReqPacket;
import com.ki11erwolf.shoppery.packets.Packet;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.RandomUtils;

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
public class InputSlot extends Widget {

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
    public InputSlot(PlayerEntity player, int xBegin, int yBegin) {
        super(xBegin, yBegin, 18, 18, "");
        this.player = player;
    }

    /**
     * Called when the slot is clicked.
     *
     * <p/>Handles getting and containing
     * the ItemStack the player is holding.
     *
     * @param xClickPos the X position of the
     *                  mouse when the button
     *                  was clicked.
     * @param yClickPos the Y position of the
     *                  mouse when the button
     *                  was clicked.
     */
    protected void onClick(double xClickPos, double yClickPos, int button) {
        ItemStack heldStack = player.inventory.getItemStack().copy();

        if(button == 2) {
            //Inventory Deposit
            Packet.send(PacketDistributor.SERVER.noArg(),
                    new DepositInventoryPacket(player.getUniqueID().toString())
            );

            playDepositSound();

        } else if(heldStack.getItem() instanceof CurrencyItem){
            //Cash Deposit
            Packet.send(PacketDistributor.SERVER.noArg(),
                    new DepositCashPacket(player.getUniqueID().toString(), button == 0)
            );

            playDepositSound();

            if (button == 0) player.inventory.setItemStack(ItemStack.EMPTY);
            else player.inventory.getItemStack().shrink(1);

            if(containedItem != null)
                setContainedItem(null);
        } else if (button == 1){
            //Clear Price Check
            setContainedItem(null);
        } else {
            //Price Check
            setContainedItem(
                    (heldStack == ItemStack.EMPTY || heldStack.getItem() == Items.AIR)
                            ? null : heldStack
            );

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
     * Called when a mouse action is performed
     * over the widget. Handles invoking a
     * click if conditions are met.
     *
     * @param x mouse X position when clicked.
     * @param y mouse Y position when clicked.
     * @param button the mouse button id.
     * @return {@code true} if a clicked event
     * was raised (effectively clicked).
     */
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.active && this.visible) {
            if (button >= 0 && button < 3) {
                boolean flag = this.clicked(x, y);
                if (flag) {
                    this.onClick(x, y, button);
                    return true;
                }
            }

        }
        return false;
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
            this.onRelease(x, y);
            return true;
        } else {
            return false;
        }
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
    public void renderButton(int mouseX, int mouseY, float renderTime) {
        renderBackground();
        renderContainedItem();
        renderTooltip(mouseX, mouseY);
    }

    /**
     * Handles rendering the {@link #containedItem}
     * on top of the widget.
     */
    private void renderContainedItem(){
        if(containedItem != null){
            drawItemStack(containedItem, x + 1, y + 1);
        }
    }

    /**
     * Copied from {@link net.minecraft.client.gui.screen.inventory.ContainerScreen}.
     *
     * <p/>Used to draw any item or block, in a stack, on the screen.
     *
     * @param stack the item or block to draw.
     * @param x the X position at which to start drawing.
     * @param y the Y position at which to start drawing.
     */
    private void drawItemStack(ItemStack stack, int x, int y) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
        this.setBlitOffset(200);
        itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.setBlitOffset(0);
        itemRenderer.zLevel = 0.0F;
    }

    /**
     * Handles rendering the correct background image
     * of this slot of screen.
     */
    private void renderBackground(){
        Minecraft.getInstance().getTextureManager().bindTexture(ShopperyInventoryScreen.SHOPPERY_GUIS);
        RenderSystem.disableDepthTest();
        blit(x, y, (
                (isHovered || containedItem != null) ? 35 : 16
        ), 65, 18, 18);
        RenderSystem.enableDepthTest();
    }

    /**
     * Handles drawing the tooltip on the screen
     * if the widget is moused over.
     *
     * @param x mouse X position at time of render.
     * @param y mouse Y position at time of render.
     */
    private void renderTooltip(int x, int y) {
        if(isHovered || isOccupied()) {
            drawCenteredString(Minecraft.getInstance().fontRenderer,
                    LocaleDomains.TOOLTIP.sub(LocaleDomains.WIDGET).get("input_slot_1"),
                    this.x + (width - 67),
                    this.y + ((height / 2) - 4) - 5,
                    0XF8F8F8
            );
        }
        if(isHovered && !isOccupied()){
            drawCenteredString(Minecraft.getInstance().fontRenderer,
                    LocaleDomains.TOOLTIP.sub(LocaleDomains.WIDGET).get("input_slot_2"),
                    this.x + (width - 67),
                    this.y + ((height / 2) - 4) + 5,
                    0XF8F8F8
            );
        }
    }

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
