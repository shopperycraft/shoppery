package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.command.Command;
import com.ki11erwolf.shoppery.packets.Packet;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Server side proxy for the Shoppery mod.
 */
public class ServerProxy implements Proxy {

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(FMLCommonSetupEvent event) {
        Packet.init();
        Command.init();
    }

}
