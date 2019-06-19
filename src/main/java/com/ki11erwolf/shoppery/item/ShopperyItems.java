package com.ki11erwolf.shoppery.item;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.QueueRegisterer;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Shoppery items registry, container &
 * initialization logic.
 */
@SuppressWarnings("unused")//References found by reflection, or are self registering.
@Mod.EventBusSubscriber(modid = ShopperyMod.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ShopperyItems extends QueueRegisterer<Item> {

    /**
     * Private instance of this class.
     */
    private static final ShopperyItems INSTANCE = new ShopperyItems();
    private ShopperyItems(){}

    //############################
    //   Public Item Instances
    //############################

    public static final DepositItem DEPOSIT_ITEM = new DepositItem().queueRegistration();

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
    public static final NoteItem NOTE_ONE = new NoteItem("one", 1).queueRegistration();                   //1

    /**
     * The five(5) bill note.
     */
    public static final NoteItem NOTE_FIVE = new NoteItem("five", 5).queueRegistration();                 //2

    /**
     * The ten(10) bill note.
     */
    public static final NoteItem NOTE_TEN = new NoteItem("ten", 10).queueRegistration();                  //3

    /**
     * The twenty(20) bill note.
     */
    public static final NoteItem NOTE_TWENTY = new NoteItem("twenty", 20).queueRegistration();            //4

    /**
     * The fifty(50) bill note.
     */
    public static final NoteItem NOTE_FIFTY = new NoteItem("fifty", 50).queueRegistration();              //5

    /**
     * The one-hundred(100) bill note.
     */
    public static final NoteItem NOTE_ONE_HUNDRED = new NoteItem("one_hundred", 100).queueRegistration(); //6

    /**
     * The five-hundred(500) bill note.
     */
    public static final NoteItem NOTE_FIVE_HUNDRED = new NoteItem("five_hundred", 500).queueRegistration(); //1

    /**
     * The one-thousand(1,000) bill note.
     */
    public static final NoteItem NOTE_ONE_K = new NoteItem("one_k", 1_000).queueRegistration();             //2

    /**
     * The five-thousand(5,000) bill note.
     */
    public static final NoteItem NOTE_FIVE_K = new NoteItem("five_k", 5_000).queueRegistration();           //3

    /**
     * The ten-thousand(10,000) bill note.
     */
    public static final NoteItem NOTE_TEN_K = new NoteItem("ten_k", 10_000).queueRegistration();            //4

    /**
     * The fifty-thousand(50,000) bill note.
     */
    public static final NoteItem NOTE_FIFTY_K = new NoteItem("fifty_k", 50_000).queueRegistration();        //5

    /**
     * The one-hundred-thousand(100,000) bill note.
     */
    public static final NoteItem NOTE_ONE_HUNDRED_K                                                                      //6
            = new NoteItem("one_hundred_k", 100_000).queueRegistration();

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
        INSTANCE.iterateQueue(item -> event.getRegistry().register(item));
    }

    /**
     * Adds a Shoppery Item instance
     * to the item registration queue.
     *
     * @param item the item to queue.
     */
    static void queueItem(ShopperyItem item){
        INSTANCE.queueForRegistration(item);
    }
}
