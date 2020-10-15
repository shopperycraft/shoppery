package com.ki11erwolf.shoppery.config.categories;

import com.ki11erwolf.shoppery.config.BooleanConfigValue;
import com.ki11erwolf.shoppery.config.ConfigCategory;
import com.ki11erwolf.shoppery.config.StringConfigValue;

/**
 * The configuration category that holds settings
 * related to Shops, Shop Blocks, and Shopping.
 */
public class ShopsConfig extends ConfigCategory {

    /**
     * The possible values for the buy-button configuration option.
     */
    private static final String BUY_BUTTON_RIGHT = "right", BUY_BUTTON_LEFT = "left";

    /**
     * The config property defining the config value that allows
     * setting the button used to buy/purchase from shops.
     */
    private final StringConfigValue buyButton = new StringConfigValue(
            "mouse-button-to-buy",
            "The mouse button, either 'left' or 'right', that is " +
                    "used to purchase Items from a Shop. The alternative button is " +
                    "then used to sell Items to Shops - so if 'left' is assigned to purchase, " +
                    "'right' will be assigned to sell. The value will always default to 'right' " +
                    "when the value is not set or set incorrectly.",
            BUY_BUTTON_RIGHT, this
    );

    /**
     * The config property defining the config flag that allows
     * making an empty hand required to trade with shops.
     */
    private final BooleanConfigValue requireEmptyHandToUse = new BooleanConfigValue(
            "require-empty-hand-to-trade",
            "The flag that makes players need to use an empty hand " +
                    "in order to trade with shops. This value affects all players " +
                    "in the server!",
            true, this
    );

    /**
     * The config property defining the config flag that allows
     * making sneaking require to trade with shops.
     */
    private final BooleanConfigValue requireSneakToUse = new BooleanConfigValue(
            "require-sneak-to-trade",
            "The flag that makes players need to use the sneak button " +
                    "in order to trade with shops. This value affects all players " +
                    "in the server!",
            false, this
    );

    /**
     * Constructs the Shops config category.
     */
    public ShopsConfig() {
        super("Shops");
    }

    /**
     * @return {@code true} if the config value requiring
     * an empty hand be used to trade with shops is enabled
     * within the config file.
     */
    public boolean requireEmptyHandToUse(){
        return requireEmptyHandToUse.getValue();
    }

    /**
     * @return {@code true} if the config value requiring
     * sneaking be used to trade with shops is enabled
     * within the config file.
     */
    public boolean requireSneakToUse(){
        return requireSneakToUse.getValue();
    }

    /**
     * @return {@code "left"} if the config value that
     * defines the button used to buy is set to Left-Button
     * or an equivalent. The value {@code "right"} is always
     * returned by default, or if the value is invalid.
     */
    public String getBuyButton(){
        return checkAndGetBuyButton();
    }

    /**
     * @return {@code true} if the config value that
     * defines the button used to buy is set to Right-Button,
     * an equivalent, default, or invalid.
     */
    public boolean isBuyRightClick(){
        return !isBuyLeftClick();
    }

    /**
     * @return {@code true} if the config value that
     * defines the button used to buy is set to Left-Button
     * or an equivalent, {@code false} otherwise.
     */
    public boolean isBuyLeftClick(){
        return getBuyButton().equals(BUY_BUTTON_LEFT);
    }

    /**
     * Gets the value defined in {@link #buyButton} and
     * checks it, both to determine the value, and to
     * ensure it's correct. If the value is not perfectly
     * correct or it is invalid, it will set correctly
     * in the config.
     *
     * @return the  the config value that defines the
     * button used to buy from shops. Defaults to {@code
     * "right"}.
     */
    private String checkAndGetBuyButton(){
        String input = buyButton.getValue().toLowerCase();

        if(input.equals(BUY_BUTTON_RIGHT))
            return input;
        else if(input.equals(BUY_BUTTON_LEFT))
            return input;
        else if (input.startsWith(BUY_BUTTON_LEFT) || input.endsWith(BUY_BUTTON_LEFT)) {
            buyButton.setValue(BUY_BUTTON_LEFT);
            return BUY_BUTTON_LEFT;
        } else {
            buyButton.setValue(BUY_BUTTON_RIGHT);
            return BUY_BUTTON_RIGHT;
        }
    }
}
