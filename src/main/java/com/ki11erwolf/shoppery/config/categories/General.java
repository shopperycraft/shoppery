package com.ki11erwolf.shoppery.config.categories;

import com.ki11erwolf.shoppery.config.ConfigCategory;
import com.ki11erwolf.shoppery.config.IntegerConfigValue;
import com.ki11erwolf.shoppery.config.StringConfigValue;

/**
 * Config category for general/miscellaneous
 * config settings.
 */
public class General extends ConfigCategory {

    /**
     * Config property to allow changing the delay time (in ms)
     * imposed between the sending of request packets.
     */
    private final IntegerConfigValue packetWaitTime = new IntegerConfigValue(
            "packet wait time",
            "The time delay (in milliseconds) between sending packets. Lowering this number "
            + "will improve Shoppery gui's responsiveness, but may impact server & network performance. "
            + "Use wisely on dedicated servers; offline singleplayer games should suffer less "
            + "from a shorter delay.",
            250, 50, 1500, this
    );

    /**
     * Config property to allow changing the amount of
     * money every player will start the game with.
     */
    private final IntegerConfigValue startingBalance = new IntegerConfigValue(
            "player starting balance",
            "The amount of money every player will start the game with.",
            100, 0, 5000, this
    );

    /**
     * Config properties to allow changing the symbol Shoppery
     * uses to represent its currency.
     */
    private final StringConfigValue currencySymbol = new StringConfigValue(
            "currency symbol",
            "The currency symbol (e.g. $) Shoppery uses to represent its currency. " +
                      "The symbol can be series of letters.",
            "$", this
    );

    /**
     * Config category for general/miscellaneous
     * config settings.
     */
    public General() {
        super("general");
    }

    /**
     * @return the config defined time delay (in
     * milliseconds) imposed before a clint
     * side request packet can be sent again.
     */
    public int getPacketWaitTime(){
        return packetWaitTime.getValue();
    }

    /**
     * @return the config defined amount of money each player
     * starts the game with.
     */
    public int getStartingBalance(){
        return startingBalance.getValue();
    }

    /**
     * @return the config defined symbol Shoppery
     * uses to represent its currency.
     */
    public String getCurrencySymbol() {
        return currencySymbol.getValue();
    }
}
