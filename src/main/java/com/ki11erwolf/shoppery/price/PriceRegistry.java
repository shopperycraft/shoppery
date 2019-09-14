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

enum PriceRegistry {

    //Instance

    INSTANCE(
        new ShopperyPricesLoader(),
        new ModPricesLoader()
    );

    // Protected API

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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLLoadComplete);
    }

    boolean isLoaded(){
        return isLoaded;
    }

    Map<ResourceLocation, ItemPrice> getPriceMap(){
        return getMap();
    }

    // Registry

    private static final Logger LOG = ShopperyMod.getNewLogger();

    private static final Object PRICE_MAP_LOCK = new Object();

    //LinkedHashMap - we want a predictable iteration order.
    private final Map<ResourceLocation, ItemPrice> priceMap = new LinkedHashMap<>();

    private volatile boolean isLoaded = false;

    private boolean isLoading = false;

    private Loader[] loaders;

    private long loadTime;

    PriceRegistry(Loader... loaders){
        this.loaders = loaders;
    }

    public Map<ResourceLocation, ItemPrice> getMap(){
        synchronized (PRICE_MAP_LOCK){
            return priceMap;
        }
    }

    // Load logic

    private class LoaderThread extends Thread {

        private final List<Results> resultsList = new ArrayList<>();

        private final List<String> failedLoaders = new ArrayList<>();

        private final List<String> affectedMods = new ArrayList<>();

        LoaderThread(){
            super("shoppery-price-registry-loader");
            this.setDaemon(true);
        }

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

        private void runLoaders(){
            for(Loader loader : loaders){
                LOG.info("Loading: " + loader.getClass().getSimpleName());

                //Get values
                ItemPrice[] prices = loader.load();
                Results results = loader.getResults();
                results.setName(loader.getClass().getSimpleName());
                resultsList.add(results);

                //Only add values if it didn't fail
                if(loader.hasErrored()){
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

        private int addToMap(ItemPrice[] prices){
            int replacements = 0;

            for(ItemPrice price : prices){
                if(getMap().put(price.getItem(), price) != null)
                    replacements++;
            }

            return replacements;
        }

        private void appendToAffectedMods(String[] mods){
            if(mods != null)
                for(String mod : mods){
                    if(!affectedMods.contains(mod))
                        affectedMods.add(mod);
                }
        }

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

    private boolean hasCleanerThreadRun = false;

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
        hasCleanerThreadRun = true;
    }

    private class CleanerThread extends Thread{

        private static final int SLEEP_TIME = 10;//ms

        private List<ItemPrice> removedPrices = new ArrayList<>();

        private long cleanTime;

        CleanerThread(){
            super("shoppery-price-registry-cleaner");
            this.setDaemon(true);
        }

        @Override
        public void run(){
            LOG.info("Cleaning price registry...");
            cleanTime = System.currentTimeMillis();
            cleanRegistry();
            cleanTime = System.currentTimeMillis() - cleanTime;
            LOG.info("Finished cleaning price registry!");

            LOG.info("Printing results...");
            printResults();
            clean();
        }

        private void clean(){
            //Null out unused references.
            removedPrices.clear();
            removedPrices = null;
        }

        private void printResults(){
            StringBuilder ret = new StringBuilder("\n");

            ret.append("------------- Registry Summary (Post Clean) -------------\n");
            ret.append("Total Entries (after clean): ").append(getMap().size()).append("\n");
            ret.append("Clean time: ")
                    .append(cleanTime).append("ms (").append(((double)cleanTime/1000)).append("s)").append("\n");

            ret.append("Removed Entries: ").append(removedPrices.size()).append("\n");
            for(ItemPrice price : removedPrices){
                ret.append("\t").append(price.getItem()).append("\n");
            }

            ret.append("---------------------------------------------------------");

            LOG.info(ret);
        }

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

                //Sleep at the end of each entry check
                //to reduce overall impact on the registry.
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    LOG.warn("Failed to pause cleaner thread...");
                }
            }
        }
    }
}
