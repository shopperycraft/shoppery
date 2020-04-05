package com.ki11erwolf.shoppery.price;

import com.google.gson.*;
import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.item.CurrencyItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * A subsystem of the {@link PriceRegistry} that handles runtime
 * modification and persistence, specifically:
 * <ol>
 *     <li>modifying the registry <b>after</b> it has been loaded
 *     - that is - adding or editing ItemPrices within it.</li>
 *     <li>and ensuring the changes are saved to file so that
 *     they may persist across Minecraft runs and registry loads.</li>
 * </ol>
 *
 * <p/>This mechanism relies on the {@link
 * com.ki11erwolf.shoppery.price.loaders.ExternalPricesLoader} to load
 * persisted prices back into the registry.
 */
class RegistryModifier {

    /**
     * The json file where all changes will be saved.
     */
    private static final File PERSISTENT_FILE
            = new File(ItemPrices.PRICES_DIRECTORY + "/my-prices.json");

    /**
     * The underlying map that makes up the price registry.
     */
    private final Map<ResourceLocation, ItemPrice> priceMap;

    /**
     * @param priceMap The underlying map that makes up the
     *                 price registry.
     */
    RegistryModifier(Map<ResourceLocation, ItemPrice> priceMap){
        this.priceMap = priceMap;
    }

    /**
     * Allows setting the price of an item, defined in the
     * ItemPrice, to the ItemPrice, either for the first
     * time or to change it.
     *
     * Setting the price modifies the registry to reflect
     * the change, as well as writes the price change to
     * file, allowing the change to persist across Minecraft
     * Launches & Registry loads.
     *
     * @param price the ItemPrice, holding a reference to
     *              the Item who's price is being changed,
     *              as well the new specified price of the item.
     * @return {@code true} if the item is a valid item, is
     * allowed to have a price, and the price change was
     * performed successfully.
     */
    boolean setPrice(ItemPrice price){
        if(!isValidItem(price.getItem()))
            return false;

        synchronized (PriceRegistry.PRICE_MAP_LOCK){
            priceMap.put(price.getItem(), price);
        }

        return persist(price);
    }

    /**
     * Sets an items price, or new price, that persists across
     * Minecraft Launches & Registry loads.
     *
     * Appends the given ItemPrice to the {@link #PERSISTENT_FILE}
     * json prices file, with all previously appended prices intact,
     * which can then be loaded back into the registry through
     * the {@link com.ki11erwolf.shoppery.price.loaders.ExternalPricesLoader}.
     * This effectively persists the data.
     *
     * <p/>This is achieved by copying the already persisted
     * {@link #PERSISTENT_FILE} file data, modifying it, and
     * then writing the modified data back to file.
     *
     * @param price the item price change to persistently save.
     * @return {@code true} if the new item price was written to
     * the {@link #PERSISTENT_FILE}, persisting the data.
     */
    private static boolean persist(ItemPrice price){
        JsonObject persistentFilePrices = getPersistentFileJson().getAsJsonObject("prices");
        String namespace = price.getItem().getNamespace();

        if(!persistentFilePrices.has(namespace)){
            persistentFilePrices.add(namespace, new JsonObject());
        }

        JsonObject modPricesList = persistentFilePrices.getAsJsonObject(namespace);
        String itemName = price.getItem().getPath();

        modPricesList.add(itemName, toJsonRepresentation(price));

        JsonObject root = new JsonObject();
        root.add("prices", persistentFilePrices);
        return writeToPersistentFile(root);
    }

    /**
     * Writes a given JsonObject to the {@link #PERSISTENT_FILE}
     * as a {@link GsonBuilder#setPrettyPrinting() pretty printed}
     * json string.
     *
     * @param json the json object to write to the {@link #PERSISTENT_FILE}.
     * @return {@code true} if the passed JsonObject was written to
     * the file, {@code false} otherwise.
     */
    private static boolean writeToPersistentFile(JsonObject json){
        try {
            String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(json);
            ensureExistence();

            FileWriter writer = new FileWriter(PERSISTENT_FILE);
            writer.write(jsonString);
            writer.flush();
            writer.close();

            return true;
        } catch (IOException e) {
            ShopperyMod.getNewLogger().error(
                    "Failed to write json to PERSISTENT_FILE: " + PERSISTENT_FILE, e
            );
            return false;
        }
    }

