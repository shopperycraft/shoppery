package com.ki11erwolf.shoppery;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

/**
 * The Shoppery "Main" mod class.
 */
@Mod("shoppery")
@SuppressWarnings("unused")//Methods/Objects found by reflection
public class ShopperyMod {

    /**
     * Logging Object
     */
    private static final Logger LOGGER = getNewLogger();

    /**
     * Default Constructor.
     *
     * Sets up mod event listeners & callbacks.
     */
    public ShopperyMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * First mod registration event.
     *
     * @param event forge provided event.
     */
    private void setup(final FMLCommonSetupEvent event){

    }

    /**
     * Client side mod registration event.
     *
     * @param event forge provided event.
     */
    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    /**
     * InterModCommunication message send event.
     *
     * @param event forge provided event.
     */
    private void enqueueIMC(final InterModEnqueueEvent event){

    }

    /**
     * InterModCommunication process event.
     *
     * @param event forge provided event.
     */
    private void processIMC(final InterModProcessEvent event){

    }

    /**
     * Server side registration event.
     *
     * @param event forge provided event.
     */
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

    }

    /**
     * @return a new apache log4j logging instance.
     * Equivalent to {@link LogManager#getLogger()}.
     */
    @SuppressWarnings("WeakerAccess")
    public static Logger getNewLogger(){
        return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Mod object & content registration events.
     */
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        /**
         * Mod block registration event.
         *
         * @param blockRegistryEvent forge provided event.
         */
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {

        }
    }
}
