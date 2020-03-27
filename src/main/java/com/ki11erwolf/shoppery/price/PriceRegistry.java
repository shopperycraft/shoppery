package com.ki11erwolf.shoppery.price;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.price.loaders.Loader;
import com.ki11erwolf.shoppery.price.loaders.ModPricesLoader;
import com.ki11erwolf.shoppery.price.loaders.Results;
import com.ki11erwolf.shoppery.price.loaders.ShopperyPricesLoader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * The singleton price registry instance that holds
 * and maintains the map of ItemPrices and their
 * corresponding block/item as a ResourceLocation.
 * <p/>
 * This class provides no accessors. Use the PriceAPI
 * class to get access to the registry.
 * <p/>
 * The registry api should not allow access to the
 * registry until the registry is loaded and cleaned.
 * <p/>
 * This class is responsible for 4 things:
 * <ul>
 *     <li>Hold the map of item prices and their block/item.</li>
 *     <li>Load entries into the registry from the given
 *     Loaders (done on the "shoppery-price-registry-loader" thread).</li>
 *     <li>Clean the registry of ItemPrices with no item or block
 *     once both it and the forge registries have finished loading
 *     (done on the "shoppery-price-registry-cleaner" thread).</li>
 *     <li>Provide basic package-private accessors so that a solid API can be
 *     built atop of the registry.</li>
 * </ul>
 * <p/>
 * <b>Registry Loader</b> - The registry is loaded by a separate thread
 * that will attempt to get all ItemPrices from Shoppery itself,
 * other mods, and any other files that provide ItemPrices (done through
 * Loaders). This process is independent of forge, and registered blocks and items;
 * and should be done as soon as possible (preferably in the mod class constructor).
 * The loader will then print out a summary of what it loaded.
 * <p/>
 * <b>Registry Cleaner</b> - The registry is cleaned: checked for ItemPrices
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
 * <p/>
 * The registry is effectively loaded (loaded and cleaned) using
 * separate threads to reduce the time Forge takes to load Shoppery
 * and hence reduce the time Minecraft takes to open.
 */
enum PriceRegistry {

    /**
     * The singleton instance of this registry.
     * The various loaders used to build the registry
     * are defined here (run in order of definition).
     */
    INSTANCE(
        new ShopperyPricesLoader(),
        new ModPricesLoader()
    );

    // *************
    // Protected API
    // *************

