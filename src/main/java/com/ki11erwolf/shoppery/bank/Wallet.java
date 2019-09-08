package com.ki11erwolf.shoppery.bank;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.PlayerUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.*;

/**
 * Player Wallet.
 *
 * Stores the currency balance of a single
 * player. This class is used to retrieve
 * and modify the balance of a player.
 *
 * The {@link Bank} is responsible for
 * storage, retrieval, saving, and loading
 * of player wallets. A wallet cannot be
 * created from this class, rather, a
 * wallet must be obtained from the
 * Bank class.
 *
 * @see BankManager for obtaining Bank & Wallet objects.
 */
@SuppressWarnings("WeakerAccess")
public class Wallet {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = ShopperyMod.getNewLogger();

    /**
     * Regex Expression to match currency in String form (e.g. 99.99, 100, ect).
     *
     * Copied from: https://stackoverflow.com/questions/354044/what-is-the-best-u-s-currency-regex/354216#354216
     * Original Library/Origin: Regex Buddy (https://www.regexbuddy.com/)
     */
    private static final String BALANCE_REGEX = "^[+-]?[0-9]{1,3}(?:,?[0-9]{3})*(?:\\.[0-9]{2})?$";

    /**
     * The symbol to represent the currency.
     */
    private static final char CURRENCY_SYMBOL = '$';

    /**
     * The player this wallet belongs to.
     */
    private final PlayerEntity player;

    /**
     * The balance of this wallet
     * and the player it belongs to
     * (balance > 0).
     */
    private long balance;

    /**
     * The cents (leading decimals)
     * of the players balance.
     */
    private byte cents;

    /**
     * Creates a new wallet for the given
     * player.
     *
     * @param player the player the wallet belongs to.
     * @param balance given balance of the player (balance > 0).
     * @param cents the cents (leading decimals) of the players
     *              balance (100 > cents > 0).
     */
    Wallet(PlayerEntity player, long balance, byte cents){
        this.player = Objects.requireNonNull(player, "Wallet player cannot be null.");

        if(cents > 100 || cents < 0)
            throw new IllegalArgumentException("cents > 100 || cents < 0");

        if(balance < 0)
            throw new IllegalArgumentException("balance < 0");

        this.balance = balance;
        this.cents = cents;
        balance();
    }

    //************
    // PUBLIC API
    //************

    /**
     * Sets the balance for this wallet. This
     * method will reset cents to 0.
     *
     * @param balance the new balance of this wallet.
     */
    public void setBalance(long balance){
        if(balance < 0)
            throw new IllegalArgumentException("balance < 0");

        this.balance = balance;
        this.cents = 0;
        balance();
    }

    /**
     * Sets the balance for this wallet.
     *
     * @param balance the new balance of this wallet.
     * @param cents the amount of cents the wallet
     *              should have.
     */
    public void setBalance(long balance, byte cents){
        if(balance < 0)
            throw new IllegalArgumentException("balance < 0");

        if(cents > 99)
            throw new IllegalArgumentException("cents > 99");

        if(cents < 0)
            throw new IllegalArgumentException("cents < 0");

        LOGGER.debug("Setting player: " + player.getGameProfile().getName() + " balance: " + balance + "-" + cents);
        this.cents = cents;
        this.balance = balance;
        balance();
    }

    /**
     * Adds the specified amount
     * to the wallet balance.
     *
     * @param balance the amount to add.
     */
    public void add(long balance){
        if(balance < 0)
            throw new IllegalArgumentException("balance < 0");

        LOGGER.debug("Adding to player: " + player.getGameProfile().getName() + " balance: " + balance);
        this.balance += balance;
        balance();
    }

    /**
     * Adds the specified amount
     * to the wallet balance.
     *
     * @param balance the amount to add.
     * @param cents the amount of cents to add.
     */
    public void add(long balance, byte cents){
        if(cents > 99)
            throw new IllegalArgumentException("cents > 99");

        if(cents < 0)
            throw new IllegalArgumentException("cents < 0");

        if(balance < 0)
            throw new IllegalArgumentException("balance < 0");

        short sum = (short)(this.cents + cents);

        LOGGER.debug("Adding to player: " + player.getGameProfile().getName() + " balance: " + balance + "-" + cents);

        if(sum > 99){
            this.balance += sum / 100;
            this.cents = (byte) (sum % 100);
        } else {
            this.cents = (byte)sum;
        }

        this.balance += balance;
        balance();
    }

    /**
     * Adds the specified amount to the wallet balance.
     *
     * Given balance must in one of the following formats:
     *   1
     *   100
     *   1,000
     *   100.00
     *   100.99
     * Only one decimal place is allowed.
     * Decimal place must have two digits.
     * Invalid formats:
     * -1
     * 100.000
     * 100.1
     *
     * @param balance the amount to add to the wallet balance.
     * @throws NumberFormatException if the given balance
     * is not in the correct format.
     */
    public void add(float balance){
        add(String.valueOf(balance));
    }

