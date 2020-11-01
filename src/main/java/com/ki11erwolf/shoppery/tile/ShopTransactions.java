package com.ki11erwolf.shoppery.tile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ShopTransactions {

    private static final long TRANSACTION_KEEP_TIME = ShopTile.SHOPS_CONFIG.getReversalTimeLimit();

    private final List<TimeBoundObject<PlayerTransaction>> transactionList = new ArrayList<>();

    public void logTransaction(UUID playerUUID, boolean isSaleTransaction){
        transactionList.add(new TimeBoundObject<>(new PlayerTransaction(playerUUID, isSaleTransaction), TRANSACTION_KEEP_TIME));
    }

    private TimeBoundObject<PlayerTransaction> foundTransaction;
    public boolean reverseTransaction(UUID playerUUID, boolean isSaleTransaction) {
        transactionList.removeIf(TimeBoundObject::isDead);
        transactionList.forEach(transactionHolder -> {
            PlayerTransaction transaction;
            if((transaction = transactionHolder.get()) == null)
                return;

            if(transaction.getPlayerUUID().equals(playerUUID)){
                if(transaction.wasSale() == isSaleTransaction){
                    foundTransaction = transactionHolder;
                }
            }
        });

        if(foundTransaction != null){
            transactionList.remove(foundTransaction);
            foundTransaction = null;
            return true;
        } else return false;
    }

    // Classes

    private static class PlayerTransaction {

        private final UUID playerUUID;

        private final boolean sale;

        public PlayerTransaction(UUID playerUUID, boolean sale) {
            this.playerUUID = playerUUID;
            this.sale = sale;
        }

        public UUID getPlayerUUID() {
            return playerUUID;
        }

        public boolean wasSale() {
            return sale;
        }
    }

    private static class TimeBoundObject<T> {

        private T object;

        private final long timeToDie;

        public TimeBoundObject(T object, long timeToLive){
            this.object = Objects.requireNonNull(object);
            this.timeToDie = System.currentTimeMillis() + timeToLive;
        }

        private void check(){
            if(System.currentTimeMillis() >= timeToDie)
                object = null;
        }

        public boolean isDead(){
            return get() == null;
        }

        @Nullable
        public T get(){
            check();
            return object;
        }
    }
}
