package com.ki11erwolf.shoppery.price.loaders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class Prices {

    private final LoadOrder loadOrder;

    private final boolean cache;

    private final JsonObject prices;

    Prices(JsonObject json){
        loadOrder = getLoadOrder(json);
        cache = shouldCache(json);
        prices = getPrices(json);
    }

    public LoadOrder getLoadOrder(){
        return loadOrder;
    }

    public boolean cache(){
        return cache;
    }

    JsonObject getPrices(){
        return prices;
    }

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

    private boolean shouldCache(JsonObject json){
        return hasMetadata(json) && json.getAsJsonObject("metadata").has("cache")
                && json.getAsJsonObject("metadata").get("cache").getAsBoolean();
    }

    private JsonObject getPrices(JsonObject json){
        if(!json.has("prices") || !json.get("prices").isJsonObject())
            return null;

        return json.get("prices").getAsJsonObject();
    }

    private boolean hasMetadata(JsonObject json){
        return json.has("metadata");
    }

    public enum LoadOrder {
        FIRST,
        LAST,
        NONE
    }
}
