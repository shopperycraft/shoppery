package com.ki11erwolf.shoppery.container;

import com.ki11erwolf.shoppery.item.NoteItem;
import com.ki11erwolf.shoppery.network.packets.PRequestMoney;
import com.ki11erwolf.shoppery.network.packets.Packet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * A special slot that displays a note item
 * and requests the item when the slot is clicked.
 */
public class SlotNote extends SlotItemHandler {

    /**
     * Constructor.
     *
     * @param player the player who's viewing the slot.
     * @param note the note item to display.
     * @param index the index of this item.
     * @param relX the relative x position of the money gui.
     * @param relY the relative y position of the money gui.
     * @param xPosition x position of the slot.
     * @param yPosition y position of the slot.
     * @param balance the players current balance.
     */
    public SlotNote(EntityPlayer player, NoteItem note, int index, int relX, int relY,
                    int xPosition, int yPosition, long balance) {
        super(new Handler(player, note, balance), index, relX + xPosition, relY + yPosition);
    }

    /**
     * Requests the server put the note item in the players inventory
     * if they have enough funds.
     *
     * @param player the player.
     * @param item the coin item.
     */
    private static void onClick(EntityPlayer player, NoteItem item){
        Packet.send(
                PacketDistributor.SERVER.noArg(),
                new PRequestMoney(player.getUniqueID().toString(), true, item.getWorth())
        );
    }

    /**
     * The handler for this slot.
     */
    private static class Handler extends IItemHandlerButton {

        /**
         * The note item to display.
         */
        private final NoteItem item;

        /**
         * The current balance of the player.
         */
        private final long balance;

        /**
         * Constructor.
         *
         * @param player the player viewing the slot.
         * @param item the item to play in the slot.
         * @param balance the current balance of the player.
         */
        Handler(EntityPlayer player, NoteItem item, long balance){
            super(player);
            this.item = item;
            this.balance = balance;
        }

        /**
         * {@inheritDoc}
         *
         * @return the note item to display if the player
         * has enough funds, otherwise display no item.
         */
        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            if(item.getWorth() <= balance)          return new ItemStack(item);
            else                                    return ItemStack.EMPTY;
        }

        /**
         * Plays the money sound and calls the slots
         * static {@link SlotNote#onClick(EntityPlayer, NoteItem)}
         * method.
         *
         * @param player the player.
         */
        @Override
        void onClick(EntityPlayer player) {
            playMoneySound();
            SlotNote.onClick(player, item);
        }
    }
}
