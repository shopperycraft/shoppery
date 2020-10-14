package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.item.DebugItem;
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
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ShopBlock extends ModBlockTile<ShopTile, ShopBlock> {

    public ShopBlock(String registryName) {
        super(AbstractBlock.Properties.create(Material.WOOD)
                        .hardnessAndResistance(10F, 1200F)
                        .harvestTool(ToolType.AXE).noDrops(),
                registryName
        );
    }

    protected ActionResultType onBlockInteract(World world, BlockPos pos, BlockState state, PlayerEntity player,
                                               boolean sneaking, boolean remote, boolean rightClick){
        if(remote) return ActionResultType.SUCCESS;

        if(rightClick) {
            if(sneaking){
                //Only allow activating for the base type.
                if(state.getBlock().getClass() == ShopBlock.class)
                    getTile(world, pos).activate();
            } else {
                getTile(world, pos).sellToPlayer(world, player);
            }

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
    }

    @Override @SuppressWarnings("deprecation") @Deprecated @ParametersAreNonnullByDefault
    @Nonnull
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                             Hand hand, BlockRayTraceResult hit) {
        if(player.getHeldItemMainhand().getItem() instanceof DebugItem)
            return ActionResultType.FAIL;

        return onBlockInteract(world, pos, state, player, player.isSneaking(), world.isRemote, true);
    }

    @Override @SuppressWarnings("deprecation") @ParametersAreNonnullByDefault
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        onBlockInteract(world, pos, state, player, player.isSneaking(), world.isRemote, false);
    }

    // Tile

    @Override
    public Class<ShopTile> getTileClass() {
        return ShopTile.class;
    }

    @Override
    public ModTile createTile(BlockState state, IBlockReader world) {
        return new ShopTile();
    }
}
