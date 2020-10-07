package com.ki11erwolf.shoppery.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * A special Shoppery ItemBlock created to easily register
 * ItemBlocks when registering normals blocks.
 */
public class ShopperyItemBlock extends BlockItem {

    /**
     * Flag to prevent queuing an item
     * more than once.
     */
    private boolean isQueued = false;

    /**
     * Creates a new ItemBlock instance.
     *
     * @param blockIn the block this itemBlock is for.
     * @param properties the item block properties.
     */
    public ShopperyItemBlock(Block blockIn, Item.Properties properties) {
        super(blockIn, properties);
    }

    /**
     * Adds this ItemBlock instance to ItemRegistrationQueue, which will then
     * be registered to the game from the queue.
     *
     * @return {@code this}.
     */
    @SuppressWarnings("UnusedReturnValue")
    public ShopperyItemBlock queueRegistration(){
        if(isQueued)
            throw new IllegalStateException(
                    String.format("ItemBlock: %s already queued for registration.",
                            this.getClass().getCanonicalName())
            );

        ShopperyItems.ITEMS.queueForRegistration(this);
        isQueued = true;

        return this;
    }
}
