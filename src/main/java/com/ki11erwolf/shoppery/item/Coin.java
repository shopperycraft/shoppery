package com.ki11erwolf.shoppery.item;

/**
 * Item class for any type of coin.
 *
 * Shoppery notes & coins are usually referred to
 * as Dollars to give simple, well known name
 * to the currency.
 */
public class Coin extends ShopperyItem<Coin> {

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
    Coin(String coinName, byte worth) {
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

}
