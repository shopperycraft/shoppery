package com.ki11erwolf.shoppery.container;

import com.ki11erwolf.shoppery.ShopperySoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * A special handler class for {@link net.minecraftforge.items.SlotItemHandler}s
 * that essentially turns slots into buttons with an item texture.
 */
abstract class IItemHandlerButton implements IItemHandlerModifiable {

    /**
     * The player who's viewing/clicking the button.
     */
    protected EntityPlayer player;

    /**
     * Protected constructor.
     *
     * @param player The player who's viewing/clicking the button.
     */
    IItemHandlerButton(EntityPlayer player){
        this.player = player;
    }

    /**
     * {@inheritDoc}
     *
     * Does nothing.
     */
    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        //NO-OP
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code 1}. Only one slot per handler.
     */
    @Override
    public int getSlots() {
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * Intercepts the insert and calls the {@link #onClick(EntityPlayer)}
     * method instead.
     *
     * @return {@link ItemStack#EMPTY} - prevents inserts.
     */
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(!GuiScreen.isShiftKeyDown())
            onClick(player);
        return ItemStack.EMPTY;
    }

    /**
     * {@inheritDoc}
     *
     * Intercepts the extract and calls the {@link #onClick(EntityPlayer)}
     * method instead.
     *
     * @return {@link ItemStack#EMPTY} - prevents extracts.
     */
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(!GuiScreen.isShiftKeyDown())
            onClick(player);
        return ItemStack.EMPTY;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code 1}. Only one slot per handler.
     */
    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code false}. Slot should not be interactable.
     */
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }

    /**
     * Called when the player clicks over slot regardless
     * of item in hand. This method is not called on
     * shift-clicks.
     *
     * @param player the player.
     */
    abstract void onClick(EntityPlayer player);

    /**
     * Plays the default minecraft button sound.
     */
    @OnlyIn(Dist.CLIENT)
    static void playButtonSound(){
        Minecraft.getInstance().getSoundHandler().play(
                SimpleSound.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F)
        );
    }

    /**
     * Plays the Shoppery money sound.
     */
    @OnlyIn(Dist.CLIENT)
    static void playMoneySound(){
        Minecraft.getInstance().getSoundHandler().play(
                SimpleSound.getMasterRecord(ShopperySoundEvents.MONEY, 1.0F)
        );
    }
}