    /**
     * Will attempt to immediately begin loading the registry
     * on its separate loader thread. Calling this method
     * will also schedule the cleaner thread to run when
     * both the forge registries and this registry are
     * loaded.
     * <p/>
     * The registry can only be loaded once and can only be
     * used after it has been loaded and cleaned.
     *
     * @throws IllegalStateException if the registry is in
     * the process of loading itself, or it is already loaded.
     */
    void load(){
        if(isLoaded)
            throw new IllegalStateException("Already loaded!");

        if(isLoading)
            throw new IllegalStateException("Already loading registry!");

        isLoading = true;

        LOG.info("Creating Shoppery price registry...");
        //Start the thread which will actually load the registry
        //and do the heavy lifting
        new LoaderThread().start();

        //Schedule running the cleaner thread
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLLoadComplete);
    }

    /**
     * @return {@code true} if and only if the price
     * registry has been both loaded and cleaned. Once
     * this returns {@code true}, it's safe to use
     * the registry.
     */
    boolean isLoadedAndCleaned(){
        return isLoaded && hasCleanerThreadRun;
    }

    /**
     * @return the map object used to store the registers
     * ItemPrices.
     */
    Map<ResourceLocation, ItemPrice> getPriceMap(){
        return getMap();
    }

    // ********
    // Registry
    // ********

    /**
     * The logger for this class.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

    /**
     * The synchronous lock object used to lock access
     * to the registers map.
     */
    private static final Object PRICE_MAP_LOCK = new Object();

    /**
     * The registries backing map that stores all its
     * ItemPrices mapped to the item/block registry name
     * (ResourceLocation).
     */
    //LinkedHashMap - we want a predictable iteration order.
    private final Map<ResourceLocation, ItemPrice> priceMap
            = new LinkedHashMap<>(ItemPrices.minExpectedNumberOfEntries);

    /**
     * Flag set to true once the registry has been completely
     * loaded (excluding clean).
     */
    private volatile boolean isLoaded = false;

    /**
     * Flag set to true when the {@link #load()} method
     * has been called.
     */
    private boolean isLoading = false;

    /**
     * The given list of loaders that will be executed
     * to provide the entries for this registry.
     */
    private Loader[] loaders;

    /**
     * The amount of time (in milliseconds) it took for
     * the LoaderThread to fully load the registry.
     */
    private long loadTime;

    /**
     * @param loaders a list of newly constructed loaders
     *                that will provide the registry with
     *                its entries. The loaders are executed
     *                in the order they are provided.
     */
    PriceRegistry(Loader... loaders){
        this.loaders = loaders;
    }

    /**
     * Provides synchronous access to the registries backing price map.
     *
     * @return the registries {@link #priceMap}.
     */
    private synchronized Map<ResourceLocation, ItemPrice> getMap(){
        synchronized (PRICE_MAP_LOCK) {
            return priceMap;
        }
    }

    // ******
    // Loader
    // ******

    /**
     * The thread responsible for executing the given Loaders
     * and appending their entries to the registry price map.
     * After all Loaders have been executed, the LoaderThread
     * will then print the results of the loaders.
     * <p/>
     * This thread and the Loaders (including resources) will
     * hopefully be reclaimed by the garbage collector after
     * execution. The loaders list is also nulled out after
     * this thread has run.
     */
    private class LoaderThread extends Thread {

        /**
         * The list of {@link Results} from the executed
         * loaders.
         */
        private final List<Results> resultsList = new ArrayList<>();

        /**
         * The names of loaders that have failed, if any.
         */
        private final List<String> failedLoaders = new ArrayList<>();

        /**
         * The list of mods by name that have had prices added for.
         */
        private final List<String> affectedMods = new ArrayList<>();

        /**
         * Sets the threads name and daemon status.
         */
        LoaderThread(){
            super("shoppery-price-registry-loader");
            this.setDaemon(true);
            this.setUncaughtExceptionHandler(((t, e) -> LOG.error("Uncaught exception on loader thread", e)));
        }

        /**
         * Executes the loaders and appends their
         * entries to the registry, calculates the
         * time it took, and prints out the results.
         */
        @Override
        public void run(){
            loadTime = System.currentTimeMillis();

            //Run the loaders and append values to the map.
            runLoaders();

            //Consider finished loading
            LOG.info("Finished building Shoppery price registry!");
            isLoaded = true;
            loadTime = System.currentTimeMillis() - loadTime;


            //Print out results.
            LOG.info("Printing results...");
            for(Results result : resultsList){
                result.print();
            }

            //Print out summary
            printRegistrySummary();

            //Clean unused resources
            clean();
        }

        /**
         * Iterates over the registries loaders and executes
         * them in order, appending their entries to the
         * registry map. This also handles the
         * loaders results to some degree.
         */
        private void runLoaders(){
            for(Loader loader : loaders){
                LOG.info("Loading: " + loader.getClass().getSimpleName());
                Results results = loader.getResults();

                //Get values
                ItemPrice[] prices;
                try{
                    prices = loader.load();
                } catch (Exception e){
                    LOG.fatal(
                            "Shoppery price registry loader: "
                                    + loader.getClass().getSimpleName()
                                    + "has failed!",
                            e
                    );
                    results.logError(e.getMessage());
                    results.flagAsErrored();
                    prices = null;
                }

                results.setName(loader.getClass().getSimpleName());
                resultsList.add(results);

                if(prices == null || loader.hasErrored()){
                    failedLoaders.add(results.getName());
                } else {
                    results.setNumberOfEntries(prices.length);
                    appendToAffectedMods(results.getAffectedMods());
                    //Add to registry map
                    int replacements = addToMap(prices);
                    results.setNumberOfReplacements(replacements);
                }
            }
        }

        /**
         * Adds the given list of ItemPrices obtained from
         * a loader to the registries map.
         *
         * @param prices the list of ItemPrices.
         * @return the amount of entries replaced
         * in the map with entries from the list,
         * if any.
         */
        private int addToMap(ItemPrice[] prices){
            int replacements = 0;

            for(ItemPrice price : prices){
                if(getMap().put(price.getItem(), price) != null)
                    replacements++;
            }

            return replacements;
        }

        /**
         * Adds the given list of mod ids that
         * were affected to the list to be displayed
         * in the printed results.
         *
         * @param mods the list of affected mods.
         */
        private void appendToAffectedMods(String[] mods){
            if(mods != null)
                for(String mod : mods){
                    if(!affectedMods.contains(mod))
                        affectedMods.add(mod);
                }
        }

        /**
         * Prints out a summary of registry (and load)
         * to the console. This includes the number
         * of entries, load time, mods affected by
         * shoppery (via ItemPrices) and the amount
         * of loaders processed and failed.
         */
        private void printRegistrySummary(){
            StringBuilder ret = new StringBuilder("\n");

            ret.append("------------- Registry Summary -------------\n");
            ret.append("Total Entries: ").append(getMap().size()).append("\n");
            ret.append("Load Time: ").append(loadTime)
                    .append("ms (").append(((double)loadTime/1000)).append("s)").append("\n");
            ret.append("Mods affected: ").append(Arrays.toString(affectedMods.toArray(new String[0]))).append("\n");
            ret.append("Loaders Processed: ").append(loaders.length).append("\n");
            ret.append("Loaders Failed: ").append(failedLoaders.size()).append("\n");
            ret.append("--------------------------------------------");

            LOG.info(ret);
        }

        /**
         * Nulls out references to any resources we no
         * longer needed and requests garbage collection
         * in attempt to reduce memory footprint.
         */
        private void clean(){
            LOG.debug("Freeing price registry resources...");

            //Remove references to the loaders. The loaders are responsible
            //for most of the memory usage when constructing the registry.
            //We remove them when they have served their purpose which will
            //also remove references to any other objects used during construction.
            loaders = null;
            //Call GC in an attempt to make the garbage collector remove
            //the now nulled out loader references. Appears to work.
            System.gc();//The loader thread exists after this call.
            //By the end (after this thread has been removed by gc as well),
            //we should only be left with the PriceRegistry instance, and the
            //HashMap of ItemPrices.
        }
    }

    // *******
    // Cleaner
    // *******

    /**
     * Flag set to true once the cleaner thread
     * has cleaned the registry. This may be
     * true while the thread is still running.
     */
    private boolean hasCleanerThreadRun = false;

    /**
     * Called when Forge Mod Loader has finished
     * loading all the mods. By the point this is
     * called, the Item and Block registries have
     * been loaded and frozen; and it's safe
     * to check for existing blocks and items.
     * <p/>
     * Will run the cleaner thread if it hasn't yet run.
     *
     * @param event forge event.
     */
    @SubscribeEvent
    public void onFMLLoadComplete(FMLLoadCompleteEvent event){
        //Prevent multiple runs just in case.
        if(hasCleanerThreadRun)
            return;

        //By this point, all items and blocks are registered
        //to the game and all registries are frozen. Now
        //we will scan the price registry for entries that don't
        //have an item or block in the forge registries to match.

        //Start the thread which will scan the registry and
        //remove entries without a matching item/block.
        new CleanerThread().start();
    }

    /**
     * Thread responsible for iterating over the registry
     * and ensuring every ItemPrice gives a price for a
     * valid Item or Block in the forge registries.
     * Any invalid ItemPrices will be removed by the
     * Clean Thread. An additional summary is printed
     * by this thread after the clean including info
     * about removed entries.
     */
    private class CleanerThread extends Thread{

        /**
         * The list of ItemPrices that were removed from the
         * registry by the cleaner due to not having a existing
         * item/block in the forge registries.
         */
        private List<ItemPrice> removedPrices = new ArrayList<>();

        /**
         * The amount of time (in milliseconds) it took
         * for the cleaner to clean the registry.
         */
        private long cleanTime;

        /**
         * Sets the thread name and daemon status.
         */
        CleanerThread(){
            super("shoppery-price-registry-cleaner");
            this.setDaemon(true);
            this.setUncaughtExceptionHandler(((t, e) -> LOG.error("Uncaught exception on cleaner thread", e)));
        }

        /**
         * Begins cleaning the registry of all entries that
         * don't give a price for an existing item/block in
         * the forge registries once the registry has finished
         * loaded. Prints out the results of the clean afterwards.
         */
        @Override
        public void run(){
            //Wait until the registry is actually loaded
            //before we clean it.
            while(!isLoaded){
                LOG.debug("Waiting for registry to finish loading...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //Meh
                }
            }

            //Clean the registry
            LOG.info("Cleaning price registry...");
            cleanTime = System.currentTimeMillis();
            cleanRegistry();
            cleanTime = System.currentTimeMillis() - cleanTime;
            //It's cleaned by this point. We just need to finish up
            hasCleanerThreadRun = true;
            LOG.info("Finished cleaning price registry!");

            //Print results.
            LOG.info("Printing results...");
            printResults();

            //Clean up resources.
            clean();
        }

        /**
         * Cleans up (nulls out) no longer
         * used resources.
         */
        private void clean(){
            //Null out unused references.
            removedPrices.clear();
            removedPrices = null;
        }

        /**
         * Prints out the results of the clean. This includes
         * clean time, load time and entries removed.
         */
        private void printResults(){
            StringBuilder ret = new StringBuilder("\n");

            ret.append("------------- Registry Summary (Post Clean) -------------\n");
            ret.append("Total Entries (before clean): ").append(getMap().size()).append("\n");
            ret.append("Total Entries (after clean): ").append(getMap().size() - removedPrices.size()).append("\n");
            ret.append("Load Time: ").append(loadTime)
                    .append("ms (").append(((double)loadTime/1000)).append("s)").append("\n");
            ret.append("Clean time: ")
                    .append(cleanTime).append("ms (").append(((double)cleanTime/1000)).append("s)").append("\n");

            ret.append("Removed Entries: ").append(removedPrices.size()).append("\n");
            for(ItemPrice price : removedPrices){
                ret.append("\t").append(price.getItem()).append("\n");
            }

            ret.append("---------------------------------------------------------");

            LOG.info(ret);
        }

        /**
         * Performs the task of iterating over the registry
         * and checking that each entry has an existing
         * item/block in the forge registries and removes
         * it if not.
         */
        private void cleanRegistry(){
            int registryEntries = getMap().size();
            for(int i = 0; i <= registryEntries - 1; i++){
                ItemPrice entry;
                synchronized (PRICE_MAP_LOCK){
                    entry = (new ArrayList<>(priceMap.values())).get(i);
                }

                ResourceLocation entryID = entry.getItem();

                //Check
                boolean found =
                        ForgeRegistries.ITEMS.containsKey(entryID)
                        || ForgeRegistries.BLOCKS.containsKey(entryID);

                if(!found){
                    //Entry does not have a matching item or block
                    removedPrices.add(entry);
                    registryEntries--;

                    //Remove from map
                    synchronized (PRICE_MAP_LOCK){
                        priceMap.remove(entryID);
                    }
                }
            }
        }
    }
}
