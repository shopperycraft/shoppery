package com.ki11erwolf.shoppery.item;

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
    private static Queue<ShopperyItem> items = new ArrayDeque<>();

    //############################
    //   Public Item Instances
    //############################

    /**
     * The one(1) cents coin item.
     */
    public static final Coin COIN_ONE_CENT = new Coin("one", (byte)1).queueRegistration();          //1

    /**
     * The five(5) cents coin item.
     */
    public static final Coin COIN_FIVE_CENT = new Coin("five", (byte)5).queueRegistration();        //2

    /**
     * The ten(10) cents coin item.
     */
    public static final Coin COIN_TEN_CENT = new Coin("ten", (byte)10).queueRegistration();         //3

    /**
     * The twenty(20) cents coin item.
     */
    public static final Coin COIN_TWENTY_CENT = new Coin("twenty", (byte)20).queueRegistration();   //4

    /**
     * The fifty(50) cents coin item.
     */
    public static final Coin COIN_FIFTY_CENT = new Coin("fifty", (byte)50).queueRegistration();     //5

    /**
     * The eighty(80) cents coin item.
     */
    public static final Coin COIN_EIGHTY_CENT = new Coin("eighty", (byte)80).queueRegistration();   //6

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
        if(items == null)
            return;

        items.iterator().forEachRemaining(item -> event.getRegistry().register(item));
        items.clear();
        items = null;
    }

    /**
     * Adds a Shoppery Item instance
     * to the item registration queue.
     *
     * @param item the item to queue.
     */
    static void queueItem(ShopperyItem item){
        items.add(item);
    }
}
