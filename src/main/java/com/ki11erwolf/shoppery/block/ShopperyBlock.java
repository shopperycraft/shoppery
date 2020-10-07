package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.ShopperyItemGroup;
import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.item.ShopperyItemBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Base class for all shoppery blocks.
 *
 * @generic T the class inheriting from this class.
 */
public class ShopperyBlock<T> extends Block {

    /**
     * Flag to prevent queuing a block
     * more than once.
     */
    private boolean isQueued = false;

    /**
     * The Item that represents this block in
     * the inventory.
     */
    private final ShopperyItemBlock itemBlock;

    /**
     * Creates a new Block for the Shoppery mod.
     *
     * @param properties the properties the block will take.
     * @param registryName the name of the block in the registry.
     */
    public ShopperyBlock(Properties properties, String registryName) {
        this(properties, new Item.Properties().group(ShopperyItemGroup.INSTANCE), registryName);
    }

    /**
     * Creates a new Block for the Shoppery mod.
     *
     * @param properties the properties the block will take on.
     * @param itemProperties the properties the item that represents
     *                       this block in the inventory will take on.
     * @param registryName the name of the block in the registry.
     */
    public ShopperyBlock(Properties properties, Item.Properties itemProperties, String registryName) {
        super(properties);
        setRegistryName(ShopperyMod.MODID, registryName);

        itemBlock = new ShopperyItemBlock(this, itemProperties);
        itemBlock.setRegistryName(registryName);
    }

    /**
     * Adds this block instance to the blocks registration queue
     * where it will be queued for registration into the game.
     *
     * @return {@code this}.
     */
    T queueRegistration(){
        if(isQueued)
            throw new IllegalStateException(
                    String.format("Item: %s already queued for registration.",
                            this.getClass().getCanonicalName())
            );

        ShopperyBlocks.queueItem(this);
        isQueued = true;

        itemBlock.queueRegistration();

        //noinspection unchecked //Should NOT be possible.
        return (T)this;
    }

}
