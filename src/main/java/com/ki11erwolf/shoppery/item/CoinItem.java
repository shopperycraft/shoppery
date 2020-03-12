package com.ki11erwolf.shoppery.item;

import com.ki11erwolf.shoppery.bank.BankManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Item class for any type of coin.
 *
 * Shoppery notes & coins are usually referred to
 * as Dollars to give simple, well known name
 * to the currency.
 */
public class CoinItem extends ShopperyItem<CoinItem> {

    /**
     * Prefix for all coin item types.
     */
    private static final String ITEM_NAME_PREFIX = "coin_";

    /**
     * The worth/worth (in cents) of this coin (0 < worth <= 100).
     */
    private byte worth;

    /**
     * {@inheritDoc}
     *
     * @param coinName the name of the coin
     *                 type.
     * @param worth the worth (in cents) of this coin (0 < worth <= 100).
     */
    CoinItem(String coinName, byte worth) {
        super(new Properties(), ITEM_NAME_PREFIX + coinName);

        if(worth < 0 || worth > 100)
            throw new IllegalArgumentException("Value out of range (0 < worth <= 100): " + worth);

        this.worth = worth;
    }

    /**
     * @return The worth/worth (in cents) of this coin (0 < worth <= 100).
     */
    public byte getWorth(){
        return this.worth;
    }

    /**
     * {@inheritDoc}
     *
     * Adds the amount of money this coin is worth
     * to the players {@link com.ki11erwolf.shoppery.bank.Wallet}.
     */
    @Override
    @SuppressWarnings("NullableProblems")
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if(world.isRemote)
            return super.onItemRightClick(world, player, hand);

        if(player.isShiftKeyDown() && BankManager._getWallet(world, player).subtract(0, this.worth)){
            //Always increase stack size to indicate success.
            if(player.getHeldItem(hand).getCount() >= 64)
                player.addItemStackToInventory(new ItemStack(player.getHeldItem(hand).getItem(), 1));
            else
                player.getHeldItem(hand).setCount(player.getHeldItem(hand).getCount() + 1);
        } else {
            BankManager._getWallet(world, player).add(0, this.worth);
            //Always decrease stack size to indicate success.
            player.getHeldItem(hand).shrink(1);
        }


        return super.onItemRightClick(world, player, hand);
    }

}
