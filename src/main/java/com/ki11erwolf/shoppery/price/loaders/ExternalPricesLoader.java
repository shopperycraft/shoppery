package com.ki11erwolf.shoppery.price.loaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.ItemPrices;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Price registry loader that loads item prices
 * defined in external .json files (in {@code
 * /shoppery/prices/}) into the registry. Any
 * errors encountered during the load will be
 * displayed in the loaders log summary.
 */
public class ExternalPricesLoader extends Loader{

    /**
     * The logger for this mod.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

    /**
     * {@inheritDoc}
     *
     * <p/>Finds every {@code .json} file in the root directory
     * of {@code /prices/} and attempts to load item prices from
     * the file, as though it were a price file. If the file
     * cannot be parsed, the errors will be logged in the loaders
     * log summary and then simply skipped.
     *
     * @return every ItemPrice found within all found
     * .json price files.
     */
    @Override
    public ItemPrice[] load() {
        if(abort()) return new ItemPrice[0];

        List<ItemPrice> prices = new ArrayList<>();
        for(File priceFile : getExternalPriceFiles()){
            //Ignore directories
            if(priceFile.isDirectory())
                continue;

            LOG.info("Processing external price file:  "+ priceFile);

            List<ItemPrice> filePrices = loadPriceFile(priceFile);
            prices.addAll(filePrices);
        }

        return prices.toArray(new ItemPrice[0]);
    }

    /**
     * Attempts to get all ItemPrices defined in the
     * passed .json prices file. The contents of the
     * file will affect the loaders {@link #results}.
     *
     * @param priceFile the file believed to contain price
     *                  definitions.
     * @return all ItemPrices defined in the passed file.
     * Will be {@code null} if prices couldn't be parsed
     * from the file.
     */
    private List<ItemPrice> loadPriceFile(File priceFile){
        //Prices get & check
        Prices filePrices = getPricesFromFile(priceFile);
        if(filePrices == null) return new ArrayList<>();

        List<ItemPrice> foundPrices = new ArrayList<>();

        //Iterate over the mod item prices lists.
        JsonObject pricesLists = filePrices.getPrices();
        for(Map.Entry<String, JsonElement> modPricesEntry : pricesLists.entrySet()){
            //Ensure of type json object.
            if(!(modPricesEntry.getValue() instanceof JsonObject)){
                results.logError(String.format(
                        "Non json object: [name=%s, type=%s] in prices object file: %s. Skipping...",
                        modPricesEntry.getKey(), modPricesEntry.getValue().getClass(), priceFile
                ));
            }

            //Check if mod prices are for is loaded.
            String modname = modPricesEntry.getKey();
            if(ModList.get().isLoaded(modname))
                results.logAffectedMod(modname);
            else{ results.logUnaffectedMod(modname); continue;}

            JsonObject modPrices = (JsonObject) modPricesEntry.getValue();

            //Iterate over the prices of each mod prices list.
            for(Map.Entry<String, JsonElement> price : modPrices.entrySet()){
                ItemPrice itemPrice = ItemPrice.getFromJson(
                        new ResourceLocation(modname, price.getKey()),
                        price.getValue()
                );

                //Check that item price is valid
                if(itemPrice == null){
                    results.logInvalidEntry(modname + ":" + price.getKey());
                } else {
                    results.logRegisteredEntry(itemPrice);
                    foundPrices.add(itemPrice);
                }
            }
        }

        return foundPrices;
    }

    /**
     * Creates a {@link Prices} object containing the defined
     * prices in the passed json prices file.
     *
     * @param priceFile the json price file containing price
     *                  definitions.
     * @return the created prices object containing all defined
     * prices, or {@code null} if the file couldn't be parsed
     * into a Prices object.
     */
    private Prices getPricesFromFile(File priceFile) {
        try{
            return new Prices(new Gson().fromJson(new FileReader(priceFile), JsonObject.class));
        } catch (Exception e){
            results.logError(
                    "Failed to process external prices file: " + priceFile + ". Caused by: " + e.getMessage()
            );
            LOG.error("Failed to process external prices file: " + priceFile, e);
            return null;
        }
    }

    /**
     * Performs a check to see if the loader
     * is capable of performing its job.
     *
     * @return {@code true} if the loader should
     * abort the load.
     */
    private boolean abort(){
        return !(ItemPrices.PRICES_DIRECTORY.exists()
                && ItemPrices.PRICES_DIRECTORY.isDirectory()
                && ItemPrices.PRICES_DIRECTORY.listFiles() != null
        );
    }

    /**
     * Gets and returns all files ending in {@literal .json}
     * with in the root directory of {@code /prices/}.
     *
     * @return all files ending in {@literal .json} with
     * in the root directory of {@code /prices/}.
     */
    private File[] getExternalPriceFiles(){
        return ItemPrices.PRICES_DIRECTORY.listFiles(
                (dir, name) -> name.endsWith(".json")
        );
    }
}
