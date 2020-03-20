package com.ki11erwolf.shoppery.config.categories;

import com.ki11erwolf.shoppery.config.ConfigCategory;
import com.ki11erwolf.shoppery.config.IntegerConfigValue;

/**
 * Config category for general/miscellaneous
 * config settings.
 */
public class General extends ConfigCategory {

    /**
     * Config property to allow changing the delay time (in ms)
     * imposed between the sending of request packets.
     */
    private final IntegerConfigValue packetDelay = new IntegerConfigValue(
            "packet delay",
            "The time delay (in milliseconds) between sending packets. Lowering this number "
            + "will improve client responsiveness, but may impact server & network performance. "
            + "Use wisely on dedicated servers; offline singleplayer games should suffer less "
            + "from a shorter delay.",
            333, 100, 2000, this
    );

    /**
     * Config property to allow changing the amount of
     * money every player will start the game with.
     */
    private final IntegerConfigValue startingBalance = new IntegerConfigValue(
            "player starting balance",
            "The amount of money every player will start the game with.",
            100, 0, 10000, this
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
    public int getPacketDelay(){
        return packetDelay.getValue();
    }

    /**
     * @return the config defined amount of money each player
     * starts the game with.
     */
    public int getStartingBalance(){
        return startingBalance.getValue();
    }
}
