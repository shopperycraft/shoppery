package com.ki11erwolf.shoppery.price;

import com.google.gson.JsonElement;
import com.ki11erwolf.shoppery.util.MathUtil;
import net.minecraft.util.ResourceLocation;

/**
 * Represents the price of an Item or Block in Minecraft.
 *
 * <p/>Each ItemPrice is linked to a single Item or Block
 * in the forge registries and gives a Buy and Sell price
 * for the item. The buy price is the price the player
 * must pay for the item, the sell price is the amount
 * of money the player can get from selling the item.
 *
 * <p/>Every ItemPrice has fluctuation - the percentage
 * the prices can vary (fluctuate) by.
 *
 * <p/>An ItemPrice can be prohibited, either
 * prohibiting buy, selling, or both. When prohibited,
 * the item cannot be bought, sold, or both.
 */
public class ItemPrice {

    /**
     * The registry name of the Item or Block
     * this ItemPrice is for.
     */
    private final ResourceLocation item;

    /**
     * The percentage the prices (buy/sell) can vary by.
     * Range: 0.0 to 100.0.
     */
    private final double fluctuation;

    /**
     * The average price a player can buy the item
     * for. Can be 0 which indicates the item shouldn't
     * be sold.
     */
    private final double buy;

    /**
     * The average price a player can sell the item
     * for. Can be 0 which indicates the item shouldn't
     * be buyable.
     */
    private final double sell;

    /**
     * Allows prohibiting shops from selling the item.
     */
    private final boolean prohibitBuy;

    /**
     * Allows prohibiting shops from buying the item
     * from the player.
     */
    private final boolean prohibitSell;

    //Constructors

    /**
     * Creates a new ItemPrice from the given values.
     */
    public ItemPrice(ResourceLocation item, boolean prohibitBuy, double buy, boolean prohibitSell,
                       double sell, double fluctuation){
        //Ensure sell price never exceeds buy price.
        double maxFluctuation = (buy * (fluctuation / 100));
        if(sell >= buy - maxFluctuation) sell = buy - (maxFluctuation * 2);

        this.item = item;
        this.buy = (buy < 0) ? 0 : buy;
        this.sell = (sell < 0) ? 0 : sell;
        this.fluctuation = (fluctuation < 0) ? 0 : fluctuation;
        this.prohibitBuy = prohibitBuy;
        this.prohibitSell = prohibitSell;
    }

    /**
     * Creates a new ItemPrice from the given values, with a fluctuation of 10%.
     */
    public ItemPrice(ResourceLocation item, boolean prohibitBuy, double buy, boolean prohibitSell, double sell){
        this(item, prohibitBuy, buy, prohibitSell, sell, 10.0);
    }

    /**
     * Creates a new ItemPrice from the given values.
     */
    public ItemPrice(ResourceLocation item, double buy, double sell, double fluctuation){
        this(item, false, buy, false, sell, fluctuation);
    }

    /**
     * Creates a new ItemPrice from the given values, with a fluctuation of 10%.
     */
    public ItemPrice(ResourceLocation item, double buy, double sell){
        this(item, buy, sell, 10.0);
    }

    // Getters

    /**
     * @return the registry name of the Item or Block that this
     * ItemPrice is for.
     */
    public ResourceLocation getItem(){
        return item;
    }

    /**
     * @return the percentage (0.0 to 100.0) that the buy and sell
     * prices can vary/fluctuate by.
     */
    public double getPriceFluctuation(){
        return MathUtil.roundToTwoDecimals(fluctuation);
    }

    /**
     * @return the amount of money the Item or Block
     * can be bought for by a player.
     */
    public double getBuyPrice(){
        return MathUtil.roundToTwoDecimals(buy);
    }

    /**
     * @return {@code true} if the item can be bought from
     * singleplayer shops and buying is not prohibited.
     */
    public boolean canBuy(){
        return allowsBuying() && buy > 0;
    }

    /**
     * @return {@code false} if the buying of the item
     * has been completely prohibited. If prohibited,
     * the item cannot be bought from shops at all.
     */
    public boolean allowsBuying(){
        return !prohibitBuy;
    }

