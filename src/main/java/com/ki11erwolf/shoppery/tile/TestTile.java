package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;

public class TestTile extends ModTile {

    static final TileDefinition<?> TILE_DEFINITION = new TileDefinition<>(
            "test", TestTile::new, ModBlocks.TEST_BLOCK
    );

    //

    public int i = 0;

    public TestTile() {
        super(TILE_DEFINITION);
    }

    @Override
    protected CompoundNBT onWrite(CompoundNBT tags) {
        tags.put("val", IntNBT.valueOf(i));
        return null;
    }

    @Override
    protected void onRead(BlockState state, CompoundNBT tags) {
        i = tags.getInt("val");
    }
}
