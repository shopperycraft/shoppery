package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.util.QueueRegisterer;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Holds all instances of shoppery blocks and handles the registration of them.
 */
public final class ModBlocks extends QueueRegisterer<Block> {

    /**
     * Private singleton instance of this class.
     */
    public static final ModBlocks BLOCKS = new ModBlocks();
    private ModBlocks(){}

    //############################
    //   Public Block Instances
    //############################

    /**
     * The most basic shop block.
     */
    public static final ShopBlock SHOP_BLOCK = new ShopBlock("shop").queueRegistration();

    //############################
    //     Block Registration
    //############################

    /**
     * Forge block register event.
     *
     * <p>Iteratively registers every shoppery block queued
     * for registration ({@link ModBlock#queueRegistration()}
     * to the game using forges block registration event.
     *
     * @param event forge event.
     */
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        this.iterateQueue(item -> event.getRegistry().register(item));
    }

    /**
     * Adds a Shoppery Block instance
     * to the block registration queue.
     *
     * @param block the block to queue.
     */
    static void queueItem(Block block){
        BLOCKS.queueForRegistration(block);
    }

}
