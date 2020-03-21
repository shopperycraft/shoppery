package com.ki11erwolf.shoppery.price;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static com.ki11erwolf.shoppery.price.PriceRegistry.INSTANCE;

/**
 * Static methods used to interface with the price registry. This
 * includes getters, setters and a few other miscellaneous methods.
 * Any interactions with the price registry must be done through
 * this class.
 */
public class ItemPrices {

    /**
     * The minimum number of entries the price registry is expected
     * to load on every run from Shoppery. This is used to set
     * List/Map initial capacities.
     */
    //The amount of entries in shoppery-prices.json basically.
    public static final int minExpectedNumberOfEntries = 600;

    /**Private constructor.*/
    private ItemPrices(){}

    // ****
    // Load
    // ****

    /**
     * Instructs the price registry to begin loading
     * itself. Should be called as soon as possible,
     * can only be called once. The registry can only
     * be used after the load is complete.
     */
    public static void loadPriceRegistry(){
        INSTANCE.load();
    }

    /**
     * @return {@code true} only once the price
     * registry has been loaded (and cleaned).
     * The registry is only safe to use once this
     * method returns {@code true}.
     */
    public static boolean isLoaded(){
        return INSTANCE.isLoadedAndCleaned();
    }

    // *******
    // Getters
    // *******

    /**
     * Allows obtaining the price of an Item or Block
     * in an ItemStack. Will block until the price registry
     * is loaded if called before.
     *
     * @param stack the item stack containing the item or block.
     * @return the ItemPrice for the item/block or {@code null}
     * if the item/block does not have a price.
     */
    public static ItemPrice getPrice(ItemStack stack){
        blockUntilLoaded();
        return INSTANCE.getPriceMap().get(stack.getItem().getRegistryName());
    }

    /**
     * Allows obtaining the price of an Item. Will block
     * until the price registry is loaded if called before.
     *
     * @param item the item to get the price for.
     * @return the ItemPrice for the item or {@code null}
     * if the item does not have a price.
     */
    public static ItemPrice getPrice(Item item){
        blockUntilLoaded();
        return INSTANCE.getPriceMap().get(item.getRegistryName());
    }

    /**
     * Allows obtaining the price of a Block. Will block
     * until the price registry is loaded if called before.
     *
     * @param block the block to get the price for.
     * @return the ItemPrice for the block or {@code null}
     * if the block does not have a price.
     */
    public static ItemPrice getPrice(Block block){
        blockUntilLoaded();
        return INSTANCE.getPriceMap().get(block.getRegistryName());
    }

    // ****
    // Util
    // ****

    /**
     * Will block the calling thread (using Thread.sleep(10))
     * until the Price Registry has completely loaded, or returns
     * if the Price Registry is already loaded.
     */
    private static void blockUntilLoaded(){
        if(INSTANCE.isLoadedAndCleaned())
            return;

        while(!INSTANCE.isLoadedAndCleaned()){
            try {
                //Keep thread usage down
                Thread.sleep(10);
            } catch (InterruptedException e) {
                //NO-OP - Should not happen.
            }
        }
    }
}