    /**
     * Will get the json data already stored in the
     * {@link #PERSISTENT_FILE}. In the event the file
     * does not exist or cannot be parsed, a new, blank
     * prices template will be returned instead.
     *
     * @return the json prices data contained within the
     * {@link #PERSISTENT_FILE}, or a blank slate that
     * can be written to if no persisted data can be
     * retrieved.
     */
    private static JsonObject getPersistentFileJson(){
        if(!PERSISTENT_FILE.exists()) {
            JsonObject newFileJson = new JsonObject();
            newFileJson.add("prices", new JsonObject());
            return newFileJson;
        }

        JsonObject persistentFileJson;

        try {
            persistentFileJson = new Gson().fromJson(
                    new FileReader(PERSISTENT_FILE), JsonObject.class
            );

            if(persistentFileJson == null){
                persistentFileJson = new JsonObject();
                persistentFileJson.add("prices", new JsonObject());
            }

            JsonElement pricesElement = persistentFileJson.get("prices");

            if(pricesElement == null || !pricesElement.isJsonObject())
                persistentFileJson.add("prices", new JsonObject());

            return persistentFileJson;
        } catch (Exception e) {
            ShopperyMod.getNewLogger().error(
                    "Failed to parse PERSISTENT_FILE json: " + PERSISTENT_FILE, e
            );
            JsonObject newFileJson = new JsonObject();
            newFileJson.add("prices", new JsonObject());
            return newFileJson;
        }
    }

    /**
     * Creates a JsonObject representation of the passed ItemPrice,
     * that can be parsed and interpreted by a
     * {@link com.ki11erwolf.shoppery.price.loaders.Loader}.
     *
     * @param itemPrice the item price to make a JsonObject representation
     *                  of.
     * @return a new JsonObject representation of the passed ItemPrice.
     */
    private static JsonObject toJsonRepresentation(ItemPrice itemPrice){
        JsonObject jsonPrice = new JsonObject();

        if(itemPrice.canBuy())
            jsonPrice.add("buy", new JsonPrimitive(itemPrice.getBuyPrice()));
        else
            jsonPrice.add("buy", new JsonPrimitive(false));

        if(itemPrice.canSell())
            jsonPrice.add("sell", new JsonPrimitive(itemPrice.getSellPrice()));
        else
            jsonPrice.add("sell", new JsonPrimitive(false));

        jsonPrice.add("fluctuation", new JsonPrimitive(itemPrice.getPriceFluctuation()));

        return jsonPrice;
    }

    /**
     * Ensures that the item mapped to the given ResourceLocation
     * exists, and is not a forbidden item: air/currency.
     *
     * @param itemID a resource location naming an item.
     * @return {@code true} if the item referred to by the
     * ResourceLocation ItemID can be used, that is, it
     * exists and is not a forbidden item. {@code false}
     * otherwise.
     */
    private static boolean isValidItem(ResourceLocation itemID){
        if(itemID == null)
            return false;

        Item item = ForgeRegistries.ITEMS.getValue(itemID);

        if(item == null || item == Items.AIR)
            return false;

        return !(item instanceof CurrencyItem);
    }

    /**
     * Will ensure, to the largest possible extent,
     * that both the {@link #PERSISTENT_FILE} and its
     * root directory exist on the file system.
     */
    private static void ensureExistence(){
        if(!ItemPrices.PRICES_DIRECTORY.exists()){
            //noinspection ResultOfMethodCallIgnored
            ItemPrices.PRICES_DIRECTORY.mkdirs();
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            PERSISTENT_FILE.createNewFile();
        } catch (IOException e) {
            ShopperyMod.getNewLogger().error(
                    "Failed to create PERSISTENT_FILE: "
                            + PERSISTENT_FILE + " before write", e
            );
        }
    }
}