    /**
     * @return the amount of money a player gets
     * from selling the Item or Block to a shop.
     */
    public double getSellPrice(){
        return MathUtil.roundToTwoDecimals(sell);
    }

    /**
     * @return {@code true} if the item can be sold to
     * singleplayer shops and selling is not prohibited.
     */
    public boolean canSell(){
        return sell > 0;
    }

    /**
     * @return {@code false} if the selling of the item
     * has been completely prohibited. If prohibited,
     * the item cannot be sold to shops at all.
     */
    public boolean allowsSelling(){
        return !prohibitSell;
    }

    /**
     * @return a random buying price for the item, that within
     * +/- {@link #fluctuation}% of the original buying price.
     * Returns a new value every call.
     *
     * @see #applyFluctuation(double)
     */
    public double getFluctuatingBuyPrice(){
        return applyFluctuation(buy);
    }

    /**
     * @return a random selling price for the item, that within
     * +/- {@link #fluctuation}% of the original selling price.
     * Returns a new value every call.
     *
     * @see #applyFluctuation(double)
     */
    public double getFluctuatingSellPrice(){
        return applyFluctuation(sell);
    }

    /**
     * Creates a new ItemPrice object based upon this ItemPrice,
     * for the same Item/Block as this ItemPrice, where the
     * buying and selling prices have been changed (+/-) by a
     * percentage of the original prices (using {@link #fluctuation}
     * as the percentage).
     *
     * @see #applyFluctuation(double)
     * @return the new ItemPrice object for the same Item/Block
     * as this ItemPrice object, containing fluctuated buying and
     * selling prices.
     */
    public ItemPrice withPriceFluctuation(){
        double exactBuy = -1;
        double exactSell = -1;

        if(this.canBuy()) exactBuy = getFluctuatingBuyPrice();
        if(this.canSell())  exactSell = getFluctuatingSellPrice();

        if(exactSell >= exactBuy){
            exactSell = exactBuy / 2;
        }

        return new ItemPrice(this.getItem(), exactBuy, exactSell, 0);
    }

    /**
     * Used to change a value, usually a price, by a
     * random percentage between {@code 0} and
     * {@link #fluctuation}, in either the positive
     * or negative direction.
     *
     * @param value the input value to calculate
     * @return a random value calculated from the
     * input value and the ItemPrice's fluctuation.
     */
    protected double applyFluctuation(double value){
        if(value <= 0) return 0;

        double change = (value * (MathUtil.getRandomDoubleInRange(0, fluctuation)/100));
        if(MathUtil.getRandomBoolean()) value += change; else value -= change;

        return MathUtil.roundToTwoDecimals((value > 0) ? value : 0.01);
    }

    /**
     * @return the information that makes up the ItemPrice
     * as a String.
     */
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

    /**
     * Constructs a new ItemPrice from the given registry name and
     * json element.
     *
     * @param registryName the registry name of the Item or Block the ItemPrice is for.
     * @param json the json element that defines the ItemPrice.
     * @return the constructed ItemPrice or {@code null} if the ItemPrice
     * could not be constructed for any reason.
     */
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

    /**
     * Constructs a new ItemPrice from the given registry name and
     * json object.
     *
     * @param registryName the registry name of the Item or Block the ItemPrice is for.
     * @param json the json object that defines the ItemPrice.
     * @return the constructed ItemPrice or {@code null} if the ItemPrice
     * could not be constructed for any reason.
     */
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

    /**
     * @return the double value from a json object with the
     * given member name. {@code -1} if the value
     * could not be found. {@code -2} if the value is null.
     */
    private static double getDoubleFromJsonObject(JsonElement json, String member){
        if(json.isJsonObject()){
            double ret;
            if(json.getAsJsonObject().has(member)){
                if(json.getAsJsonObject().get(member).isJsonNull())
                    ret = -2;
                else {
                    try {
                        ret = json.getAsJsonObject().get(member).getAsDouble();

                        if(ret < 0)
                            ret = 0;
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
