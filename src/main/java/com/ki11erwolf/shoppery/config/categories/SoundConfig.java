package com.ki11erwolf.shoppery.config.categories;

import com.ki11erwolf.shoppery.config.BooleanConfigValue;
import com.ki11erwolf.shoppery.config.ConfigCategory;

/**
 * The configuration category that holds settings
 * related to sound & sound events.
 */
public class SoundConfig extends ConfigCategory {

    /**
     * The config property defining the config flag allowing changing
     * the transaction sound event between default & alternative.
     */
    private final BooleanConfigValue useAlternativeTransactionSound = new BooleanConfigValue(
            "use-alternative-transaction-sound",
            "Enable this value to have the alternative transaction sound event " +
                     "play when a player trades with a shop instead of the default.",
            false, this
    );

    /**
     * Constructs the Sound config category.
     */
    public SoundConfig() {
        super("Sound");
    }

    /**
     * @return {@code true} if the config value enabling
     * use of the alternative transaction sound event
     * has been enabled within the config file.
     */
    public boolean useAltTransactionSound() {
        return useAlternativeTransactionSound.getValue();
    }
}
