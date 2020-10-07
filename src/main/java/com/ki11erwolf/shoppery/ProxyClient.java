package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.gui.WalletButton;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Client side proxy for the Shoppery mod.
 */
public class ProxyClient extends ProxyServer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        WalletButton.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNativeSetup() {
        super.onNativeSetup(); //Remember server
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerSetup(FMLCommonSetupEvent event) {
        super.onServerSetup(event); //Remember server
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRegistrySetup(IEventBus eventBus) {
        super.onRegistrySetup(eventBus); //Remember server
    }
}
