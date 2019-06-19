package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.command.Command;
import com.ki11erwolf.shoppery.network.packets.Packet;
import com.ki11erwolf.shoppery.util.TolerantArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEntityJoinWorld(EntityJoinWorldEvent joinWorldEvent){
        Entity entity = joinWorldEvent.getEntity();

        if(!(entity instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer)entity;

        if(! (player.inventoryContainer.inventorySlots instanceof TolerantArrayList)){
            player.inventoryContainer.inventorySlots = new TolerantArrayList<>(
                    player.inventoryContainer.inventorySlots
            );
        }
    }
}
