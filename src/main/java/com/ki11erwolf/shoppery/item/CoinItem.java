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
public class CoinItem extends ShopperyItem<CoinItem> implements CurrencyItem{

    /**
     * Prefix for all coin item types.
     */
    private static final String ITEM_NAME_PREFIX = "coin_";

    /**
     * The worth/worth (in cents) of this coin (0 < worth <= 100).
     */
    private final byte worth;

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

        if(player.isSneaking() && BankManager._getWallet(world, player).subtract(0, this.worth)){
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

    /**
     * {@inheritDoc}
     *
     * @return a decimal {@code double} value, that gives
     * the cash value/worth of this cent item. The value
     * is always less than one (1) and above zero (0):
     * {@code value > 0 && value < 1}, with a fraction
     * that gives the value of this cent item.
     *
     * <br/>E.g. {@code 0.5} is 50 cents and {@code 0.05} is
     * 5 cents.
     */
    @Override
    public double getCashValue() {
        if(this.worth < 10)
            return Double.parseDouble("0.0" + this.worth);//Add .0 if less than 10
        else
            return Double.parseDouble("0." + this.worth);
    }
}
