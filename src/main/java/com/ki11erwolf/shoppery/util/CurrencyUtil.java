package com.ki11erwolf.shoppery.util;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.config.ShopperyConfig;
import com.ki11erwolf.shoppery.config.categories.General;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * A set of utilities that aid in working with
 * currencies.
 */
public class CurrencyUtil {

    /**
     * The config defined symbol used to denote
     * the currency.
     */
    public static final String CURRENCY_SYMBOL
            = ShopperyConfig.GENERAL_CONFIG.getCategory(General.class).getCurrencySymbol();

    /** Static util class */
    private CurrencyUtil(){}

    /**
     * Will create a String that correctly displays the
     * given {@code amount} as a price. Includes
     * separating commas.
     *
     * @return the given amount as a price.
     */
    public static String toFullString(double amount){
        String[] balance = (amount + "").replace(".", "-").split("-");

        ShopperyMod.getNewLogger().info("Balance: " + Arrays.toString(balance));

        if(balance.length == 0){
            return toFullString(Long.parseLong(String.valueOf(amount)), (byte) 0);
        } else if(balance.length == 1){
            balance = new String[] {String.valueOf(balance[0]), "00"};
        } else {
            char[] bal = balance[1].toCharArray();

            if(balance[1].length() == 1) balance[1] = balance[1] + "0";
            else balance[1] = String.valueOf(bal[0] + bal[1]);
        }

        return toFullString(
                Long.parseLong(balance[0]),
                Byte.parseByte(balance[1])
        );
    }

    /**
     * Will create a String that correctly displays the
     * given {@code balance} and {@code cents} as a price.
     * Includes separating commas.
     *
     * @return the given amount as a price.
     */
    public static String toFullString(long balance, byte cents){
        if(cents < 10) return NumberFormat.getInstance().format(balance) + ".0" + cents;
        else return NumberFormat.getInstance().format(balance) + "." + cents;
    }

    /**
     * Will create a String that correctly displays the
     * given {@code amount} as a shortened price - that is
     * - price only containing the most significant
     * value(s) followed by a letter to denote the amount
     * of trailing zeros (0). Includes separating commas.
     *
     * @return the given amount as a price.
     */
    public static String toShortString(double amount){
        String[] balance = (amount + "").split(".");

        ShopperyMod.getNewLogger().info("Balance: " + Arrays.toString(balance));

        if(balance.length == 0){
            return toShortString(Long.parseLong(String.valueOf(amount)), (byte) 0);
        } else if(balance.length == 1){
            balance = new String[] {String.valueOf(balance[0]), "00"};
        } else {
            char[] bal = balance[1].toCharArray();
            balance[1] = String.valueOf(bal[0] + bal[1]);

            if(balance[1].length() == 1){
                balance[1] = balance[1] + "0";
            }
        }

        return toShortString(
                Long.parseLong(balance[0]),
                Byte.parseByte(balance[1])
        );
    }

    /**
     * Will create a String that correctly displays the
     * given {@code balance} and {@code cents} as a
     * shortened price - that is - price only containing
     * the most significant value(s) followed by a letter
     * to denote the amount of trailing zeros (0). Includes
     * separating commas.
     *
     * @return the given amount as a price.
     */
    public static String toShortString(long balance, byte cents){
        if(balance == 0)
            if(cents < 10){
                return "0.0" + cents + 'c';
            } else {
                return "0." + cents + 'c';
            }

        return format(balance);
    }

    //***************************************************************************************************************
    // Currency Formatting Code
    // Copied from:
    // https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java/30661479#30661479
    // THE BELOW CODE HAS BEEN EDITED
    //***************************************************************************************************************

    /**
     * Map of numbers to their shorthand version.
     */
    private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>();

    /*
     * Initializes suffixes the map.
     */
    static {
        SUFFIXES.put(1_000L, "K");
        SUFFIXES.put(1_000_000L, "M");
        SUFFIXES.put(1_000_000_000L, "B");
        SUFFIXES.put(1_000_000_000_000L, "T");
        SUFFIXES.put(1_000_000_000_000_000L, "Q");
    }

    /**
     * Shortens the given number to the most
     * significant value(s) followed by a letter
     * to denote the amount of trailing zeros (0).
     *
     * Examples:
     * 1000 to 1k
     * 5821 to 5.8k
     * 10500 to 10k
     * 101800 to 101k
     * 2000000 to 2m
     * 7800000 to 7.8m
     * 92150000 to 92m
     * 123200000 to 123m
     *
     * @param value the value to shorten.
     * @return the shorten value.
     */
    private static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = SUFFIXES.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        //noinspection IntegerDivisionInFloatingPointContext
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
