package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.QueueRegisterer;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

/**
 * The Shoppery Mods "internal" Tile definitions queue and
 * TileType creator & registerer. Enables the usage, creation,
 * registration, and abstraction of Tiles & TileTypes.
 *
 * <p>Provides/mimics the same ease of use and functionality
 * as {@link com.ki11erwolf.shoppery.block.ModBlocks} & {@link
 * com.ki11erwolf.shoppery.item.ModItems}, while handling the
 * complexity of Tiles and TileTypes behind the scenes.
 *
 * <p>Provides a {@code static} block and a {@link
 * #queue(TileDefinition)} method where Tiles TileDefinitions
 * can be easily queued.
 */
public final class ModTiles extends QueueRegisterer<TileDefinition<? extends ModTile>> {

    /**
     * Modding logger instance for this class.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

    /**
     * This builds version number passed to {@link DataFixUtils#makeKey(int)}.
     */
    private static final int DF_KEY_VERSION = 42010;//1591

    /**
     * Private singleton instance of this class.
     */
    public static final ModTiles TILES = new ModTiles();
    private ModTiles(){}

    //############################
    //     Tiles to Register
    //############################

    /*
     * Add definitions for new Tiles & TileTypes
     * here within the static block.
     */
    static {
        queue(ShopTile.TILE_DEFINITION);
    }

    /**
     * Adds a Tile to the queue of Tiles to be created and
     * registered. Tiles are queued and registered using
     * {@link TileDefinition} objects.
     *
     * @param tile the Tile & TileType to create and queue,
     *             defined by a TileDefinition.
     */
    public static void queue(TileDefinition<? extends ModTile> tile){
        TILES.queueForRegistration(tile);
    }

    //############################
    //      Tile Registration
    //############################

    /**
     * Called when the Forge Tiles registry is being
     * created and objects are being registered.
     *
     * <p>Will attempt to create & register a TileType
     * for all the Tiles queued for registration, using
     * {@link TileDefinition}'s.
     *
     * Assuming success, the created & registered TileType
     * will be passed back to the Tiles TileDefinition for
     * use in the constructor.
     *
     * @param registryEvent the subscribed to event that called this method.
     *                      Contains a reference to the Tile registry.
     */
    @SubscribeEvent
    public void registerTiles(RegistryEvent.Register<TileEntityType<?>> registryEvent) {
        this.iterateQueue(tileDef -> createRegisterAndPass(tileDef, registryEvent.getRegistry()));
    }

    /**
     * Using a {@link TileDefinition} and Forges Tile Registry, will
     * create & register a TileType for a Tile.
     *
     * <p>The Tiles & TileTypes are created/defined using TileDefinitions.
     * TileDefinitions allow passing Tiles & TileTypes
     *
     * Once created & registered, the TileType is passed back to the
     * TileDefinition so that the Tile may use it.The created TileType
     * supports data fixers.
     *
     * @param tileDefinition the object defining what Tile/TileType to
     *                       create & register.
     * @param tileRegistry Forge's Tile Registry.
     */
    private void createRegisterAndPass(TileDefinition<? extends ModTile> tileDefinition,
                                       IForgeRegistry<TileEntityType<?>> tileRegistry){
        //Registry Name
        ResourceLocation registryName = new ResourceLocation(ShopperyMod.MODID,
                tileDefinition.getRegistryName());

        //Create Builders
        Type<?> dataFixerType = getDataFixerType(registryName);
        TileEntityType.Builder<?> tileTypeBuilder = TileEntityType.Builder.create(
                tileDefinition.getTileSupplier(), tileDefinition.getBlockTiles()
        );

        //Create
        @SuppressWarnings("ConstantConditions") //Still works
        TileEntityType<?> tileType = tileTypeBuilder.build(dataFixerType);
        tileType.setRegistryName(registryName);

        //Register & Pass
        tileRegistry.register(tileType);
        tileDefinition.setTileType(tileType);
    }

    /**
     * Attempts to get the {@link com.mojang.datafixers.DataFixer}
     * {@link Type} registered to a Tile/TileType, if any.
     *
     * <p>Useless at time of implementation/until data fixers
     * are created and registered.
     *
     * @param registryName the registry name of the Tile.
     * @return the existent, registered, and found Type,
     * or {@code null} if none could be found.
     */
    private Type<?> getDataFixerType(ResourceLocation registryName){
        try {
            return DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(DF_KEY_VERSION))
                    .getChoiceType(TypeReferences.BLOCK_ENTITY, registryName.toString());
        } catch (IllegalArgumentException ex){
            LOG.warn("No DataFixer for: " + registryName.toString(), ex);
        }

        return null;
    }
}
