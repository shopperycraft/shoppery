package com.ki11erwolf.shoppery.item;

/**
 * Item class for any type of coin.
 */
public class Coin extends ShopperyItem<Coin> {

    /**
     * Prefix for all coin item types.
     */
    private static final String ITEM_NAME_PREFIX = "coin_";

    /**
     * The value/worth (in cents) of this coin (0 < value <= 100).
     */
    private byte value;

    /**
     * {@inheritDoc}
     *
     * @param coinName the name of the coin
     *                 type.
     * @param value the worth (in cents) of this coin (0 < value <= 100).
     */
    Coin(String coinName, byte value) {
        super(new Properties(), ITEM_NAME_PREFIX + coinName);

        if(value < 0 || value > 100)
            throw new IllegalArgumentException("Value out of range (0 < value <= 100): " + value);

        this.value = value;
    }

    /**
     * @return The value/worth (in cents) of this coin (0 < value <= 100).
     */
    public byte getValue(){
        return this.value;
    }
}
