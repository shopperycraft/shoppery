package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.gui.ShopperyButton;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Client side proxy for the Shoppery mod.
 */
public class ClientProxy extends ServerProxy {

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(FMLCommonSetupEvent event) {
        //Do registration for the internal server as well.
        super.setup(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clientOnlySetup(FMLClientSetupEvent event) {
        ShopperyButton.init();
    }
}
