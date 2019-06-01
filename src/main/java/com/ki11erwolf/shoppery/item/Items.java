package com.ki11erwolf.shoppery.item;

import com.ki11erwolf.shoppery.ShopperyItemGroup;
import com.ki11erwolf.shoppery.ShopperyMod;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Shoppery items registry, container &
 * initialization logic.
 */
@SuppressWarnings("unused")//References found by reflection.
@Mod.EventBusSubscriber(modid = ShopperyMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class Items {

    /**
     * Queue holding every shoppery item
     * that needs to be registered to the game.
     *
     * This queue is used to programmatically
     * register items to the game, after which
     * it is nullified (made {@code null}) as
     * it has no further purpose to store item
     * references.
     */
    private static final Queue<ShopperyItem> ITEMS
            = new ArrayDeque<>();

    //############################
    //   Public Item Instances
    //############################

    public static final ShopperyItem TEST_ITEM = new ShopperyItem(
            new Item.Properties()
                    .group(ShopperyItemGroup.SHOPPERY_ITEM_GROUP),
            "test_item"
    ).queueRegistration();

    //############################
    //    Registration Logic
    //############################

    /**
     * Forge item register event.
     *
     * Iteratively registers every queued shoppery
     * item class.
     *
     * @param event forge event.
     */
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ITEMS.iterator().forEachRemaining(item -> event.getRegistry().register(item));
    }

    /**
     * Adds a Shoppery Item instance
     * to the item registration queue.
     *
     * @param item the item to queue.
     */
    static void queueItem(ShopperyItem item){
        ITEMS.add(item);
    }
}
