package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.tile.ModTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.apache.logging.log4j.Logger;

/**
 * The base class for all Shoppery mod blocks that work with tile
 * entities.
 *
 * <p>Inherits from {@link ModBlock}, providing the
 * basic mod block functionality with additional functionality
 * added for {@link TileEntity TileEntities or Tiles}.
 *
 * @param <T> the blocks tile entity class.
 * @param <B> the implementing block child class.
 */
public abstract class ModBlockTile<T extends TileEntity, B extends ModBlockTile<T, B>> extends ModBlock<B> {

    /**
     * Mod logger instance for this class.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

    /**
     * Basic constructor for all Mod tile entities.
     *
     * @param properties the properties & behaviour of this specific block.
     * @param name the registry name of the block.
     */
    protected ModBlockTile(Properties properties, String name) {
        super(properties, name);
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
     * @return the class of the exact Tile/TE this block
     * provides.
     */
    public abstract Class<T> getTileClass();

    /**
     * Called when the block is created/placed in world
     * and needs to provide a Tile instance. Should
     * construct and return a new Tile instance for this
     * block whenever called.
     *
     * @param world the world the block is in.
     * @param state the block state of the block.
     * @return the newly constructed tile entity.
     */
    public abstract ModTile createTile(BlockState state, IBlockReader world);

    /**
     * @deprecated use ModBlockTile provided {@link
     * #createTile(BlockState, IBlockReader)}.
     *
     * Called so the block can construct and return the its entity
     * object instance for this block.
     *
     * @param world the world the block is in.
     * @param state the block state of the block.
     * @return the newly constructed tile entity.
     */
    @Override @Deprecated
    public TileEntity createTileEntity(BlockState state, IBlockReader world){
        return createTile(state, world);
    }

    // ***********
    // Util & Help
    // ***********

    /**
     * <b>Tile Util - </b>Gets the tile entity at
     * the given location, provided it exists, and
     * is of the correct type. Will be cast
     * correctly if possible.
     *
     * @param world the world the tile entity is in.
     * @param pos the location in the world.
     * @return the Tile at the given position
     * cast to the correct generic type, if
     * possible. Returns {@code null} on failure.
     */
    @SuppressWarnings("unchecked")
    protected T getTile(IBlockReader world, BlockPos pos) {
        TileEntity tile = getTE(world, pos);

        if(checkTile(tile))
            return (T) world.getTileEntity(pos);
        else return null; //No Tile/Tile invalid type.
    }

    /**
     * Checks that a given Tile is not {@code null}
     * and that it is of type {@link #getTileClass()}.
     *
     * @param toCheck the Tile to check for {@code null} or
     *                Class mismatch.
     * @return {@code true} if Tile is not {@code null} and
     * of type {@link #getTileClass()}.
     */
    protected boolean checkTile(TileEntity toCheck){
        if(toCheck == null) {
            LOG.error("No Tile nor TileEntity (NULL) - getTile()");
            return false;
        }

        if(toCheck.getClass() != getTileClass()){
            LOG.warn("Failed to get exact Tile from TE. " + String.format(
                    "%s != %s", toCheck.getClass(), getTileClass()));
            return false;
        }

        return true;
    }

    /**
     * <b>Tile Util - </b>Gets the tile entity at
     * the given location in the given world. Null
     * Tiles will be logged to console.
     *
     * @param world the world the tile entity is in.
     * @param pos the location in the world.
     * @return the Tile at the given position, or
     * {@code null} if no Tile exists.
     */
    protected TileEntity getTE(IBlockReader world, BlockPos pos){
        TileEntity entity = world.getTileEntity(pos);

        if(entity == null)
            LOG.warn("Got NULL TileEntity. Block: "
                + world.getBlockState(pos).getBlock().getRegistryName());

        return entity;
    }
}
