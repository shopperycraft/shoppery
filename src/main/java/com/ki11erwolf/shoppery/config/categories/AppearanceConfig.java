package com.ki11erwolf.shoppery.config.categories;

import com.ki11erwolf.shoppery.config.BooleanConfigValue;
import com.ki11erwolf.shoppery.config.ConfigCategory;
import com.ki11erwolf.shoppery.config.StringConfigValue;

import java.awt.*;

public class AppearanceConfig extends ConfigCategory {

    /**
     * Boolean Configuration object that adds the config value
     * allowing enabling/disabling wallet gui tooltips to the
     * config file.
     */
    private final BooleanConfigValue enableWalletTooltips = new BooleanConfigValue(
            "enable-wallet-tooltips",
            "Allows turning on/off tooltips within the wallet gui.",
            true, this
    );

    /**
     * String Configuration object that adds the config value
     * allowing changing of the wallet gui's balance color
     * to the config file.
     */
    private final StringConfigValue walletBalanceColor = new StringConfigValue(
            "wallet-gui-balance-color",
            "The wallet gui's balance color, as a hexadecimal color code.",
            "#00E500", this
    );

    // Category Name

    /**
     * Creates the new, unique configuration category
     * under the name "Appearance" to store settings
     * that affect looks.
     */
    public AppearanceConfig() {
        super("Appearance");
    }

    // Getters

    /**
     * @return the boolean value set in the configuration file
     * that tell us if tooltips should or shouldn't be shown.
     */
    public boolean allowTooltipsInWalletGui() {
        return enableWalletTooltips.getValue();
    }

    /**
     * @return the hex string value set in the configuration file
     * that tell us the color of the balance displayed in the
     * Wallet Gui.
     */
    public int getWalletGuiBalanceColor() {
        String color = walletBalanceColor.getValue();

        if(color.startsWith("#"))
            color = color.replace("#", "0x");

        return Color.decode(color).getRGB();
    }
}
