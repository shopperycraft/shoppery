package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.block.BasicShopBlock;
import com.ki11erwolf.shoppery.block.ModBlocks;
import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.ItemPrices;

/**
 * The "Basic" Shops {@link ShopTile} implementation.
 *
 * <p/>A type of Shop that trades a randomly chosen Item or Block, with a
 * registered {@link com.ki11erwolf.shoppery.price.ItemPrice Price}, at a
 * price similar to the original. The Shop chooses the Item/Block to trade
 * and at what prices when first setup by a player, and forever links the
 * two together. Additionally, these Shops have unlimited Items and Money
 * for trading, as they're primarily intended for players in a singleplayer
 * setting.
 *
 * <p/> Defines the properties, logic, and anything else specific to this
 * unique implementation of ShopTile, such as the way it's setup and the
 * type(s) of data it uses.
 */
public class BasicShopTile extends ShopTile<ShopTileData> {

    /**
     * The registration object used to register this specific Tile Type
     * to the game.
     */
    protected static final TileRegistration<?> BASIC_SHOP_REGISTRATION = new TileRegistration<>(
            BasicShopBlock.REGISTRY_NAME, BasicShopTile::new, ModBlocks.BASIC_SHOP
    );

    /**
     * Creates a new "Basic" Shop Tile instance object, likely for a newly placed
     * / generated Basic Shop. Provides the necessary {@link TileRegistration}
     * object and {@link ShopTileData} implementation instance.
     */
    public BasicShopTile() {
        super(BASIC_SHOP_REGISTRATION, ShopTileData::new);
    }

    /**
     * Makes this Shop trade a random item at a slightly higher or
     * lower price.
     *
     * Chooses a random Item from the {@link ItemPrices price registry}
     * that is acceptable to trade in the Shop, and forces this shop to
     * trade the chosen Item at a slightly higher or lower price ({@link
     * ItemPrice#withPriceFluctuation()}), regardless of whether or not
     * it has previously been setup.
     */
    private void setupRandomTrade() {
        ItemPrice randomSetPrice = ItemPrices.getRandomPrice().withPriceFluctuation();
        if(!isValidTrade(randomSetPrice))
            setupRandomTrade(); //Repeat until valid price is found

        setShopsTrade(randomSetPrice);
    }

    /**
     * Sets up this specific Shop to trade a randomly chosen
     * Item at a price similar to the original.
     */
    @Override
    protected void setup() {
        setupRandomTrade();
    }
}
