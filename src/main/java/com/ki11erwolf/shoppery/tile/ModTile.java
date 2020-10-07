package com.ki11erwolf.shoppery.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A base class for all Shoppery Mod Tiles to inherit
 * from. Extends upon the vanilla TileEntity class to
 * allow using TileDefinitions, better read/write
 * methods, and various other Mod features & functions.
 */
public abstract class ModTile extends TileEntity {

    /**
     * The inheriting Tiles specific TileDefinition object
     * that was used to create & register the Tile & TileType.
     * Contains the registered TileType object for the TileEntity
     * super class as well, if registered successfully.
     */
    protected final TileDefinition<? extends ModTile> definition;

    /**
     * Creates a new basic ModTile that
     *
     * @param tileDefinition the Tiles defining object, that defines
     *                      the Tile & TileType, holds its data, and
     *                       allows it to be registered using {@link
     *                       ModTiles}.
     */
    public ModTile(TileDefinition<? extends ModTile> tileDefinition) {
        super(Objects.requireNonNull(tileDefinition).getTileType());
        this.definition = tileDefinition;
    }

    // ***************
    // Tile read/write
    // ***************

    /**
     * The Tile version of {@code read()}.
     *
     * <p>Allows and instructs inheriting classes to
     * reconstruct their data from the CompoundNBT
     * object containing previously saved NBT-tag-data
     * on disk, to then <b>read</b> it back into memory.
     * Effectively restoring the Tile from a write/save.
     *
     * <p>Called by Forge when the Chunk the Tile belongs
     * to is being reloaded into game from disk.
     *
     * @param state the block providing the Tile as it
     *              exists in the world.
     * @param tags the CompoundNBT object containing
     *             previously saved tags from file. Use
     *             to read back data into memory.
     */
    protected void onRead(BlockState state, CompoundNBT tags){
        //No-op
    }

    /**
     * The Tile version of {@code write()}.
     *
     * <p>Allows and instructs inheriting classes to
     * convert their data from memory into NBT-tag-data
     * and <b>write</b> that data to disk by passing it
     * to the CompoundNBT object. Effectively saving
     * and storing the Tile for later reading/loading.
     *
     * <p>Called by Forge when the Chunk the Tile belongs
     * to is being unloaded & saved.
     *
     * @param tags the CompoundNBT object provided by
     *             Forge. Use to write memory data to
     *             file by converting to NBT-tag-data
     *             and writing it to the CompoundNBT
     *             object.
     * @return by convention, returns the given {@code
     * tags} object after being written to. However, the
     * Tile {@code onWrite()} method allows returning
     * {@code null} as shortcut to achieve the same result.
     * Additionally, {@code onWrite()} allows returning any
     * CompoundNBT object to saved to disk instead.
     */
    protected CompoundNBT onWrite(CompoundNBT tags){
        return null;
    }

    // *******************
    // Override read/write
    // *******************

    /**
     * @deprecated use {@link #onWrite(CompoundNBT)}.
     *
     * Captures the call from Forge to write data
     * to disk and passes control over to {@link
     * #onWrite(CompoundNBT)}.
     *
     * @param tagsIn given NBT tags from forge to
     *               write to.
     * @return either {@code tagsIn} if {@code
     * onWrite()} returns {@code null}, else
     * the return value from {@code onWrite()}
     * is returned.
     */
    @Override @Nonnull @NonnullDefault @Deprecated
    public CompoundNBT write(CompoundNBT tagsIn) {
        super.write(tagsIn);
        CompoundNBT tagsNew = onWrite(tagsIn);
        return tagsNew == null ? tagsIn : tagsNew;
    }

    /**
     * @deprecated use {@link #onRead(BlockState,
     * CompoundNBT)}.
     *
     * Captures the call from Forge to read data
     * from disk and passes control over to {@link
     * #onRead(BlockState, CompoundNBT)}.
     *
     * @param state the block instance in the world
     *              that provides this Tile.
     * @param tags given NBT tags from forge to
     *               read from.
     */
    @Override @NonnullDefault @Deprecated
    public void /*read*/ func_230337_a_(BlockState state, CompoundNBT tags) {
        super.func_230337_a_(state, tags);
        onRead(state, tags);
    }
}
