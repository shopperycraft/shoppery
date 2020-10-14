package com.ki11erwolf.shoppery.price;

import com.google.gson.Gson;
import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.Random;

/**
 * The public price registry API. Provides access to the
 * {@link PriceRegistry}.
 *
 * <p/>The underlying registry must be completely {@link ItemPrices#isLoaded()}
 * before it can be used. This action is only completed after the
 * {@link net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent}.
 *
 * <p/><b>Registry Loader</b> - The registry is loaded by a separate thread
 * that will attempt to get all ItemPrices from Shoppery itself,
 * other mods, and any other files that provide ItemPrices (done through
 * Loaders). This process is independent of forge, and registered blocks and items;
 * and should be done as soon as possible (preferably in the mod class constructor).
 * The loader will then print out a summary of what it loaded.
 *
 * <p/><b>Registry Cleaner</b> - The registry is cleaned: checked for ItemPrices
 * that don't give a price for an actual item or block in the forge registries,
 * by another separate thread that is scheduled to run by this class when both
 * the this registry and the forge registries are loaded.
 * The cleaner thread will check each ItemPrice in the registry and make sure it
 * points to a valid item (checked first) or block (checked second) in the forge
 * registries, if it doesn't, it will be removed from the registry. Once finished,
 * the cleaner thread will also print out a summary of what it did (nothing if
 * everything is done correctly).
 * Due to the way the registry is loaded it's possible it contains prices
 * that don't actually point to a valid item or block, and hence the cleaner
 * is needed to check for this. This also means the registry should not
 * be used until cleaned or it could give out these ItemPrices and break
 * things down the line.
 *
 * <p/>The registry is effectively loaded (loaded and cleaned) using
 * separate threads to reduce the time Forge takes to load Shoppery
 * and hence reduce the time Minecraft takes to open.
 */
public enum ItemPrices {

    /**
     * The global instance that points to a single ItemPrices
     * object, which can be used as an alternative to the
     * static methods.
     */
    INSTANCE;

    /**
     * A reusable {@link Gson} object instance for the Price
     * system. It's preferable to reuse the same object rather
     * than create a new one each time.
     */
    public static final Gson GSON_INSTANCE = new Gson();

    /**
     * The directory on disk where
     */
    public static final File PRICES_DIRECTORY =
            new File(ShopperyMod.SHOPPERY_DIRECTORY + "/prices/");

    /**
     * The minimum number of entries the price registry is expected
     * to load on every run from Shoppery. This is used to set
     * List/Map initial capacities.
     *
     * <p/>This number should always be above the amount of entries
     * Shoppery loads for Minecraft from prices.json. Be sure to
     * keep space for additional entries that may be added as well.
     */
    public static final int ENTRIES_EXPECTED = 50 + 595;

    // ****
    // Load
    // ****

    /**
     * Instructs the price registry to begin loading itself
     * from all possible sources supplying price information.
     * This should be called as soon as possible!
     *
     * The registry can only be used after the load is
     * complete. Any repeated calls to this method will
     * result in an exception being thrown.
     *
     * @throws IllegalStateException if the registry is
     * in the process of loading itself, or it is already loaded.
     */
    public static void loadPriceRegistry(){
        PriceRegistry.INSTANCE.load();
    }

    /**
     * @return {@code true} only once the price
     * registry has been loaded (and cleaned).
     * The registry is only safe to use once this
     * method returns {@code true}.
     */
    public static boolean isLoaded(){
        return PriceRegistry.INSTANCE.isLoadedAndCleaned();
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
        PriceRegistry.INSTANCE.assertUsable();
        return PriceRegistry.INSTANCE.getPriceMap().get(stack.getItem().getRegistryName());
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
        PriceRegistry.INSTANCE.assertUsable();
        return PriceRegistry.INSTANCE.getPriceMap().get(item.getRegistryName());
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
        PriceRegistry.INSTANCE.assertUsable();
        return PriceRegistry.INSTANCE.getPriceMap().get(block.getRegistryName());
    }

    /**
     * Allows obtaining the price of an Item or Block from
     * the registry name of the Item/Block as a resource
     * location.
     *
     * @param item the registry name of the Item/Block to
     *             get the price of.
     * @return the ItemPrice for the Item/Block matching
     * the given registry name, or {@code null} if no
     * ItemPrice for the given Item/Block could be found.
     */
    public static ItemPrice getPrice(ResourceLocation item){
        PriceRegistry.INSTANCE.assertUsable();
        return PriceRegistry.INSTANCE.getPriceMap().getOrDefault(item, null);
    }

    /**
     * Allows obtaining an ItemPrice contained within
     * the registry completely randomly.
     *
     * @return a random ItemPrice in the registry.
     */
    public static ItemPrice getRandomPrice(){
        PriceRegistry.INSTANCE.assertUsable();

        int randomIndexInRage = MathUtil.getRandomIntegerInRange(
                0, PriceRegistry.INSTANCE.getPriceMap().size() - 1
        );
        return PriceRegistry.INSTANCE.getPriceMap().values().toArray(new ItemPrice[0])[randomIndexInRage];
    }

