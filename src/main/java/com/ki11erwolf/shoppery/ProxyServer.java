package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.block.ModBlocks;
import com.ki11erwolf.shoppery.command.Command;
import com.ki11erwolf.shoppery.item.ModItems;
import com.ki11erwolf.shoppery.packets.Packet;
import com.ki11erwolf.shoppery.price.ItemPrices;
import com.ki11erwolf.shoppery.tile.ModTiles;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Server side proxy for the Shoppery mod.
 */
public class ProxyServer implements Proxy {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNativeSetup() {
        ItemPrices.loadPriceRegistry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerSetup(FMLCommonSetupEvent event) {
        Packet.init();
        Command.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRegistrySetup(IEventBus eventBus) {
        eventBus.register(ModBlocks.BLOCKS);
        eventBus.register(ModItems.ITEMS);
        eventBus.register(ModTiles.TILES);
    }
}
