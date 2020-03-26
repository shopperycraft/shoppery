package com.ki11erwolf.shoppery.item;

import com.ki11erwolf.shoppery.ShopperyItemGroup;
import com.ki11erwolf.shoppery.ShopperyMod;
import net.minecraft.item.Item;

/**
 * Base class for all shoppery items.
 *
 * @generic T the class inheriting from this class.
 */
@SuppressWarnings("WeakerAccess")
public class ShopperyItem<T extends ShopperyItem<T>> extends Item {

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
        super(setItemGroup(properties));
        this.setRegistryName(ShopperyMod.MODID, registryName);
    }

    /**
     * Constructor that allows turning off
     * the setting of the ItemGroup.
     *
     * Package private constructor to prevent
     * item instance creation from outside
     * packages.
     *
     * @param properties properties specific
     *                   to this item.
     * @param group {@code false} to turn off setting
     *                           the ItemGroup
     * @param registryName the name to register
     *                     this item under.
     */
    ShopperyItem(Properties properties, boolean group, String registryName){
        super((group) ? setItemGroup(properties) : properties);
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
    T queueRegistration(){
        if(isQueued)
            throw new IllegalStateException(
                    String.format("Item: %s already queued for registration.", this.getClass().getCanonicalName())
            );

        ShopperyItems.queueItem(this);
        isQueued = true;

        //noinspection unchecked //Should NOT be possible.
        return (T)this;
    }

    /**
     * Util method to set the item group of the given
     * properties object before passing it to the
     * super class constructor.
     *
     * @param properties given properties object.
     * @return updated properties object.
     */
    private static Properties setItemGroup(Properties properties){
        return properties.group(ShopperyItemGroup.INSTANCE);
    }
}