    /**
     * Allows obtaining an ItemPrice contained within
     * the registry completely randomly.
     *
     * @param random the random object instance to use
     *               to get the random item price.
     * @return a random ItemPrice in the registry.
     */
    public static ItemPrice getRandomPrice(Random random){
        PriceRegistry.INSTANCE.assertUsable();

        int randomIndexInRage = MathUtil.getRandomIntegerInRange(
                random, 0, PriceRegistry.INSTANCE.getPriceMap().size() - 1
        );
        return PriceRegistry.INSTANCE.getPriceMap().values().toArray(new ItemPrice[0])[randomIndexInRage];
    }

    // *******
    // Setters
    // *******

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param price the item we're changing the price of, as
     *              well the price we're changing it to, both
     *              contained in an {@link ItemPrice}
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public static boolean setPrice(ItemPrice price){
        PriceRegistry.INSTANCE.assertUsable();
        return PriceRegistry.INSTANCE.modifyPrice(price);
    }

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param item the item we're changing the price of.
     * @param buy the buy price of the item. Will be double the
     *            sell price.
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public static boolean setPrice(IItemProvider item, double buy){
        return setPrice(new ItemPrice(item.asItem().getRegistryName(), buy, buy / 2));
    }

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param itemID the item we're changing the price of,
     *               referred to by its name/ID in the form of a
     *               ResourceLocation.
     * @param buy the buy price of the item. Will be double the
     *            sell price.
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public static boolean setPrice(ResourceLocation itemID, double buy){
        return setPrice(new ItemPrice(itemID, buy, buy / 2));
    }

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param item the item we're changing the price of.
     * @param buy the buy price of the item.
     * @param sell the selling price of the item.
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public static boolean setPrice(IItemProvider item, double buy, double sell){
        return setPrice(new ItemPrice(item.asItem().getRegistryName(), buy, sell));
    }

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param itemID the item we're changing the price of,
     *               referred to by its name/ID in the form of a
     *               ResourceLocation.
     * @param buy the buy price of the item. Will be double the
     *            sell price.
     * @param sell the selling price of the item.
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public static boolean setPrice(ResourceLocation itemID, double buy, double sell){
        return setPrice(new ItemPrice(itemID, buy, sell));
    }

    // ****************
    // Instance Getters
    // ****************

    /**
     * Allows obtaining the price of an Item or Block
     * in an ItemStack. Will block until the price registry
     * is loaded if called before.
     *
     * @param stack the item stack containing the item or block.
     * @return the ItemPrice for the item/block or {@code null}
     * if the item/block does not have a price.
     */
    public ItemPrice getItemPrice(ItemStack stack){
        return getPrice(stack);
    }

    /**
     * Allows obtaining the price of an Item. Will block
     * until the price registry is loaded if called before.
     *
     * @param item the item to get the price for.
     * @return the ItemPrice for the item or {@code null}
     * if the item does not have a price.
     */
    public ItemPrice getItemPrice(Item item){
        return getPrice(item);
    }

    /**
     * Allows obtaining the price of a Block. Will block
     * until the price registry is loaded if called before.
     *
     * @param block the block to get the price for.
     * @return the ItemPrice for the block or {@code null}
     * if the block does not have a price.
     */
    public ItemPrice getItemPrice(Block block){
        return getPrice(block);
    }

    /**
     * Allows obtaining an ItemPrice contained within
     * the registry completely randomly.
     *
     * @return a random ItemPrice in the registry.
     */
    public static ItemPrice getRandomItemPrice(){
        return getRandomPrice();
    }

    /**
     * Allows obtaining an ItemPrice contained within
     * the registry completely randomly.
     *
     * @param random the random object instance to use
     *               to get the random item price.
     * @return a random ItemPrice in the registry.
     */
    public static ItemPrice getRandomItemPrice(Random random){
        return getRandomPrice(random);
    }

    // ****************
    // Instance Setters
    // ****************

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param price the item we're changing the price of, as
     *              well the price we're changing it to, both
     *              contained in an {@link ItemPrice}
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public boolean setItemPrice(ItemPrice price){
        return setPrice(price);
    }

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param item the item we're changing the price of.
     * @param buy the buy price of the item. Will be double the
     *            sell price.
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public boolean setItemPrice(IItemProvider item, double buy){
        return setPrice(item, buy);
    }

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param itemID the item we're changing the price of,
     *               referred to by its name/ID in the form of a
     *               ResourceLocation.
     * @param buy the buy price of the item. Will be double the
     *            sell price.
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public boolean setItemPrice(ResourceLocation itemID, double buy){
        return setPrice(itemID, buy);
    }

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param item the item we're changing the price of.
     * @param buy the buy price of the item.
     * @param sell the selling price of the item.
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public boolean setItemPrice(IItemProvider item, double buy, double sell){
        return setPrice(item, buy, sell);
    }

    /**
     * Allows setting, or alternatively, changing the price of
     * the given item to the price provided, within the price
     * registry. The price set/change is persistent, and
     * therefore persists across Minecraft Launches & Registry
     * loads, effectively making it permanent until changed again.
     *
     * @param itemID the item we're changing the price of,
     *               referred to by its name/ID in the form of a
     *               ResourceLocation.
     * @param buy the buy price of the item. Will be double the
     *            sell price.
     * @param sell the selling price of the item.
     * @return {@code true} only if: the item is a valid item, is
     * allowed to have a price, and the price change was updated
     * in the active registry and on file.
     */
    public boolean setItemPrice(ResourceLocation itemID, double buy, double sell){
        return ItemPrices.setPrice(itemID, buy, sell);
    }
}
