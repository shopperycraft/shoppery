package com.ki11erwolf.shoppery.price;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import static com.ki11erwolf.shoppery.price.PriceRegistry.INSTANCE;

public class PriceAPI {
    private PriceAPI(){}

    // Initializing

    public static void loadPriceRegistry(){
        INSTANCE.load();
    }

    public static boolean isLoaded(){
        return INSTANCE.isLoaded();
    }

    // Getters

    public static ItemPrice getPrice(ItemStack stack){
        blockUntilLoaded();
        return INSTANCE.getPriceMap().get(stack.getItem().getRegistryName());
    }

    public static ItemPrice getPrice(Item item){
        blockUntilLoaded();
        return INSTANCE.getPriceMap().get(item.getRegistryName());
    }

    public static ItemPrice getPrice(Block block){
        blockUntilLoaded();
        return INSTANCE.getPriceMap().get(block.getRegistryName());
    }

    private static void blockUntilLoaded(){
        while(!INSTANCE.isLoaded()){
            try {
                //Keep thread usage down
                Thread.sleep(10);
            } catch (InterruptedException e) {
                //NO-OP
            }
        }
    }
}
