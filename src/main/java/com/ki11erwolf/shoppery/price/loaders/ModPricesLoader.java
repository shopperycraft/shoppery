package com.ki11erwolf.shoppery.price.loaders;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.price.ItemPrice;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Price registry loader responsible for loading in
 * prices for Items from other Mods, defined in the
 * Mod jars themselves.
 *
 * <p/>For each mod in the mod directory, the loader
 * will look for a "price.json" file in root directory
 * of the mod jar, or a "prices" value in the root
 * of the mods "mods.toml" file, pointing a valid
 * json file containing a list of item prices.
 *
 * <p/>Separate results for each mod will be
 * printed in the console along with this loaders
 * results.
 */
public class ModPricesLoader extends Loader{

    /**
     * The logger for this mod.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

    /**
     * The directory where all the mod jars are kept.
     */
    private static final File MODS_DIRECTORY = new File(System.getProperty("user.dir") + "/mods/");

    /**
     * List of found mods in the mods folder. Any .jar is
     * considered a valid mod and will be added to this list.
     */
    private final List<File> mods = new ArrayList<>();

    /**
     * The map of loaded prices from mod jars and results from loading them.
     * The prices object may be null if no prices could be found.
     */
    private final Map<Results, Prices> modPrices = new LinkedHashMap<>();

