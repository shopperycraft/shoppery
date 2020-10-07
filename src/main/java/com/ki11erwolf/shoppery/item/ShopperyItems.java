package com.ki11erwolf.shoppery.item;

import com.ki11erwolf.shoppery.util.QueueRegisterer;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Holds all instances of shoppery items and handles
 * the registration of them.
 */
public final class ShopperyItems extends QueueRegisterer<Item> {

    /**
     * Private instance of this class.
     */
    public static final ShopperyItems ITEMS = new ShopperyItems();
    private ShopperyItems(){}

    //############################
    //   Public Item Instances
    //############################

    /**
     * The one(1) cents coin item.
     */
    public static final CoinItem COIN_ONE = new CoinItem("one", (byte)1).queueRegistration();          //1

    /**
     * The five(5) cents coin item.
     */
    public static final CoinItem COIN_FIVE = new CoinItem("five", (byte)5).queueRegistration();        //2

    /**
     * The ten(10) cents coin item.
     */
    public static final CoinItem COIN_TEN = new CoinItem("ten", (byte)10).queueRegistration();         //3

    /**
     * The twenty(20) cents coin item.
     */
    public static final CoinItem COIN_TWENTY = new CoinItem("twenty", (byte)20).queueRegistration();   //4

    /**
     * The fifty(50) cents coin item.
     */
    public static final CoinItem COIN_FIFTY = new CoinItem("fifty", (byte)50).queueRegistration();     //5

    /**
     * The eighty(80) cents coin item.
     */
    public static final CoinItem COIN_EIGHTY = new CoinItem("eighty", (byte)80).queueRegistration();   //6

    /**
     * The one(1) bill note.
     */
    public static final NoteItem NOTE_ONE = new NoteItem("one", 1).queueRegistration();         //1

    /**
     * The five(5) bill note.
     */
    public static final NoteItem NOTE_FIVE = new NoteItem("five", 5).queueRegistration();       //2

    /**
     * The ten(10) bill note.
     */
    public static final NoteItem NOTE_TEN = new NoteItem("ten", 10).queueRegistration();        //3

    /**
     * The twenty(20) bill note.
     */
    public static final NoteItem NOTE_TWENTY = new NoteItem("twenty", 20).queueRegistration();  //4

    /**
     * The fifty(50) bill note.
     */
    public static final NoteItem NOTE_FIFTY = new NoteItem("fifty", 50).queueRegistration();    //5

    /**
     * The one-hundred(100) bill note.
     */
    public static final NoteItem NOTE_ONE_HUNDRED                                                                //6
            = new NoteItem("one_hundred", 100).queueRegistration();

    /**
     * The five-hundred(500) bill note.
     */
    public static final NoteItem NOTE_FIVE_HUNDRED                                                               //1
            = new NoteItem("five_hundred", 500).queueRegistration();

    /**
     * The one-thousand(1,000) bill note.
     */
    public static final NoteItem NOTE_ONE_K = new NoteItem("one_k", 1_000).queueRegistration(); //2

    /**
     * The five-thousand(5,000) bill note.
     */
    public static final NoteItem NOTE_FIVE_K = new NoteItem("five_k", 5_000).queueRegistration();//3

    /**
     * The ten-thousand(10,000) bill note.
     */
    public static final NoteItem NOTE_TEN_K = new NoteItem("ten_k", 10_000).queueRegistration();//4

    /**
     * The fifty-thousand(50,000) bill note.
     */
    public static final NoteItem NOTE_FIFTY_K                                                                    //5
            = new NoteItem("fifty_k", 50_000).queueRegistration();

    /**
     * The one-hundred-thousand(100,000) bill note.
     */
    public static final NoteItem NOTE_ONE_HUNDRED_K                                                              //6
            = new NoteItem("one_hundred_k", 100_000).queueRegistration();

    //############################
    //     Item Registration
    //############################

    /**
     * Forge item register event.
     *
     * <p>Iteratively registers every shoppery item queued
     * for registration ({@link ShopperyItem#queueRegistration()}
     * to the game using forges item registration event.
     *
     * @param event forge event.
     */
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        this.iterateQueue(item -> event.getRegistry().register(item));
    }

    /**
     * Adds a Shoppery Item instance
     * to the item registration queue.
     *
     * @param item the item to queue.
     */
    static void queueItem(ShopperyItem<?> item){
        ITEMS.queueForRegistration(item);
    }
}
