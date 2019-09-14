package com.ki11erwolf.shoppery.price.loaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.price.ItemPrice;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;

public class ShopperyPricesLoader extends Loader {

    private static final Logger LOG = ShopperyMod.getNewLogger();

    private static final String PRICES_FILE = "/shoppery-prices.json";

    @Override
    public ItemPrice[] load() {
        JsonObject prices = getPricesJson();

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
                //For each value
                price.getValue().getAsJsonObject().entrySet().forEach((priceDef) -> {
                    ResourceLocation id = new ResourceLocation(modid, priceDef.getKey());
                    JsonElement json = priceDef.getValue();

                    ItemPrice itemPrice = ItemPrice.getFromJson(id, json);

                    if(itemPrice != null) {
                        pricesList.add(itemPrice);
                        results.logRegistered(itemPrice);
                    } else results.logInvalidEntry(id.toString() + " -> " + json.toString());
                });
            } else {
                results.logUnaffectedMod(modid);
            }
        });

        return pricesList.toArray(new ItemPrice[0]);
    }

    private JsonObject getPricesJson(){
        try{
            BufferedInputStream reader = new BufferedInputStream(this.getClass().getResourceAsStream(PRICES_FILE));
            StringBuilder prices = new StringBuilder();
            int chr;

            while((chr = reader.read()) != -1){
                prices.append((char) chr);
            }

            Prices pricesObj = new Prices(new Gson().fromJson(prices.toString(), JsonObject.class));
            return pricesObj.getPrices();
        } catch (Exception ex){
            LOG.error("Failed to load prices.json for Shoppery...", ex);
            this.flagAsErrored();
            this.results.logError(ex.getMessage());
        }

        return null;
    }
}
