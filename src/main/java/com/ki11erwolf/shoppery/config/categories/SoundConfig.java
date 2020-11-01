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
     * The config property defining the config flag allowing enabling
     * or disabling the playing of the shop transaction sound effect.
     */
    private final BooleanConfigValue playShopTransactionSound = new BooleanConfigValue(
            "enable-shop-trade-sound-effect",
            "The flag that enables ('true') or disables ('false') the playing of the shop" +
                    "transaction sound effect, when either a purchase or sale is made. This config" +
                    "setting is set server-side, by the hosting server, for all players. ",
            true, this
    );

    /**
     * The config property defining the config flag allowing enabling or
     * disabling the playing of the shop transaction declined sound effect.
     */
    private final BooleanConfigValue playShopTransactionDeclinedSound = new BooleanConfigValue(
            "enable-shop-decline-sound-effect",
            "The flag that enables ('true') or disables ('false') the playing of the shop" +
                    "declined transaction sound effect, when either a purchase or sale fails. This" +
                    "config setting is set server-side, by the hosting server, for all players. ",
            true, this
    );

    /**
     * The config property defining the config flag allowing enabling or
     * disabling the playing of the shop activated sound effect.
     */
    private final BooleanConfigValue playShopActivatedSound = new BooleanConfigValue(
            "enable-shop-activation-sound-effect",
            "The flag that enables ('true') or disables ('false') the playing of the shop" +
                    "activated sound effect, when a shop is first used and configured. This" +
                    "config setting is set server-side, by the hosting server, for all players. ",
            true, this
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

    /**
     *
     * @return {@code true} if the config value enabling
     * the playing of the shop transaction sound event
     * has been enabled within the config file.
     */
    public boolean playTransactionSoundEffect() {
        return playShopTransactionSound.getValue();
    }

    /**
     *
     * @return {@code true} if the config value enabling
     * the playing of the shop transaction declined sound
     * event has been enabled within the config file.
     */
    public boolean playTransactionDeclinedSoundEffect() {
        return playShopTransactionDeclinedSound.getValue();
    }

    /**
     *
     * @return {@code true} if the config value enabling
     * the playing of the shop activation sound event
     * has been enabled within the config file.
     */
    public boolean playActivatedSoundEffect() {
        return playShopActivatedSound.getValue();
    }
}
