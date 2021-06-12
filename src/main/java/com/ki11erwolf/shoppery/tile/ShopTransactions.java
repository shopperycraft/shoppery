package com.ki11erwolf.shoppery.tile;

import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * An object used by {@link ShopTile}s to store, manage, and
 * retrieve transactions, for the specific purpose of allowing
 * players to "undo" transactions within a specific amount of
 * time ({@link ShopTransactions#TRANSACTION_KEEP_TIME}).
 *
 * <p>Every transaction a shop makes, that is not a reverse
 * transaction, is logged ({@link #logTransaction(PlayerEntity, boolean)})
 * to the object with a timestamp. When the shop makes another
 * trade, it can query ({@link #reverseTransaction(PlayerEntity, boolean)})
 * the list of transactions, and if transaction with a matching
 * player and opposite flag is found, the shop can buy/sell back
 * the item for the same price. Transactions older than the set
 * lifetime will be removed when the object is queried.
 */
class ShopTransactions {

    /**
     * The amount of time, in milliseconds, to keep a PlayerTransaction. Set through mod config.
     */
    private static final long TRANSACTION_KEEP_TIME = ShopTile.SHOPS_CONFIG.getReversalTimeLimit();

    /**
     * The list that holds all the logged PlayerTransactions for later querying. Transactions
     * that have reached the end of their lifespan are removed when the list is queried using
     * {@link #reverseTransaction(PlayerEntity, boolean)}.
     */
    private final List<TimedObjectReference<PlayerTransaction>> transactionList = new ArrayList<>();

    /**
     * A variable object used solely by the {@link #reverseTransaction(PlayerEntity, boolean)}
     * method. Holds the last transaction found that match the search query, if any.
     */
    private TimedObjectReference<PlayerTransaction> foundTransaction;

    /**
     * Will add and store a new player transaction record to this object,
     * that identifies a player and transaction type (sale/purchase).
     * After a set amount of time ({@link #TRANSACTION_KEEP_TIME}), the
     * player transaction record will expire and be removed.
     *
     * @param player the player who made the transaction.
     * @param isTypeSale {@code true} if the transaction was a
     * sale from a shop to player, {@code false} if the
     */
    void logTransaction(PlayerEntity player, boolean isTypeSale){
        transactionList.add(new TimedObjectReference<>(
                new PlayerTransaction(player.getUniqueID(), isTypeSale), TRANSACTION_KEEP_TIME)
        );
    }

    /**
     * Queries the entire list of logged transactions (that have not yet expired or
     * been removed) for the existence of a transaction record, made by the specified
     * player, and matching the transaction type specified.
     *
     * <p/>All records are processed, from oldest to newest, to check for all
     * transaction records that match the query. Only the latest matching record, if
     * any were found, is removed from the log once all records have been processed.
     * The method will also return {@code true} if one or more records were found.
     *
     * <p/>All transaction records that are older than the {@link #TRANSACTION_KEEP_TIME
     * specified lifespan} are removed from the records as well during processing -
     * keeping the records fresh and light on memory. Old records will remain referenced
     * and in memory indefinitely if this method is never called.
     *
     * <p/><b>NOTE: </b> do not log reverse transactions as though they were transactions!
     *
     * @param player the player that made the transactions that are being queried for.
     * @param isTypeSale the type of transactions that are being queried for, in
     *                   addition to player.
     * @return {@code true} if a transaction matching the query was found, and the
     * latest was removed, {@code false} if no matching queries were found.
     */
    boolean reverseTransaction(PlayerEntity player, boolean isTypeSale) {
        transactionList.removeIf(TimedObjectReference::expired);

        //Always iterates the entire list - keeps it from accumulating many references
        for(TimedObjectReference<PlayerTransaction> reference : transactionList) {
            //Get object from reference, or null-out reference if expired
            PlayerTransaction transaction;
            if((transaction = reference.get()) == null)
                continue;

            //Check if matches query
            if(transaction.getPlayerUUID().equals(player.getUniqueID())) {
                if(transaction.wasSale() == isTypeSale) {
                    //use the latest matching transaction
                    foundTransaction = reference;
                }
            }
        }

        //if a match was found after processing all records
        if(foundTransaction != null) {
            //Remove it and return a positive match
            transactionList.remove(foundTransaction);
            foundTransaction = null;
            return true;
        }

        //otherwise return a negative match
        return false;
    }

    // Classes

    /**
     * A transaction object, which is created and stored
     * in a ShopTransactions object for every shop
     * transaction that is made. Each transaction object
     * stores a player reference and either a purchase or
     * sale marker, which can be queried later to reverse
     * or "undo" transactions.
     *
     * <p/>PlayerTransactions are wrapped in TimeBoundObjects
     * to ensure they're only kept for a specific amount
     * of time before being deleted.
     */
    private static class PlayerTransaction {

        /**
         * The {@link PlayerEntity#getUniqueID() unique ID} of the player
         * who made the transaction.
         */
        private final UUID playerUUID;

        /**
         * The flag that describes if this transaction object records
         * a purchase or a sale.
         */
        private final boolean sale;

        /**
         * Creates a new player transaction object that records a
         * specific player and the type of transaction the player
         * made.
         *
         * @param playerUUID the unique ID of the player who made the
         *                   transaction.
         * @param sale {@code true} if the transaction was a sale from
         *                         shop to player, {@code false} if the
         *                         transaction was a purchase from
         *                         player to shop.
         */
        public PlayerTransaction(UUID playerUUID, boolean sale) {
            this.playerUUID = playerUUID;
            this.sale = sale;
        }

        /**
         * @return the {@link PlayerEntity#getUniqueID()} of the player
         * who made the transaction.
         */
        public UUID getPlayerUUID() {
            return playerUUID;
        }

        /**
         * @return the flag that describes if this transaction object records
         * a purchase or a sale. {@code true} means it is/was a sale.
         */
        public boolean wasSale() {
            return sale;
        }
    }

    /**
     * A type of object reference that holds onto an object
     * until the amount of time specified has elapsed, after
     * which, the reference becomes unobtainable from this
     * object.
     *
     * <p/>References to objects will remain until they are
     * queried using either {@link #check()}, {@link #get()},
     * or {@link #expired()}, <b>even if expired</b>.
     *
     * @param <T> the type of object to store.
     */
    private static class TimedObjectReference<T> {

        /**
         * The variable used to store the specific
         * object until it's lifetime is reached.
         */
        private T object;

        /**
         * The number of milliseconds the object
         * has to live, starting from the moment of
         * creation, before it's removed from memory.
         */
        private final long lifetime;

        /**
         * Creates a new timed reference to any object.
         * The lifespan is set and starts counting at
         * creation time.
         *
         * @param object the object to reference.
         * @param lifetime the amount of milliseconds
         * to hold onto the object.
         */
        public TimedObjectReference(T object, long lifetime){
            this.object = Objects.requireNonNull(object);
            this.lifetime = System.currentTimeMillis() + lifetime;
        }

        /**
         * Gets the {@link System#currentTimeMillis() exact time
         * of the current system}, accurate to the millisecond,
         * and checks it against the lifetime of the object
         * reference. If the object reference is found to be older
         * than its lifetime, it will be removed and all internal
         * references will be nulled-out.
         */
        private void check(){
            if(System.currentTimeMillis() >= lifetime)
                object = null;
        }

        /**
         * Gets the {@link System#currentTimeMillis() exact time
         * of the current system} and checks it against the lifetime
         * of the object reference to determine if it has expired.
         *
         * @return {@code true} if the object reference is still
         * accessible and within its lifespan, {@code false} if the
         * reference has outlived its lifespan and is no longer
         * accessible.
         *
         * @see #check()
         */
        public boolean expired(){
            return get() == null;
        }

        /**
         * Performs an attempt to get the time-bound object reference
         * if it has not yet {@link #expired()}.
         *
         * <p/>If the reference has expired, internal references will
         * be nulled-out/removed, and the object will no longer be
         * accessible from this object.
         *
         * <p/>Checked by getting the {@link System#currentTimeMillis()
         * exact time of the current system} and comparing it against
         * the lifetime of the object reference.
         *
         * @see #check()
         *
         * @return the object that is held and referenced by this object,
         * or {@code null} if the object has {@link #expired()}.
         */
        @Nullable
        public T get(){
            check();
            return object;
        }
    }
}
