package com.ki11erwolf.shoppery.container;

import com.ki11erwolf.shoppery.item.ShopperyItems;
import com.ki11erwolf.shoppery.network.packets.PRequestInventoryDeposit;
import com.ki11erwolf.shoppery.network.packets.Packet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Special slot that takes the players money
 * items in their inventory and adds the sum
 * total to their balance/wallet.
 */
public class SlotDeposit extends SlotItemHandler {

    /**
     * Constructor.
     *
     * @param player the player who's viewing the slot.
     * @param index the index of this item.
     * @param relX the relative x position of the money gui.
     * @param relY the relative y position of the money gui.
     * @param xPosition x position of the slot.
     * @param yPosition y position of the slot.
     */
    public SlotDeposit(EntityPlayer player, int index, int relX, int relY, int xPosition, int yPosition) {
        super(new Handler(player), index, relX + xPosition, relY + yPosition);
    }

    /**
     * Requests the server sum up the total of the money
     * in the players inventory and add it to their
     * balance/wallet.
     *
     * @param player the player.
     */
    private static void onClick(EntityPlayer player){
        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestInventoryDeposit(player.getUniqueID().toString())
        );
    }

    /**
     * Handler for this slot.
     */
    private static class Handler extends IItemHandlerButton {

        /**
         * Constructor.
         *
         * @param player the player viewing the slot.
         */
        Handler(EntityPlayer player){
            super(player);
        }

        /**
         * {@inheritDoc}
         *
         * @return the special {@link ShopperyItems#DEPOSIT_ITEM}.
         */
        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return new ItemStack(ShopperyItems.DEPOSIT_ITEM);
        }

        /**
         * Plays the button click sound
         * and calls the slots static
         * {@link SlotDeposit#onClick(EntityPlayer)}
         * method.
         *
         * @param player the player.
         */
        @Override
        void onClick(EntityPlayer player) {
            playButtonSound();
            SlotDeposit.onClick(player);
        }
    }
}
