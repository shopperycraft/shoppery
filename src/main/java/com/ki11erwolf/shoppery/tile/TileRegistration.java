package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.block.ModBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.function.Supplier;

/**
 * A simple data holder and static {@link TileEntityType} used
 * in the creation of {@link TileEntity Tiles}. Used to register
 * a specific Tile and TileType to the game.
 *
 * <p>Created to avoid the direct use of TileTypes within the Tile
 * and ModTiles classes. Done using a TileDefinition object for
 * each Tile/TileType, defined in the Tile class and given to the
 * ModTiles classes. Data, properties, and created objects is then
 * passed around using the TileDefinition.
 *
 * <p>Holds the values required to create and register a Tile,
 * as well as the created TileEntityType to be passed to the
 * Tile constructor, as a {@link static} package-private
 * variable in the Tile class.
 *
 * <p>Allows simply defining the Tile name, constructor Supplier,
 * and TileBlocks in the Tile class. The TileDefinition can then
 * be registered in the {@link ModTiles} class using {@link
 * ModTiles#TILES#queueForRegistration(Object)}. Finally, the
 * created TileEntityType is passed to the TileDefinition so that
 * it can be passed to the Tile constructor.
 *
 * @param <T> the Tile class defining and using the TileDefinition.
 */
public class TileRegistration<T extends ModTile> {

    /**
     * The simple registry name of the tile entity
     * in the Forge Registry. Usually has {@code "_tile"}
     * appended. Does not include namespace/modid.
     */
    private final String registryName;

    /**
     * List of Shoppery ModBlockTiles that provide the
     * Tile created & defined by this TileDefinition.
     */
    private final ModBlock<?>[] blockTiles;

    /**
     * A reference to the using Tiles constructor.
     * E.g. {@code TileClass::new}. Constructor must
     * be a default constructor with no parameters.
     */
    private final Supplier<T> tileSupplier;

    /**
     * The TileEntityType for the Tile defined
     * by this TileDefinition. The TileType is
     * created and registered in the Forge Tile
     * Registry hook - at which point it is passed
     * to this TileDefinition object for use in the
     * Tile. <b>TileType is {@code null} until
     * registered!</b>
     */
    private TileEntityType<?> tileType;


    // Constructors

    /**
     * Creates a new default TileDefinition for use in
     * the creation & registration of Tiles & TileTypes.
     *
     * <p>Note: all Tile names will have {@code "_tile"}
     * appened to the end of them.
     *
     * @param registryName {@link #getRegistryName()}
     * @param tileSupplier {@link #getTileSupplier()}
     * @param blockTiles {@link #getBlockTiles()}
     */
    TileRegistration(String registryName, Supplier<T> tileSupplier, ModBlock<?>... blockTiles) {
        this(registryName, tileSupplier, true, blockTiles);
    }

    /**
     * Creates a new TileDefinition for use in the
     * creation & registration of Tiles & TileTypes.
     * Allows choosing if {@code "_tile"} is appended
     * to registry names.
     *
     * @param registryName {@link #getRegistryName()}
     * @param tileSupplier {@link #getTileSupplier()}
     * @param prefixName pass {@code true} to append
     * {@code "_tile"} to the registry name.
     * @param blockTiles {@link #getBlockTiles()}
     */
    TileRegistration(String registryName, Supplier<T> tileSupplier,
                     boolean prefixName, ModBlock<?>... blockTiles) {
        this.registryName = registryName + (prefixName ? "_tile" : "");
        this.blockTiles = blockTiles;
        this.tileSupplier = tileSupplier;
    }

    // Immutable values defining the Tile & TileType

    /**
     * The simple registry name of the tile entity
     * in the Forge Registry. Usually has {@code "_tile"}
     * appended. Does not include namespace/modid.
     */
    public String getRegistryName() {
        return registryName;
    }

    /**
     * List of Shoppery ModBlockTiles that provide the
     * Tile created & defined by this TileDefinition.
     */
    public ModBlock<?>[] getBlockTiles() {
        return blockTiles;
    }

    /**
     * A reference to the using Tiles constructor.
     * E.g. {@code TileClass::new}. Constructor must
     * be a default constructor with no parameters.
     */
    public Supplier<T> getTileSupplier() {
        return tileSupplier;
    }

    // Mutable TileType

    /**
     * The TileEntityType for the Tile defined by
     * this TileDefinition. The TileType is created
     * and registered in the Forge Tile Registry
     * hook - at which point it is passed to this
     * TileDefinition object for use in the Tile.<b>
     * TileType is {@code null} until registered!</b>
     */
    public TileEntityType<?> getTileType() {
        return tileType;
    }

    /**
     * The TileEntityType for the Tile defined by
     * this TileDefinition. The TileType is created
     * and registered in the Forge Tile Registry
     * hook - at which point it is passed to this
     * TileDefinition object for use in the Tile.<b>
     * TileType is {@code null} until registered!</b>
     *
     * <p/> Just a version of the method that has no
     * generic type associated with it.
     */
    @SuppressWarnings("rawtypes")
    public TileEntityType getTypelessTileType() {
        return tileType;
    }

    /**
     * Sets the TileEntityType for the Tile defined by
     * this TileDefinition. The TileType is created and
     * registered in the Forge Tile Registry hook - at
     * which point it is passed to this TileDefinition
     * object for use in the Tile.
     */
    void setTileType(TileEntityType<?> tileType) {
        this.tileType = tileType;
    }
}
