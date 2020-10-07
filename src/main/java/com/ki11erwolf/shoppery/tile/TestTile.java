package com.ki11erwolf.shoppery.tile;

import com.ki11erwolf.shoppery.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nonnull;

public class TestTile extends TileEntity {

    static final TileDefinition<TestTile> TILE_DEFINITION =
            new TileDefinition<>("test", TestTile::new, ModBlocks.TEST_BLOCK);

    public int i = 0;

    public TestTile() {
        super(TILE_DEFINITION.getTileType());
    }

    @Override @Nonnull @NonnullDefault
    public CompoundNBT write(CompoundNBT tags) {
        super.write(tags);
        tags.put("val", IntNBT.valueOf(i));
        return tags;
    }

    @Override @NonnullDefault
    public void func_230337_a_(BlockState state, CompoundNBT tags) {
        super.func_230337_a_(state, tags);
        i = tags.getInt("val");
    }

}
