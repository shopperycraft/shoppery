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

public class ModPricesLoader extends Loader{

    private static final Logger LOG = ShopperyMod.getNewLogger();

    private static final File MODS_DIRECTORY = new File(System.getProperty("user.dir") + "/mods/");

    private final List<File> mods = new ArrayList<>();

    private final Map<Results, Prices> modPrices = new LinkedHashMap<>();

    public ModPricesLoader(){
        this.results = new ModLoaderResults();
    }

    @Override
    public ItemPrice[] load() {
        getMods();
        getModsPriceFiles();
        return getModPrices();
    }

    // Methods

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

    private static String getFileFromZip(ZipFile file, ZipEntry entry) throws IOException {
        StringBuilder content = new StringBuilder();
        InputStream stream = file.getInputStream(entry);
        int byt;

        while ((byt = stream.read()) != -1)
            content.append((char) byt);

        return content.toString();
    }

    // Results Class

    private class ModLoaderResults extends Results{

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
