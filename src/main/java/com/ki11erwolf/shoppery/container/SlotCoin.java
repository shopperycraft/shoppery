package com.ki11erwolf.shoppery.container;

import com.ki11erwolf.shoppery.item.CoinItem;
import com.ki11erwolf.shoppery.network.packets.PRequestMoney;
import com.ki11erwolf.shoppery.network.packets.Packet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * A special slot that displays a coin item
 * and requests the item when the slot is clicked.
 */
public class SlotCoin extends SlotItemHandler {

    /**
     * Constructor.
     *
     * @param player the player who's viewing the slot.
     * @param coin the coin item to display.
     * @param index the index of this item.
     * @param relX the relative x position of the money gui.
     * @param relY the relative y position of the money gui.
     * @param xPosition x position of the slot.
     * @param yPosition y position of the slot.
     * @param balance the players current balance.
     * @param cents the players current cents balance.
     */
    public SlotCoin(EntityPlayer player, CoinItem coin, int index, int relX, int relY, int xPosition, int yPosition,
                    long balance, byte cents) {
        super(new Handler(player, coin, balance, cents), index, relX + xPosition, relY + yPosition);
    }

    /**
     * Requests the server put the coin item in the players inventory
     * if they have enough funds.
     *
     * @param player the player.
     * @param item the coin item.
     */
    private static void onClick(EntityPlayer player, CoinItem item){
        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestMoney(player.getUniqueID().toString(), false, item.getWorth())
        );
    }

    /**
     * The handler for this slot.
     */
    private static class Handler extends IItemHandlerButton {

        /**
         * The item the slot displays.
         */
        private final CoinItem item;

        /**
         * The current balance of the player.
         */
        private final long balance;

        /**
         * The current cents balance of the player.
         */
        private final byte cents;

        /**
         * Constructor.
         *
         * @param player the player viewing the slot.
         * @param item the item being displayed.
         * @param balance the current players balance.
         * @param cents the current players cents balance.
         */
        Handler(EntityPlayer player, CoinItem item, long balance, byte cents){
            super(player);
            this.item = item;
            this.balance = balance;
            this.cents = cents;
        }

        /**
         * {@inheritDoc}
         *
         * @return the coin item to display if the player
         * has enough funds, otherwise display no item.
         */
        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            if(balance > 0)                         return new ItemStack(item);
            else if(item.getWorth() <= cents)       return new ItemStack(item);
            else                                    return ItemStack.EMPTY;
        }

        /**
         * Plays the money sound and calls the slots
         * static {@link SlotCoin#onClick(EntityPlayer, CoinItem)}
         * method.
         *
         * @param player the player.
         */
        @Override
        void onClick(EntityPlayer player) {
            playMoneySound();
            SlotCoin.onClick(player, item);
        }
    }
}
