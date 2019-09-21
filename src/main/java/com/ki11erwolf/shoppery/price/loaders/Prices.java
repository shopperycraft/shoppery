package com.ki11erwolf.shoppery.price.loaders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents a prices.json file.
 *
 * <p/>Allows getting the list of item
 * prices as a json object as well
 * as the load order as an enum.
 */
class Prices {

    /**
     * The load order as specified by the prices.json file.
     * Will be {@link LoadOrder#NONE} if no load order is
     * specified.
     */
    private final LoadOrder loadOrder;

    /**
     * The list of item prices from the
     * prices.json file.
     */
    private final JsonObject prices;

    /**
     * Creates a representation of the
     * prices.json file from the json
     * object in the file.
     *
     * @param json the prices.json
     *             file json object.
     */
    Prices(JsonObject json){
        loadOrder = getLoadOrder(json);
        prices = getPrices(json);
    }

    /**
     * @return The load order as specified by the prices.json file.
     * Will be {@link LoadOrder#NONE} if no load order is specified.
     */
    LoadOrder getLoadOrder(){
        return loadOrder;
    }

    /**
     * @return the prices json object that holds all
     * the items prices within the prices.json file.
     */
    JsonObject getPrices(){
        return prices;
    }

    // ********
    // Internal
    // ********

    /**
     * Gets the load order from the prices.json file.
     *
     * @param json the prices.json file json object.
     * @return the load order as an enum.
     */
    private LoadOrder getLoadOrder(JsonObject json){
        if(!hasMetadata(json))
            return LoadOrder.NONE;

        if(!json.getAsJsonObject("metadata").has("load order"))
            return LoadOrder.NONE;

        JsonElement loadOrder = json.getAsJsonObject("metadata").get("load order");

        if(!loadOrder.isJsonPrimitive())
            return LoadOrder.NONE;

        if(loadOrder.isJsonArray())
            return LoadOrder.NONE;

        if(loadOrder.getAsString().equals("first"))
            return LoadOrder.FIRST;

        if(loadOrder.getAsString().equals("last"))
            return LoadOrder.LAST;

        return LoadOrder.NONE;
    }

    /**
     * Gets the prices list from the prices.json file.
     *
     * @param json the prices.json file json object.
     * @return the prices list json object.
     */
    private JsonObject getPrices(JsonObject json){
        if(!json.has("prices") || !json.get("prices").isJsonObject())
            return null;

        return json.get("prices").getAsJsonObject();
    }

    /**
     * @return {@code true} if the prices.json file
     * has a metadata json object.
     */
    private boolean hasMetadata(JsonObject json){
        return json.has("metadata");
    }

    /**
     * The list of possible load orders.
     */
    public enum LoadOrder {
        FIRST,
        LAST,
        NONE
    }
}