    /**
     * Adds the specified amount to the wallet balance.
     *
     * Given balance must in one of the following formats:
     *   1
     *   100
     *   1,000
     *   100.00
     *   100.99
     * Only one decimal place is allowed.
     * Decimal place must have two digits.
     * Invalid formats:
     * -1
     * 100.000
     * 100.1
     *
     * @param balance the amount to add to the wallet balance.
     * @throws NumberFormatException if the given balance
     * is not in the correct format.
     */
    public void add(String balance){
        //Don't allow commas & currency symbol.
        balance = Objects.requireNonNull(balance.replace(",", "").replace("$", ""));

        if(!balance.matches(BALANCE_REGEX))
            throw new NumberFormatException("Balance not in correct format: " + balance);

        if(balance.contains("-"))
            throw new NumberFormatException("Negative balance: " + balance);

        if(balance.contains(".")){//Has cents(decimal)
            String[] values = balance.split("\\.");

            if(values.length != 2 || values[1].length() != 2)
                throw new NumberFormatException("Balance not in correct format (decimals): " + balance);

            //noinspection UnnecessaryBoxing
            add(Long.valueOf(values[0]), Byte.valueOf(values[1]));
        } else {//Has no cents(decimal)
            //noinspection UnnecessaryBoxing
            add(Long.valueOf(balance));
        }
    }

    /**
     * Subtracts the given amount from
     * this wallet IF the remaining
     * amount/balance is above 0.
     *
     * @param balance the amount to subtract.
     * @return {@code true} if the amount
     * was taken off, {@code false} if the
     * wallet doesn't have a big enough
     * balance.
     */
    public boolean subtract(long balance){
        if(balance < 1)
            throw new IllegalArgumentException("balance < 1");

        if(this.balance < balance)
            return false;

        LOGGER.debug("Taking from player: " + player.getGameProfile().getName() + " balance: " + balance);
        this.balance -= balance;
        balance();
        return true;
    }

    /**
     * Subtracts the given amount from
     * this wallet IF the remaining
     * amount/balance is above 0.
     *
     * @param balance the amount to subtract.
     * @param cents the amount of cents to subtract.
     * @return {@code true} if the amount
     * was taken off, {@code false} if the
     * wallet doesn't have a big enough
     * balance.
     */
    public boolean subtract(long balance, byte cents){
        if(cents > 99)
            throw new IllegalArgumentException("cents > 99");

        if(cents < 0)
            throw new IllegalArgumentException("cents < 0");

        if(balance < 0)
            throw new IllegalArgumentException("balance < 0");


        int newCents = this.cents - cents;
        long newBalance = this.balance;

        if(newCents < 0){
            newBalance--;
            newCents += 100;
        }

        newBalance -= balance;

        if(newBalance == 0){
            if (newCents == 0)
                return false;
        }

        if(newBalance < 0)
            return false;

        LOGGER.debug("Taking from player: " + player.getGameProfile().getName() + " balance: " + balance + "." + cents);

        this.balance = newBalance;
        this.cents = (byte)newCents;

        balance();
        return true;
    }

    /**
     * Subtracts the specified amount from the wallet balance.
     *
     * Given balance must in one of the following formats:
     *   1
     *   100
     *   1,000
     *   100.00
     *   100.99
     * Only one decimal place is allowed.
     * Decimal place must have two digits.
     * Invalid formats:
     * -1
     * 100.000
     * 100.1
     *
     * @param balance the amount to subtract from the wallet balance.
     * @throws NumberFormatException if the given balance
     * is not in the correct format.
     * @return {@code true} if the wallet has a sufficient balance
     * and the amount was taken off, {@code false} otherwise.
     */
    public boolean subtract(String balance){
        //Don't allow commas & currency symbol.
        balance = Objects.requireNonNull(balance.replace(",", "").replace("$", ""));

        if(!balance.matches(BALANCE_REGEX))
            throw new NumberFormatException("Balance not in correct format: " + balance);

        if(balance.contains("-"))
            throw new NumberFormatException("Negative balance: " + balance);

        if(balance.contains(".")){//Has cents(decimal)
            String[] values = balance.split("\\.");

            if(values.length != 2 || values[1].length() != 2)
                throw new NumberFormatException("Balance not in correct format (decimals): " + balance);

            //noinspection UnnecessaryBoxing
            return subtract(Long.valueOf(values[0]), Byte.valueOf(values[1]));
        } else {//Has no cents(decimal)
            //noinspection UnnecessaryBoxing
            return subtract(Long.valueOf(balance));
        }
    }

    /**
     * Subtracts the specified amount from the wallet balance.
     *
     * Given balance must in one of the following formats:
     *   1
     *   100
     *   1,000
     *   100.00
     *   100.99
     * Only one decimal place is allowed.
     * Decimal place must have two digits.
     * Invalid formats:
     * -1
     * 100.000
     * 100.1
     *
     * @param balance the amount to subtract from the wallet balance.
     * @throws NumberFormatException if the given balance
     * is not in the correct format.
     * @return {@code true} if the wallet has a sufficient balance
     * and the amount was taken off, {@code false} otherwise.
     */
    public boolean subtract(float balance){
        return subtract(String.valueOf(balance));
    }

