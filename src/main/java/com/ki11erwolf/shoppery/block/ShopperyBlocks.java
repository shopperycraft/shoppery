package com.ki11erwolf.shoppery.block;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.QueueRegisterer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Holds all instances of shoppery blocks and handles
 * the registration of them.
 */
@Mod.EventBusSubscriber(modid = ShopperyMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShopperyBlocks extends QueueRegisterer<Block> {

    /**
     * Private singleton instance of this class.
     */
    private static final ShopperyBlocks INSTANCE = new ShopperyBlocks();
    private ShopperyBlocks(){}

    public static final ShopperyBlock<?> TEST_BLOCK = new ShopperyBlock<ShopperyBlock<?>>(
            Block.Properties.create(Material.EARTH), "test"
    ).queueRegistration();

    //############################
    //     Block Registration
    //############################

    /**
     * Forge block register event.
     *
     * <p>Iteratively registers every shoppery block queued
     * for registration ({@link ShopperyBlock#queueRegistration()}
     * to the game using forges block registration event.
     *
     * @param event forge event.
     */
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Block> event) {
        INSTANCE.iterateQueue(item -> event.getRegistry().register(item));
    }

    /**
     * Adds a Shoppery Block instance
     * to the block registration queue.
     *
     * @param block the block to queue.
     */
    static void queueItem(Block block){
        INSTANCE.queueForRegistration(block);
    }

}
