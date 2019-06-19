package com.ki11erwolf.shoppery.item;

class DepositItem extends ShopperyItem<DepositItem> {

    /**
     * Package private constructor to prevent
     * item instance creation from outside
     * packages.
     */
    DepositItem() {
        super(new Properties(), false, "deposit");
    }

}
