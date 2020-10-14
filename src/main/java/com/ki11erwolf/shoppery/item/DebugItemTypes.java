package com.ki11erwolf.shoppery.item;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

/**
 * Enum list of all the different types of debug items.
 * To register a new type of debug item, just list it
 * in this enum & it will be registered.
 */
public enum DebugItemTypes {

    /**
     * The read type debug item used to read hidden
     * tile block information.
     */
    READ("read", (context) -> {
//        World world = context.getWorld();
//        PlayerEntity player = context.getPlayer();
//        BlockState blockState = world.getBlockState(context.getPos());
//
//        if(blockState.getBlock() instanceof ShopBlock && player != null && world.isRemote){
//            TileEntity tile = world.getTileEntity(context.getPos());
//            if(!(tile instanceof ShopTile))
//                return ActionResultType.FAIL;
//
//            ItemPrice shopTileItemPrice = ((ShopTile)tile).getShopItemPrice();
//
//            player.sendMessage(new StringTextComponent(
//                    TextFormatting.GOLD + "-------------- Shop Block Info --------------"
//            ), player.getUniqueID());
//
//            player.sendMessage(new StringTextComponent(
//                    TextFormatting.GREEN + "Trading Item: " +
//                            TextFormatting.BLUE + shopTileItemPrice.getItem()
//            ), player.getUniqueID());
//
//            player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "For: " +
//                    TextFormatting.BLUE + "Buy " + CurrencyUtil.CURRENCY_SYMBOL + shopTileItemPrice.getBuyPrice() +
//                    TextFormatting.GOLD + " / " +
//                    TextFormatting.RED + "Sell "  + CurrencyUtil.CURRENCY_SYMBOL + shopTileItemPrice.getSellPrice()
//            ), player.getUniqueID());
//
//            player.sendMessage(new StringTextComponent(
//                    TextFormatting.GOLD + "-------------------------------------------"
//            ), player.getUniqueID());
//        }

        return ActionResultType.PASS;
    }),

    /**
     * The set type debug item used to modify tile
     * block information which cannot normally be
     * changed.
     */
    SET("modify", (context) -> ActionResultType.PASS);

    /**
     * The prefix name used to register the debug
     * within the registry.
     */
    private final String typeName;

    /**
     * The right-click callback, provided by the debug item type,
     * that is used to provide the right-click functionality.
     */
    private final ItemRightClickListener rightClickListener;

    /**
     * @param typeName           The prefix name used to
     *                           register the debug within the registry.
     * @param rightClickListener The right-click
     *                           callback, provided by the debug item type,
     *                           that is used to provide the right-click functionality.
     */
    DebugItemTypes(String typeName, ItemRightClickListener rightClickListener) {
        this.typeName = typeName;
        this.rightClickListener = rightClickListener;
    }

    /**
     * @return The prefix name used to register
     * the debug within the registry.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return The right-click callback, provided by
     * the debug item type, that is used to provide
     * the right-click functionality.
     */
    ItemRightClickListener getRightClickListener() {
        return this.rightClickListener;
    }

    /**
     * Item right-click listener, provided by debug item type,
     * that is notified of debug item right-clicks so that types
     * may specify right-click functionality.
     */
    public interface ItemRightClickListener {

        /**
         * Called when the debug item matching the type
         * that provided this callback is right-clicked.
         *
         * <p/>Debug Item types must specify right-click
         * functionality within this callback.
         *
         * @param context the context of the game when
         * the item was used, holding information related
         * to the usage of the item.
         * @return the result, either success or failure,
         * of using the item.
         */
        ActionResultType onItemRightClick(ItemUseContext context);

    }
}
