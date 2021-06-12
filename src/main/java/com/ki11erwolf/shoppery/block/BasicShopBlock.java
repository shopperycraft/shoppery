package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.tile.BasicShopTile;
import com.ki11erwolf.shoppery.tile.ModTile;
import net.minecraft.block.BlockState;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

/**
 * The "Basic" Shops {@link ShopBlock} implementation.
 *
 * <p/>A type of Shop that trades a randomly chosen Item or Block, with a
 * registered {@link com.ki11erwolf.shoppery.price.ItemPrice Price}, at a
 * price similar to the original. The Shop chooses the Item/Block to trade
 * and at what prices when first setup by a player, and forever links the
 * two together. Additionally, these Shops have unlimited Items and Money
 * for trading, as they're primarily intended for players in a singleplayer
 * setting.
 *
 * <p/> Defines the unique properties and logic specific to this
 * implementation of ShopBlock, such as the blocks registry name
 * and specific {@link com.ki11erwolf.shoppery.tile.ShopTile} implementation.
 */
public class BasicShopBlock extends ShopBlock<BasicShopTile> {

    /**
     * The unique name that both this Block & its Tile are registered under.
     */
    public static final String REGISTRY_NAME = "basic_shop";

    /**
     * Creates a new instance of the Basic Shop Block type.
     */
    public BasicShopBlock() {
        super(REGISTRY_NAME);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link BasicShopTile#getClass()}.
     */
    @Override
    public Class<BasicShopTile> getTileType() {
        return BasicShopTile.class;
    }

    /**
     * {@inheritDoc}
     *
     * @param state the state of the specific block, as it
     *              exists in the world, that needs the Tile.
     * @param world the world, in which the block is placed,
     *              that needs the Tile.
     * @return a new {@link BasicShopTile} object instance.
     */
    @Override @Nonnull
    public ModTile createTile(BlockState state, IBlockReader world) {
        return new BasicShopTile();
    }
}