    /**
     * Creates a new mod prices loader.
     */
    public ModPricesLoader(){
        this.results = new ModLoaderResults();
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Scans the mod directory for .jar files
     * and looks for a prices.json file in each mod
     * jar.
     *
     * @return list of ItemPrices loaded from mod files.
     * May contain duplicates.
     */
    @Override
    public ItemPrice[] load() {
        getMods();
        getModsPriceFiles();
        return getModPrices();
    }

    // Methods

    /**
     * Handles getting the ItemPrices from
     * each found mod jar.
     *
     * @return the ItemPrices from each jar
     * file. May contain duplicates.
     */
    private ItemPrice[] getModPrices(){
        List<ItemPrice> prices = new ArrayList<>();

        for(int i = 0; i < Prices.LoadOrder.values().length; i++){
            Prices.LoadOrder order = null;
            switch (i){
                case 0:
                    order = Prices.LoadOrder.FIRST;
                    break;
                case 1:
                    order = Prices.LoadOrder.NONE;
                    break;
                case 2:
                    order = Prices.LoadOrder.LAST;
                    break;
            }

            if(order == null) break;

            for(Map.Entry<Results, Prices> entry : modPrices.entrySet()){
                Results results = entry.getKey();
                Prices modPrices = entry.getValue();

                if(modPrices == null) continue;
                if(modPrices.getLoadOrder() != order) continue;

                ItemPrice[] modItemPrices = getModPrices(modPrices, results);

                if(modItemPrices != null)
                    prices.addAll(Arrays.asList(modItemPrices));
            }

        }

        return prices.toArray(new ItemPrice[0]);
    }

    /**
     * Gets the list of ItemPrices from a mods prices file.
     *
     * @param modPrices the prices file object.
     * @param results the results object for the mod.
     * @return the list of ItemPrices from the prices file.
     */
    private ItemPrice[] getModPrices(Prices modPrices, Results results) {
        JsonObject prices = modPrices.getPrices();

        if(prices == null || !prices.isJsonObject()){
            flagAsErrored();
            results.logError("Could not get prices.json");
            return null;
        }

        List<ItemPrice> pricesList = new ArrayList<>();

        //For each mod
        prices.entrySet().forEach((price) -> {
            String modid = price.getKey();

            if(!price.getValue().isJsonObject()){
                flagAsErrored();
                results.logError(modid + " is not a json object.");
            }

            if(ModList.get().isLoaded(modid)){
                results.logAffectedMod(modid);
                this.results.logAffectedMod(modid);
                //For each value
                price.getValue().getAsJsonObject().entrySet().forEach((priceDef) -> {
                    ResourceLocation id;
                    try{
                        id = new ResourceLocation(modid, priceDef.getKey());
                    } catch (ResourceLocationException e){
                        results.logInvalidEntry(modid + ":" + priceDef.getKey());
                        this.results.logInvalidEntry(modid + ":" + priceDef.getKey());
                        id = null;
                    }

                    if(id != null){
                        JsonElement json = priceDef.getValue();
                        ItemPrice itemPrice = ItemPrice.getFromJson(id, json);

                        if(itemPrice != null) {
                            pricesList.add(itemPrice);
                            results.logRegisteredEntry(itemPrice);
                            this.results.logRegisteredEntry(itemPrice);
                            results.setNumberOfEntries(results.getNumberOfEntries() + 1);
                        } else {
                            String entry = id.toString() + " -> " + json.toString();
                            results.logInvalidEntry(entry);
                            this.results.logInvalidEntry(entry);
                        }
                    }
                });
            } else {
                results.logUnaffectedMod(modid);
                this.results.logUnaffectedMod(modid);
            }
        });

        return pricesList.toArray(new ItemPrice[0]);
    }

    /**
     * Handles getting the prices.json files
     * from each found mod.
     */
    private void getModsPriceFiles(){
        //For each mod file.
        for(File modFile : mods){
            String modName = modFile.getName();
            Results results = new Results();
            results.setName(modName);
            Prices prices = null;

            //Actual Scan
            try {
                LOG.debug("Scanning: " + modName + " for prices.json...");
                prices = getPricesFromMod(modFile, results);
                if(prices == null){
                    LOG.info("Couldn't find prices for mod: " + modName);
                    results.flagAsErrored();
                } else {
                    LOG.info("Found prices for mod: " + modName);
                }
            } catch (Exception e) {
                results.logError(e.getMessage());
                results.flagAsErrored();
            }

            modPrices.put(results, prices);
        }
    }

    /**
     * Handles getting the prices.json file from a mod jar.
     *
     * @param modFile the mod jar.
     * @param results the results object for the mod jar.
     * @return the prices file object from the mod jar
     * or {@code null} if no prices file could be found.
     * @throws Exception if any exception occurs during the
     * process.
     */
    private Prices getPricesFromMod(File modFile, Results results) throws Exception {
        String modName = modFile.getName();
        ZipFile modJar = new ZipFile(modFile);

        //Get prices in root.
        ZipEntry pricesJson = modJar.getEntry("prices.json");
        if(pricesJson == null){
            results.logError("Could not find prices.json in root!");
        }

        //Get prices from mods.toml
        if(pricesJson == null){
            ZipEntry modInfoFile = modJar.getEntry("META-INF/mods.toml");

            if(modInfoFile != null){
                String content = getFileFromZip(modJar, modInfoFile);

                CommentedConfig modInfo = new TomlParser().parse(content);
                String pricesFile = modInfo.get("prices");

                if(pricesFile != null){
                    ZipEntry pricesJsonFile = modJar.getEntry(pricesFile);

                    if(pricesJsonFile != null){
                        pricesJson = pricesJsonFile;
                    } else {
                        results.logError("Could not find defined prices file!");
                    }
                } else {
                    results.logError("Could not find prices entry in 'mods.toml' file");
                }
            } else {
                results.logError("Could not find 'mods.toml' file");
            }
        }

        return getPricesFromZipEntry(modJar, pricesJson);
    }

    /**
     * Handles getting a prices object from a prices.json
     * file within a zip file.
     *
     * @param file the zip file.
     * @param entry the prices file in the zip file.
     * @return the prices file from the zip or {@code null}
     * if the file couldn't be found or it's not a prices file.
     * @throws IOException if any exception occurs during the process.
     */
    private Prices getPricesFromZipEntry(ZipFile file, ZipEntry entry) throws IOException {
        if(entry != null){
            String content = getFileFromZip(file, entry);
            JsonObject json = new Gson().fromJson(content, JsonObject.class);
            return new Prices(json);
        } else {
            results.logError("Could not find a prices file for mod!");
        }

        return null;
    }

    /**
     * Adds each mod jar in the mods directory
     * to the {@link #mods} list, which will
     * then be scanned.
     */
    private void getMods(){
        if(!MODS_DIRECTORY.exists()){
            LOG.warn("Could not find mods directory!");
            return;
        }

        String[] files = MODS_DIRECTORY.list();

        if(files == null || files.length == 0){
            LOG.warn("No mods in mod directory!");
            return;
        }

        for(String modFileName : files){
            File file = new File(MODS_DIRECTORY + "/" + modFileName);
            if(modFileName.endsWith(".jar") && file.exists()){
                LOG.debug("Found mod: " + modFileName);
                mods.add(file);
            }
        }
    }

    // Static Util

    /**
     * Handles getting the content from a zip
     * file entry.
     *
     * @param file the zip file.
     * @param entry the file in the zip file.
     * @return the files contents.
     * @throws IOException if any exception occurs during the process.
     */
    private static String getFileFromZip(ZipFile file, ZipEntry entry) throws IOException {
        StringBuilder content = new StringBuilder();
        InputStream stream = file.getInputStream(entry);
        int byt;

        while ((byt = stream.read()) != -1)
            content.append((char) byt);

        return content.toString();
    }

    // Results Class

    /**
     * {@inheritDoc}
     *
     * <p/>A special results object that will also
     * print out the results for each mod when called
     * to print out its results.
     */
    private class ModLoaderResults extends Results{

        /**
         * {@inheritDoc}
         *
         * <p/>Prints out the results object for
         * each mod in the mods directory after
         * printing out the original results object.
         */
        @Override
        public void print(){
            super.print();

            for(Map.Entry<Results, Prices> entry : modPrices.entrySet()){
                entry.getKey().setNumberOfReplacements(-1);
                LOG.info(entry.getKey().getAsStandardResultString());
                if(!entry.getKey().hasErrored())
                    LOG.debug(entry.getKey().getAsDebugResultString());
            }
        }
    }
}
