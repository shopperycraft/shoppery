package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.config.ModConfig;
import com.ki11erwolf.shoppery.config.categories.ShopsConfig;
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

    protected static final ShopsConfig SHOPS_CONFIG = ModConfig.GENERAL_CONFIG.getCategory(ShopsConfig.class);

    public ShopBlock(String registryName) {
        super(registryName, AbstractBlock.Properties
                .create(Material.WOOD).harvestTool(ToolType.AXE).noDrops()
                .hardnessAndResistance(10F, 1200F)
        );
    }

    protected ActionResultType onBlockInteract(World world, BlockPos pos, BlockState state, PlayerEntity player,
                                               boolean sneaking, boolean remote, boolean rightClick){
        // Do on server-side
        if(remote) return ActionResultType.SUCCESS;

        //Left-Click
        if(!rightClick){
            if(SHOPS_CONFIG.isBuyLeftClick())
                return getTile(world, pos).sellToPlayer(world, player) ?
                        ActionResultType.SUCCESS : ActionResultType.FAIL;
        }

        //Right-Click
        if(rightClick) {
            if(SHOPS_CONFIG.isBuyRightClick())
                return getTile(world, pos).sellToPlayer(world, player) ?
                        ActionResultType.SUCCESS : ActionResultType.FAIL;
        }

        return ActionResultType.FAIL;
    }

    // MC Click Handlers

    @Override @SuppressWarnings("deprecation") @Deprecated @ParametersAreNonnullByDefault @Nonnull
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!(player.getHeldItemMainhand().getItem() instanceof DebugItem))
            return onBlockInteract(world, pos, state, player, player.isSneaking(), world.isRemote, true);
        else return ActionResultType.FAIL;
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
