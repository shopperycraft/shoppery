package com.ki11erwolf.shoppery.item;

import com.ki11erwolf.shoppery.ShopperyTab;
import com.ki11erwolf.shoppery.ShopperyMod;
import net.minecraft.item.Item;

/**
 /**
 * Base class for all Shoppery mod Items.
 *
 * <p>Extends upon Minecraft's base {@link Item} class
 * to add: a native {@link #queueRegistration() register}
 * system & method, as well as other utilities.
 *
 * @generic T the Item child class inheriting from this class.
 */
@SuppressWarnings("WeakerAccess")
public class ModItem<T extends ModItem<T>> extends Item {

    /**
     * Flag to prevent queuing an item
     * more than once.
     */
    private boolean isQueued = false;

    /**
     * Package private constructor to prevent
     * item instance creation from outside
     * packages. Creates a new blank{@link
     * Item.Properties}.
     *
     *
     * @param registryName the name to register
     *                     this item under.
     */
    ModItem(String registryName){
        this(new Item.Properties(), registryName);
    }

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
    ModItem(Properties properties, String registryName) {
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
    ModItem(Properties properties, boolean group, String registryName){
        super((group) ? setItemGroup(properties) : properties);
        this.setRegistryName(ShopperyMod.MODID, registryName);
    }

    /**
     * Adds this item instance to
     * the items registration queue
     * where it will be queued for
     * registration into the game.
     *
     * @return {@code this}.
     */
    T queueRegistration(){
        if(isQueued)
            throw new IllegalStateException(String.format(
                "Item: %s already queued for registration.",
                    this.getClass().getCanonicalName())
            );

        ModItems.queueItem(this);
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
        return properties.group(ShopperyTab.INSTANCE);
    }
}
