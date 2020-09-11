package com.ki11erwolf.shoppery.block;

public class AbstractShopBlock<T extends AbstractShopBlock<?>> extends ShopperyBlock<T>{

    public AbstractShopBlock(Properties properties, String registryName) {
        super(properties, registryName);
    }

}
