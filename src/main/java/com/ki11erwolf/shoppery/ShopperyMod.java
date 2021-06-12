package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.price.ItemPrices;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
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
 * The Shoppery Mods main/entry class. Holds core Shoppery
 * systems, objects and variables, as well as handles
 * proxies, events, and complete mod setup.
 */
@Mod(ShopperyMod.MODID)
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
     * Shopperycraft.
     */
    public static final String VERSION = "1.0.0";

    /**
     * The Minecraft version number this release of
     * Shopperycraft is for.
     */
    public static final String MINECRAFT_VERSION = "1.16.3";

    /**
     * The directory where shoppery will save all its data.
     */
    public static final File SHOPPERY_DIRECTORY = new File(System.getProperty("user.dir") + "/shoppery/");

    /**
     * The proxy class (server or client) for this instance.
     */
    @SuppressWarnings({"FieldMayBeFinal", "deprecation"})
    private static Proxy proxy = DistExecutor.runForDist(() -> ProxyClient::new, () -> ProxyServer::new);

    /**
     * Default Constructor for main Shoppery Mod class and
     * non-static entry point.
     *
     * <p>Calls the various setup methods that ultimately
     * construct, setup, create, & make the mod. Setup
     * methods are called in a specific order.
     */
    public ShopperyMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        initSetup(eventBus);
    }

    /**
     * Handles calling the mods setup methods
     * and events in the correct orders.
     *
     * <p>Firstly, handles pre-mod native setup
     * before registering the server & client mod
     * setup events along with inter-mod events
     * and communication.
     *
     * <p>Secondly, passes control over to the
     * proxy(s) for various registry object setup
     * and registration with {@link
     * #setupRegistry(IEventBus)}
     *
     * <p>Finally, registers all other hooks and
     * events for Forge and FML.
     *
     * @param eventBus {@link FMLJavaModLoadingContext
     * #getModEventBus() FML Mod Loading event bus}
     */
    private void initSetup(IEventBus eventBus){
        LOGGER.info(String.format("%sBegin Shoppery-%s-%s setup!",
                "\n------------------\n", VERSION, MINECRAFT_VERSION
        ));

        //Native
        setupNative();

        //Client/Server
        eventBus.addListener(this::setupServer);
        eventBus.addListener(this::setupClient);

        //Mod Communications
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);

        //Registry
        setupRegistry(eventBus);

        //Other
        MinecraftForge.EVENT_BUS.register(this);
    }

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
    private void setupNative(){
        LOGGER.info("Starting ShopperyCraft native setup...");
        proxy.onNativeSetup();
    }


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
    private void setupServer(final FMLCommonSetupEvent event){
        LOGGER.info("Starting ShopperyCraft server setup...");
        proxy.onServerSetup(event);
    }

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
    private void setupClient(final FMLClientSetupEvent event) {
        if(proxy instanceof ProxyClient) {
            LOGGER.info("Starting ShopperyCraft client setup...");
            proxy.onClientSetup(event);
        }
    }

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
    private void setupRegistry(final IEventBus eventBus){
        LOGGER.info("Starting ShopperyCraft registry setup...");
        proxy.onRegistrySetup(eventBus);
    }

    /**
     * InterModCommunication message send event.
     *
     * @param event forge provided event.
     */
    private void enqueueIMC(final InterModEnqueueEvent event){
        proxy.enqueueIMC(event);
    }

    /**
     * InterModCommunication process event.
     *
     * @param event forge provided event.
     */
    private void processIMC(final InterModProcessEvent event){
        proxy.processIMC(event);
    }

    /**
     * Server side registration event.
     *
     * @param event forge provided event.
     */
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        proxy.onServerStarting(event);
    }

    /**
     * Called when the game is stopping.
     *
     * @param event Forge event.
     */
    @SubscribeEvent
    public void onServerStopped(FMLServerStoppedEvent event){
        if(!(proxy instanceof ProxyClient))
            proxy.onServerStopped(event);
    }

    /**
     * Called whenever an entity joins a world.
     *
     * @param joinWorldEvent forge event.
     */
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent joinWorldEvent){
        proxy.onEntityJoinWorld(joinWorldEvent);
    }

    // **************
    // Accessor Utils
    // **************

    /**
     * @return a new apache log4j logging instance.
     * Equivalent to {@link LogManager#getLogger()}.
     */
    public static Logger getNewLogger() {
        return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * @return the singleton {@link BankManager} instance.
     */
    public static BankManager getBankManager() {
        return BankManager.INSTANCE;
    }

    /**
     * Provides an alternative to accessing the
     * {@link com.ki11erwolf.shoppery.price.ItemPrices}
     * statically, by proving an object instance with
     * the same methods as instance methods.
     *
     * @return the singleton ItemPrices instance.
     */
    public static ItemPrices getItemPrices() {
        return ItemPrices.INSTANCE;
    }
}