    /**
     * @return the balance of this wallet. This excludes
     * the cents in the wallet.s
     */
    public long getBalance(){
        return this.balance;
    }

    /**
     * @return the amount of cents in this wallet.
     */
    public byte getCents(){
        return this.cents;
    }

    /**
     * @return the players full balance (with commas & currency symbol).
     * E.g. $100.00, $1,000.00 $1,000,000.00
     */
    public String getFullBalance(){
        if(cents < 10) return CURRENCY_SYMBOL + NumberFormat.getInstance().format(balance) + ".0" + cents;
        else return CURRENCY_SYMBOL + NumberFormat.getInstance().format(balance) + "." + cents;
    }


    public String getFormattedBalance(){
        if(balance == 0)
            if(cents < 10){
                return "0.0" + cents + 'c';
            } else {
                return "0." + cents + 'c';
            }

        if(cents < 10) return CURRENCY_SYMBOL + format(balance);
        else return CURRENCY_SYMBOL + format(balance);
    }

    /**
     * @return the player this wallet belongs to.
     */
    public PlayerEntity getPlayer(){
        return this.player;
    }

    /**
     * @return the string representation of this wallet object.
     */
    @Override
    public String toString(){
        return String.format(
                "Wallet[player: %s, balance: %s, cents: %s, full: %s]",
                getPlayer().getName().getString(), balance, cents, getFullBalance()
        );
    }

    /**
     * Makes sure the players balance
     * is correct.
     *
     * This includes making sure
     * the balance is always above
     * 0 and that the cents is ever
     * over 100 (will add the excess
     * to the balance if it is).
     */
    private void balance(){
        if(balance < 0)
            balance = 0;

        if(cents < 0)
            cents = 0;

        if(cents > 99){
            balance += cents / 100;
            cents = (byte) (cents % 100);
        }
    }

    //****************
    // INTERNAL LOGIC
    //****************

    /*
        Json structure used to store
        and retrieve wallet data:
        {"PlayerName": "<player name>, "Balance": "<balance>, "Cents": "<cents>"}.
        We write the player name to identify different wallets when editing
        the file manually.
     */

    /**
     * An enum set of keys used when
     * writing the wallet to json.
     */
    enum WalletObjectKeys {

        /**
         * Key-value pair that stores
         * the players name.
         */
        PLAYER_NAME("PlayerName"),

        /**
         * Key-value pair that stores
         * the players balance.
         */
        BALANCE("Balance"),

        /**
         * Key-value pair that stores
         * the players balance cents.
         */
        CENTS("Cents");

        /**
         * Value used in json.
         */
        public final String value;

        /**
         * @param value Value used in json.
         */
        WalletObjectKeys(String value){
            this.value = value;
        }
    }

    /**
     * Creates a JsonObject that holds
     * all the data in this wallet
     * object which can be used to store
     * this wallet object on file.
     *
     * @return a JsonObject that can
     * be used to store this wallet on file.
     */
    JsonObject getWalletAsJsonObject(){
        balance();
        JsonObject walletObject = new JsonObject();

        walletObject.add(
                WalletObjectKeys.PLAYER_NAME.value,
                new JsonPrimitive(player.getGameProfile().getName())
        );

        walletObject.add(
                WalletObjectKeys.BALANCE.value,
                new JsonPrimitive(balance)
        );

        walletObject.add(
                WalletObjectKeys.CENTS.value,
                new JsonPrimitive(cents)
        );

        return walletObject;
    }

    /**
     * Takes a JsonWalletObject (containing
     * wallet data in the correct format)
     * and constructs a new Wallet from it.
     *
     * @param jWallet the JsonObject containing wallet
     *                data
     * @param player the player entity.
     * @return the newly created wallet object or {@code null}
     * if the json wallet object couldn't be parsed.
     */
    @SuppressWarnings("WeakerAccess")
    static Wallet createWalletFromJsonObject(JsonObject jWallet, PlayerEntity player){
        JsonElement jBalance = jWallet.get(WalletObjectKeys.BALANCE.value);
        JsonElement jCents = jWallet.get(WalletObjectKeys.CENTS.value);

        if(jBalance == null
                || jCents == null){
            LOGGER.warn("Invalid json wallet object found: " + jWallet.toString());
            return null;
        }

        long balance = jBalance.getAsLong();
        byte cents = jCents.getAsByte();

        return new Wallet(player, balance, cents);
    }

    /**
     * Takes a JsonWalletObject (containing
     * wallet data in the correct format)
     * and constructs a new Wallet from it.
     *
     * @param jWallet the JsonObject containing wallet
     *                data
     * @param playerUUID the player entity's unique ID.
     * @return the newly created wallet object or {@code null}
     * if the json wallet object couldn't be parsed.
     */
    static Wallet createWalletFromJsonObject(JsonObject jWallet, UUID playerUUID){
        return createWalletFromJsonObject(jWallet, PlayerUtil.getPlayerFromUUID(playerUUID));
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
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    /*
     * Initializes the map.
     */
    static {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "Q");
    }

    /**
     * Shortens a long value.
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

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        //noinspection IntegerDivisionInFloatingPointContext
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
