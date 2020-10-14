package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.tile.ModTile;
import com.ki11erwolf.shoppery.tile.ShopTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ShopBlock extends ModBlockTile<ShopTile, ShopBlock> {

    public ShopBlock(String registryName) {
        super(AbstractBlock.Properties.create(Material.WOOD), registryName);
    }

    @SuppressWarnings("deprecation") @Deprecated @ParametersAreNonnullByDefault @Nonnull
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                             Hand hand, BlockRayTraceResult hit) {

        return ActionResultType.PASS;
    }

    //Tile

    @Override
    public Class<ShopTile> getTileClass() {
        return ShopTile.class;
    }

    @Override
    public ModTile createTile(BlockState state, IBlockReader world) {
        return new ShopTile();
    }


}
