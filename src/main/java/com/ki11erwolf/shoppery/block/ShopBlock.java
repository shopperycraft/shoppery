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

/**
 * The definition of the base Shop Block item class - the most basic type of shop,
 * containing everything necessary and proving a solid foundation for the other
 * additional Shop Blocks variations.
 *
 * <p/>Contains the base logic, characteristics, appearance, and properties,
 * that affect every Shop Block, and defines the Shop Block item class as a whole.
 *
 * <p/>The {@link ShopTile} class handles each Shop Block individually and defines
 * how they work, with a dedicated ShopTile object for each block in the world.
 */
public class ShopBlock extends ModBlockTile<ShopTile, ShopBlock> {

    /**
     * A convenient reference to the global config settings for Shops.
     */
    protected static final ShopsConfig SHOPS_CONFIG = ModConfig.GENERAL_CONFIG.getCategory(ShopsConfig.class);

    /**
     * Creates a new basic shop block under the specified unique registry name.
     *
     * @param registryName the unique identifying name that this shop block instance
     * will be registered under and identified by.
     */
    public ShopBlock(String registryName) {
        super(registryName, AbstractBlock.Properties
                .create(Material.WOOD).harvestTool(ToolType.AXE).noDrops()
                .hardnessAndResistance(
                        (SHOPS_CONFIG.isBreakingPrevented()) ? -1.0F : 10.0F, 3600000.0F
                )
        );
    }

    /**
     * The onClick callback for Shop Blocks.
     *
     * A custom built callback that is fired whenever a block is either
     * left-clicked or right-clicked, and containing additional information.
     *
     * <p/>Built using the Minecraft provided onClick callbacks, to make a better
     * suited onClick callback for shop blocks.
     *
     * @param state the state of the specific block clicked as it is in the world.
     * @param world the world the clicked block in is.
     * @param pos the position of the clicked block in the world.
     * @param player the player who clicked the block.
     * @param sneaking flag stating if the player was sneaking when clicked
     * @param remote if the world and logic is running client side.
     * @param rightClick flag stating the block was left-clicked or right-clicked.
     * @return the results of the action: success or failure.
     */
    protected ActionResultType onClicked(World world, BlockPos pos, BlockState state, PlayerEntity player,
                                         boolean sneaking, boolean remote, boolean rightClick){
        // Do on client-side
        if(remote) return ActionResultType.SUCCESS;

        if(!rightClick){ //Left-Click & set to left-click
            if(SHOPS_CONFIG.isBuyLeftClick()) {
                return getTile(world, pos).playerBuyRequest(world, player) ?
                        ActionResultType.SUCCESS : ActionResultType.FAIL;
            } else {
                return getTile(world, pos).playerSellRequest(world, player) ?
                        ActionResultType.SUCCESS : ActionResultType.FAIL;
            }
        }

        if(!(SHOPS_CONFIG.isBuyLeftClick())) { //Right-Click & set to right-click
            return getTile(world, pos).playerBuyRequest(world, player) ?
                    ActionResultType.SUCCESS : ActionResultType.FAIL;
        } else {
            return getTile(world, pos).playerSellRequest(world, player) ?
                    ActionResultType.SUCCESS : ActionResultType.FAIL;
        }

    }

    // MC Click Handlers

    /**
     * The normal right-click callback provided by Minecraft, that is fired whenever
     * a block of this type is right-clicked in the world. The exact block clicked
     * is passed as a parameter.
     *
     * <p/>Callback hook used to make the self internal {@link #onClicked(World,
     * BlockPos, BlockState, PlayerEntity, boolean, boolean, boolean)} callback
     * work.
     *
     * @param state the state of the specific block clicked as it is in the world.
     * @param world the world the clicked block in is.
     * @param pos the position of the clicked block in the world.
     * @param player the player who clicked the block.
     */
    @Override @SuppressWarnings("deprecation") @Deprecated @ParametersAreNonnullByDefault @Nonnull
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!(player.getHeldItemMainhand().getItem() instanceof DebugItem))
            return onClicked(world, pos, state, player, player.isSneaking(), world.isRemote, true);
        else return ActionResultType.FAIL;
    }

    /**
     * The left-click callback provided by Minecraft, that is fired whenever
     * a block of this type is left-clicked in the world. The exact block clicked
     * is passed as a parameter.
     *
     * <p/>Callback hook used to make the self internal {@link #onClicked(World,
     * BlockPos, BlockState, PlayerEntity, boolean, boolean, boolean)} callback
     * work.
     *
     * @param state the state of the specific block clicked as it is in the world.
     * @param world the world the clicked block in is.
     * @param pos the position of the clicked block in the world.
     * @param player the player who clicked the block.
     */
    @Override @SuppressWarnings("deprecation") @ParametersAreNonnullByDefault
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        onClicked(world, pos, state, player, player.isSneaking(), world.isRemote, false);
    }

    // Tile

    /**
     * {@inheritDoc}
     *
     * @return the class object for the {@link ShopTile} class:
     * {@link ShopTile#getClass()}.
     */
    @Override
    public Class<ShopTile> getTileClass() {
        return ShopTile.class;
    }

    /**
     * {@inheritDoc}
     *
     * @param state the block state of the block.
     * @param world the world the block is in.
     * @return a newly constructed {@link ShopTile} instance.
     */
    @Override
    public ModTile createTile(BlockState state, IBlockReader world) {
        return new ShopTile();
    }
}
