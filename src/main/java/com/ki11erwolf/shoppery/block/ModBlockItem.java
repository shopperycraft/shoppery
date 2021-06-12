package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * A rather special Shoppery mod Block that inherits from
 * Minecraft's {@link BlockItem} class to provide a mod
 * BlockItem with the native register system & method.
 */
class ModBlockItem extends BlockItem {

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
    protected ModBlockItem(Block blockIn, Item.Properties properties) {
        super(blockIn, properties);
    }

    /**
     * Adds this ItemBlock instance to ItemRegistrationQueue, which will then
     * be registered to the game from the queue.
     *
     * @return {@code this}.
     */
    @SuppressWarnings("UnusedReturnValue")
    protected ModBlockItem queueRegistration(){
        if(isQueued)
            throw new IllegalStateException(
                    String.format("ItemBlock: %s already queued for registration.",
                            this.getClass().getCanonicalName())
            );

        ModItems.ITEMS.queueForRegistration(this);
        isQueued = true;

        return this;
    }
}
