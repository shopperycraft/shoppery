package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.bank.BankManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.io.File;

/**
 * The Shoppery "Main" mod class.
 */
@Mod(ShopperyMod.MODID)
@SuppressWarnings("unused")//Methods/Objects found by reflection
public class ShopperyMod {

    /**
     * Logging Object
     */
    private static final Logger LOGGER = getNewLogger();

    /**
     * The forge modid for shopperycraft.
     */
    public static final String MODID = "shoppery";

    /**
     * The version number for this release of
     * shopperycraft.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String VERSION = "1.0.0";

    /**
     * The directory where shoppery will save all its data.
     */
    public static final File SHOPPERY_DIRECTORY = new File(System.getProperty("user.dir") + "/shoppery/");

    /**
     * The directory where shoppery will save all its
     * {@link com.ki11erwolf.shoppery.bank.Bank} data.
     */
    public static final File SHOPPERY_BANK_DIRECTORY
            = new File(System.getProperty("user.dir") + "/shoppery/banks/");

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
        LOGGER.info(String.format("Starting ShopperyCraft %s setup...", VERSION));
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
     * Called when the game is stopping.
     *
     * @param event Forge event.
     */
    @SubscribeEvent
    public void onServerStopped(FMLServerStoppedEvent event){

    }

    /**
     * @return a new apache log4j logging instance.
     * Equivalent to {@link LogManager#getLogger()}.
     */
    public static Logger getNewLogger(){
        return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * @return the singleton {@link BankManager} instance.
     */
    public static BankManager getBankManager(){
        return BankManager.INSTANCE;
    }
}
