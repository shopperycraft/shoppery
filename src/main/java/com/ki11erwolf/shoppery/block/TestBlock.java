package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.tile.ModTile;
import com.ki11erwolf.shoppery.tile.TestTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nonnull;

public class TestBlock extends ModBlockTile<TestTile, TestBlock> {

    protected TestBlock(String name) {
        super(AbstractBlock.Properties.create(Material.ROCK), name);
    }

    @Deprecated @Override @Nonnull @NonnullDefault @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                             Hand handIn, BlockRayTraceResult hit) {
        Logger log = ShopperyMod.getNewLogger();
        log.info("activated!");

        TileEntity entity = world.getTileEntity(pos);
        log.info("entity: " + entity);
        log.info("entity is correct: " + (entity instanceof TestTile));

        if(entity instanceof  TestTile){
            TestTile tile = (TestTile)entity;
            tile.i++;
            tile.markDirty();

            log.info("Entity val: " + tile.i);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public Class<TestTile> getTileClass() {
        return TestTile.class;
    }

    @Override
    public ModTile createTile(BlockState state, IBlockReader world) {
        return new TestTile();
    }
}
