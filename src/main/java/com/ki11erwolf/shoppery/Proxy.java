package com.ki11erwolf.shoppery;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

/**
 * Shoppery mod proxy interface.
 */
public interface Proxy {

    /**
     * The pre-mod-setup register & setup event called by the mod
     * during construction.
     *
     * <p>Called to construct and setup mod systems that don't
     * depend (or directly depend) on MC/Forge or other Shoppery
     * systems, and registered events.
     *
     * <p>Call order: First, before everything & anything, including
     * the mod setup event(s).
     */
    void onNativeSetup();

    /**
     * The mods server construct & setup event called by
     * the mod after the native setup during construction.
     *
     * <p>Called to construct and setup MC/Forge dependent
     * mod systems used by both the client and server.
     *
     * <p>Call order: directly after the native setup.
     *
     * @param event forge provided event.
     */
    void onServerSetup(final FMLCommonSetupEvent event);

    /**
     * The mods create and setup event for registry objects
     * called by the mod during construction.
     *
     * <p>Called to setup and register any/all mod objects
     * that need to be registered in the Forge Registry.
     *
     * <p>Call order: last, after native and client/server
     * setup.
     *
     * @param eventBus forge FML event bus.
     */
    void onRegistrySetup(final IEventBus eventBus);

    /**
     * The mods client construct & setup event called by
     * the mod after the native setup during construction.
     *
     * <p>Called to construct and setup MC/Forge dependent
     * mod systems used <b>only by the client</b>.
     *
     * <p>Call order: directly after the native & server setup.
     *
     * @param event forge provided event.
     */
    default void onClientSetup(final FMLClientSetupEvent event){}

    /**
     * InterModCommunication message send event.
     *
     * @param event forge provided event.
     */
    default void enqueueIMC(final InterModEnqueueEvent event){}

    /**
     * InterModCommunication process event.
     *
     * @param event forge provided event.
     */
    default void processIMC(final InterModProcessEvent event){}

    /**
     * Server side registration event.
     *
     * @param event forge provided event.
     */
    default void onServerStarting(FMLServerStartingEvent event){}

    /**
     * Called when the game is stopping.
     *
     * @param event Forge event.
     */
    default void onServerStopped(FMLServerStoppedEvent event){}

    /**
     * Called whenever an entity joins a world.
     *
     * @param event forge event.
     */
    default void onEntityJoinWorld(EntityJoinWorldEvent event){}
}
