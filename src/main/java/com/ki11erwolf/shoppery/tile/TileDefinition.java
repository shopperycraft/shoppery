package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.block.ModBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.function.Supplier;

class TileDefinition<T extends TileEntity> {

    private final String registryName;

    private final ModBlock<?>[] blockTiles;

    private final Supplier<T> tileSupplier;

    private TileEntityType<? extends TileEntity> tileType;

    //

    TileDefinition(String registryName, Supplier<T> tileSupplier, ModBlock<?>... blockTiles) {
        this(registryName, tileSupplier, true, blockTiles);
    }

    TileDefinition(String registryName, Supplier<T> tileSupplier,
                   boolean prefixName, ModBlock<?>... blockTiles) {
        this.registryName = registryName + (prefixName ? "_tile" : "");
        this.blockTiles = blockTiles;
        this.tileSupplier = tileSupplier;
    }

    String getRegistryName() {
        return registryName;
    }

    ModBlock<?>[] getBlockTiles() {
        return blockTiles;
    }

    Supplier<? extends TileEntity> getTileSupplier() {
        return tileSupplier;
    }

    //

    void setTileType(TileEntityType<? extends TileEntity> tileType){
        this.tileType = tileType;
    }

    TileEntityType<? extends TileEntity> getTileType(){
        return tileType;
    }
}
