package com.ki11erwolf.shoppery.item;

import com.ki11erwolf.shoppery.ShopperyMod;
import net.minecraft.item.Item;

/**
 * Base class for all shoppery items.
 */
@SuppressWarnings("WeakerAccess")
public class ShopperyItem extends Item {

    /**
     * Flag to prevent queuing an item
     * more than once.
     */
    private boolean isQueued = false;

    /**
     * Package private constructor to prevent
     * item instance creation from outside
     * packages.
     *
     * @param properties properties specific
     *                   to this item.
     * @param registryName the name to register
     *                     this item under.
     */
    ShopperyItem(Properties properties, String registryName) {
        super(properties);
        this.setRegistryName(ShopperyMod.MODID, registryName);
    }

    /**
     * Adds this item instance to the
     * Shoppery-item-registration-queue
     * where it will be queued for
     * registration into the game.
     *
     * @return {@code this}.
     */
    ShopperyItem queueRegistration(){
        if(isQueued)
            throw new IllegalStateException(
                    String.format("Item: %s already queued for registration.", this.getClass().getCanonicalName())
            );

        Items.queueItem(this);
        isQueued = true;

        return this;
    }
}
