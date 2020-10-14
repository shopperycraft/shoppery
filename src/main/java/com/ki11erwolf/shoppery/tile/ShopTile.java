package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class ShopTile extends ModTile{

    // Type & Constructor

    static final TileDefinition<?> TILE_DEFINITION = new TileDefinition<>(
            "shop", ShopTile::new, ModBlocks.SHOP_BLOCK
    );

    public ShopTile() {
        super(TILE_DEFINITION);
    }

    // Read/Write

    @Override
    protected CompoundNBT onWrite(CompoundNBT tags) {

        return tags;
    }

    @Override
    protected void onRead(BlockState state, CompoundNBT tags) {

    }
}
