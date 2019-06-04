package com.ki11erwolf.shoppery.bank;

import net.minecraft.entity.player.EntityPlayer;

import java.util.Objects;

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
 */
public class Wallet {

    /**
     * The symbol to represent the currency.
     */
    private static final char CURRENCY_SYMBOL = '$';

    /**
     * The player this wallet belongs to.
     */
    private final EntityPlayer player;

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
    Wallet(EntityPlayer player, long balance, byte cents){
        this.player = Objects.requireNonNull(player, "Wallet player cannot be null.");

        if(cents > 100 || cents <= 0)
            throw new IllegalArgumentException("cents > 100 || cents <= 0");

        if(balance < 0)
            throw new IllegalArgumentException("balance < 0");

        this.balance = balance;
        this.cents = cents;
    }

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
        if(balance < 1)
            throw new IllegalArgumentException("balance < 1");

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

        if(cents < 1)
            throw new IllegalArgumentException("cents < 1");

        if(balance < 1)
            throw new IllegalArgumentException("balance < 1");

        short sum = (short)(this.cents + cents);

        if(sum > 99){
            this.balance += sum / 100;
            this.cents = (byte) (sum % 100);
        } else {
            this.cents = (byte)sum;
        }

        add(balance);
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

        if(cents < 1)
            throw new IllegalArgumentException("cents < 1");

        if(balance < 0)
            throw new IllegalArgumentException("balance < 0");

        double bal;
        double sub;

        if(this.cents < 10)
            bal = Math.round(Double.parseDouble(this.balance + ".0" + this.cents) * 100.0) / 100.0;
        else
            bal = Math.round(Double.parseDouble(this.balance + "." + this.cents) * 100.0) / 100.0;

        if(cents < 10)
            sub = Math.round(Double.parseDouble(balance + ".0" + cents) * 100.0) / 100.0;
        else
            sub = Math.round(Double.parseDouble(balance + "." + cents) * 100.0) / 100.0;

        if(bal - sub < 0)
            return false;

        String sum = String.valueOf((double)Math.round((bal - sub) * 100.0) / 100.0);

        if(sum.contains(".")){
            String[] sumA = sum.split("\\.");
            this.balance = Long.valueOf(sumA[0]);
            this.cents = Byte.valueOf(sumA[1]);
        } else {
            this.balance = Long.valueOf(sum);
            this.cents = 0;
        }

        balance();
        return true;
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
     * @return a styled textual representation
     * of the balance in this wallet. This includes
     * the currency symbol, any formatting commas
     * and a leading letter to represent large
     * amounts (e.g. $10M for $10,000,000).
     */
    public String getTextualBalance(){
        String display = balance + "." + cents;
        //TODO: Create proper styling for textual balance.
        return CURRENCY_SYMBOL + display;
    }

    /**
     * @return the player this wallet belongs to.
     */
    public EntityPlayer getPlayer(){
        return this.player;
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
}
