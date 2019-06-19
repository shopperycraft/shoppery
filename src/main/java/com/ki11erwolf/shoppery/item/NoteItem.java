package com.ki11erwolf.shoppery.item;

import com.ki11erwolf.shoppery.bank.BankManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Item class for every type of note.
 *
 * Shoppery notes & coins are usually referred to
 * as Dollars to a give simple, well known name
 * to the currency.
 */
public class NoteItem extends ShopperyItem<NoteItem> {

    /**
     * Prefix for the name of every note item.
     */
    private static final String ITEM_NAME_PREFIX = "note_";

    /**
     * How much money this note is worth (worth > 0)
     */
    private final int worth;

    /**
     * Package private constructor to prevent
     * item instance creation from outside
     * packages.
     *
     * @param noteName the name of the specific
     *                 note (e.g. note_fifty)
     * @param worth how much money the note
     *              is worth (worth > 0).
     */
    NoteItem(String noteName, int worth) {
        super(new Properties(), ITEM_NAME_PREFIX + noteName);

        if(worth < 0)
            throw new IllegalArgumentException("Invalid worth of note (worth < 0). Worth: " + worth);

        this.worth = worth;
    }

    /**
     * @return how much money this note is worth (worth > 0)
     */
    public int getWorth() {
        return worth;
    }

    /**
     * {@inheritDoc}
     *
     * Adds the amount of money this note is worth
     * to the players {@link com.ki11erwolf.shoppery.bank.Wallet}.
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if(world.isRemote)
            return super.onItemRightClick(world, player, hand);

        if(player.isSneaking() && BankManager._getWallet(world, player).subtract(this.worth)){
            //Always increase stack size to indicate success.
            if(player.getHeldItem(hand).getCount() >= 64)
                player.addItemStackToInventory(new ItemStack(player.getHeldItem(hand).getItem(), 1));
            else
                player.getHeldItem(hand).setCount(player.getHeldItem(hand).getCount() + 1);
        } else {
            BankManager._getWallet(world, player).add(this.worth);
            //Always decrease stack size to indicate success.
            player.getHeldItem(hand).shrink(1);
        }


        return super.onItemRightClick(world, player, hand);
    }

}