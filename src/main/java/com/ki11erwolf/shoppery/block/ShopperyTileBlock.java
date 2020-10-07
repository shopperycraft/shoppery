package com.ki11erwolf.shoppery.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * The base class for all Mod blocks that provide a tile entity.
 * This class extends the functionality of the base Block class
 * to provide for tile entities. Implementing classes are expected
 * to provide the tile entity.
 *
 * @param <T> the tile entity class.
 */
public abstract class ShopperyTileBlock<T extends TileEntity> extends ShopperyBlock<ShopperyTileBlock<?>> {

    /**
     * Basic constructor for all Mod tile entities.
     *
     * @param properties the properties & behaviour of this specific block.
     * @param name the registry name of the block.
     */
    protected ShopperyTileBlock(Properties properties, String name) {
        super(properties, name);
    }

    /**
     * Gets the tile entity at the given location
     * cast as tile entity with generic type.
     *
     * @param world the world the tile entity is in.
     * @param pos the location in the world.
     * @return the cast tile entity.
     */
    @SuppressWarnings("unchecked")
    protected T getTileEntity(IBlockReader world, BlockPos pos) {
        return (T) world.getTileEntity(pos);
    }

    /**
     * Always returns {@code true} as implementing classes
     * are tile entity blocks.
     *
     * @return {@code true}
     */
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    // ****************
    // Abstract Methods
    // ****************

    /**
     * @return this blocks tile entity class.
     */
    public abstract Class<T> getTileEntityClass();

    /**
     * Called so the block can construct and return the its entity
     * object instance for this block.
     *
     * @param world the world the block is in.
     * @param state the block state of the block.
     * @return the newly constructed tile entity.
     */
    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);
}
