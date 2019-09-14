package com.ki11erwolf.shoppery.price;

import com.google.gson.JsonElement;
import com.ki11erwolf.shoppery.util.MathUtil;
import net.minecraft.util.ResourceLocation;

public class ItemPrice {

    private final ResourceLocation item;

    private final double fluctuation;

    private final double buy;

    private final double sell;

    private final boolean prohibitBuy;

    private final boolean prohibitSell;

    //Constructors

    private ItemPrice(ResourceLocation item, boolean prohibitBuy, double buy, boolean prohibitSell,
                       double sell, double fluctuation){
        this.item = item;
        this.buy = (buy < 0) ? 0 : buy;
        this.sell = (sell < 0) ? 0 : sell;
        this.fluctuation = (fluctuation < 0) ? 0 : fluctuation;
        this.prohibitBuy = prohibitBuy;
        this.prohibitSell = prohibitSell;
    }

    private ItemPrice(ResourceLocation item, boolean prohibitBuy, double buy, boolean prohibitSell, double sell){
        this(item, prohibitBuy, buy, prohibitSell, sell, 10.0);
    }

    private ItemPrice(ResourceLocation item, double buy, double sell, double fluctuation){
        this(item, false, buy, false, sell, fluctuation);
    }

    private ItemPrice(ResourceLocation item, double buy, double sell){
        this(item, buy, sell, 10.0);
    }

    // Getters

    public ResourceLocation getItem(){
        return item;
    }

    public double getPriceFluctuation(){
        return MathUtil.roundToTwoDecimals(fluctuation);
    }

    public double getBuyPrice(){
        return MathUtil.roundToTwoDecimals(buy);
    }

    public boolean canBuy(){
        return buy > 0;
    }

    public boolean allowsBuying(){
        return !prohibitBuy;
    }

    public double getSellPrice(){
        return MathUtil.roundToTwoDecimals(sell);
    }

    public boolean canSell(){
        return sell > 0;
    }

    public boolean allowsSelling(){
        return !prohibitSell;
    }

    public double getBuyPriceWithFluctuation(){
        double buy = getBuyPrice();

        if(buy <= 0)
            return 0;

        double change = (buy * (MathUtil.getRandomDoubleInRage(0, fluctuation)/100));
        if(MathUtil.getRandomBoolean()) buy += change; else buy -= change;

        return MathUtil.roundToTwoDecimals((buy > 0) ? buy : 0.01);
    }

    public double getSellPriceWithFluctuation(){
        double sell = getSellPrice();

        if(sell <= 0)
            return 0;

        double change = (sell * (MathUtil.getRandomDoubleInRage(0, fluctuation)/100));
        if(MathUtil.getRandomBoolean()) sell += change; else sell -= change;

        return MathUtil.roundToTwoDecimals((sell > 0) ? sell : 0.01);
    }

    @Override
    public String toString(){
        return String.format(
                "[registryName=%s, buyProhibited=%s, buy=%s, sellProhibited=%s, sell=%s, fluctuation=%s]",
                item.toString(), prohibitBuy, buy, prohibitSell, sell, fluctuation
        );
    }

    // ***************
    // Json Conversion
    // ***************

    public static ItemPrice getFromJson(ResourceLocation registryName, JsonElement json){
        //if null
        if(json.isJsonNull())
            return new ItemPrice(registryName, true, 0, true, 0, 10);

        //if array
        if(json.isJsonArray())
            return null;

        //if number
        if(json.isJsonPrimitive()){
            try{
                double bsPrice = json.getAsDouble();
                return new ItemPrice(registryName, bsPrice, (bsPrice / 2));
            } catch (Exception e){
                return null;
            }
        }

        //Must be in object form or null.
        return getFromObject(registryName, json);
    }

    private static ItemPrice getFromObject(ResourceLocation registryName, JsonElement json){
        if(json.isJsonObject()){
            //Get values
            double fluctuation = getDoubleFromJsonObject(json, "fluctuation");
            double buy = getDoubleFromJsonObject(json, "buy");
            double sell = getDoubleFromJsonObject(json, "sell");

            //Ensure buy and sell are correct
            if(buy == -1 && sell != -1){
                buy = sell*2;
            }
            if(sell == -1 && buy != -1){
                sell = buy/2;
            }

            //If has nulls
            if(buy == -2 || sell == -2){
                return new ItemPrice(registryName, buy == -2, buy, sell == -2, sell);
            }

            //Else
            if(fluctuation < 0){
                return new ItemPrice(registryName, buy, sell);
            } else {
                return new ItemPrice(registryName, buy, sell, fluctuation);
            }
        } else return null;
    }

    private static double getDoubleFromJsonObject(JsonElement json, String member){
        if(json.isJsonObject()){
            double ret;
            if(json.getAsJsonObject().has(member)){
                if(json.getAsJsonObject().get(member).isJsonNull())
                    ret = -2;
                else {
                    try {
                        ret = json.getAsJsonObject().get(member).getAsDouble();
                    } catch (Exception e) {
                        ret = -1;
                    }
                }
            } else ret = -1;

            return ret;
        }

        return -1;
    }
}
