package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.QueueRegisterer;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

/**
 * Holds all instances of shoppery tile entities and their
 * types, as well as handles the creation & registration of them.
 */
public final class ModTiles extends QueueRegisterer<TileDefinition<? extends TileEntity>> {

    /**
     * Modding logger instance for this class.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

    /**
     * This builds version number passed to {@link DataFixUtils#makeKey(int)}.
     */
    private static final int DF_KEY_VERSION = 1519;

    /**
     * Private singleton instance of this class.
     */
    public static final ModTiles TILES = new ModTiles();
    private ModTiles(){}

    //############################
    //     Tiles to Register
    //############################

    static {
        TILES.queueForRegistration(TestTile.TILE_DEFINITION);
    }

    //############################
    //      Tile Registration
    //############################

    @SubscribeEvent @SuppressWarnings("ConstantConditions")
    public void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        this.iterateQueue(tileDef -> {
            //Create name, builder & possibly fixer.
            ResourceLocation registryName = new ResourceLocation(ShopperyMod.MODID, tileDef.getRegistryName());
            TileEntityType.Builder<?> tileTypeBuilder = TileEntityType.Builder.create(tileDef.getTileSupplier(), tileDef.getBlockTiles());
            Type<?> dataFixerType = getDataFixerType(registryName);

            //Build & set name
            TileEntityType<?> tileType = tileTypeBuilder.build(dataFixerType);
            tileType.setRegistryName(registryName);

            //Register & give back to tile
            event.getRegistry().register(tileType);
            tileDef.setTileType(tileType);
        });
    }

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
