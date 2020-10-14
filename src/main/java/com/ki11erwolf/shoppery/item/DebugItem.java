package com.ki11erwolf.shoppery.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A type of various items, used in development, that
 * is normally not registered to the game.
 */
public abstract class DebugItem extends ModItem<DebugItem> {

    /**
     * The string used to prefix all debug item
     * names within the registry.
     */
    private static final String ITEM_PREFIX = "debug_item_";

    /**
     * @param typeName the type name of the specific
     *                 debug item.
     */
    DebugItem(String typeName) {
        super(typeName);
    }

    /**
     * Called when the item is right-clicked.
     *
     * <p/>Will try and cycle the item to the
     * next debug item type using the method ({@link
     * #switchToNextDebugItem(PlayerEntity, Hand)}).
     *
     * @param world the world of the player.
     * @param player the player.
     * @param hand the players hand holding the
     *             item in use.
     * @return an ActionResult, that holds the ItemStack
     * that the player is holding, with the result Pass
     * if the item was cycled, or result Fail  if the
     * item was not cycled.
     */
    @Override @Nonnull @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack item = player.getHeldItem(hand);

        if(player.isSneaking())
            return new ActionResult<>(switchToNextDebugItem(player, hand)
                    ? ActionResultType.PASS : ActionResultType.FAIL, player.getHeldItem(hand));

        return new ActionResult<>(ActionResultType.FAIL, player.getHeldItem(hand));
    }

    /**
     * Called when the debug item is used on a block.
     *
     * <p/>Will try and use the debug item as the type
     * it was specified as.
     *
     * @param context the context in which the item was
     *                used.
     * @return the result of using the item.
     */
    @Nonnull @ParametersAreNonnullByDefault
    public ActionResultType onItemUse(ItemUseContext context) {
        if(context.getPlayer() != null && context.getPlayer().isSneaking()) {
            switchToNextDebugItem(context.getPlayer(), context.getHand());
            return ActionResultType.FAIL;
        }

        return getDebugItemType().getRightClickListener().onItemRightClick(context);
    }

    /**
     * Will try and cycle the type of the debug item the
     * player is holding.
     *
     * @param player the player holding the item.
     * @param hand the hand of the player holding the
     *             item.
     * @return {@code true} if the item was cycled,
     * {@code false} otherwise.
     */
    protected boolean switchToNextDebugItem(PlayerEntity player, Hand hand){
        ItemStack heldItem = player.getHeldItem(hand);

        if(!(heldItem.getItem() instanceof DebugItem))
            return false;

        boolean found = false;
        for(DebugItem debugItem : ModItems.DEBUG_ITEMS){
            if(heldItem.getItem() == debugItem){
                found = true; continue;
            }

            if (found) {
                heldItem = new ItemStack(debugItem);
                player.setHeldItem(hand, heldItem);
                return true;
            }
        }

        player.setHeldItem(hand, new ItemStack(ModItems.DEBUG_ITEMS.get(0)));
        return true;
    }

    /**
     * @return The enum type that defines what exact debug
     * item this object instance is.
     */
    public abstract DebugItemTypes getDebugItemType();

    /**
     * Creates a new type of debug item that where the
     * type is set by the {@link DebugItemTypes}.
     *
     * @param debugItemType the type of debug item to
     *                      make this debug item.
     * @return the newly created debug item matching
     * the type passed in.
     */
    public static DebugItem newDebugItem(DebugItemTypes debugItemType){
        return new DebugItem(ITEM_PREFIX + debugItemType.getTypeName()) {
            @Override
            public DebugItemTypes getDebugItemType() {
                return debugItemType;
            }
        };
    }

}
