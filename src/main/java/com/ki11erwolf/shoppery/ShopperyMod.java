package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.block.ShopperyBlocks;
import com.ki11erwolf.shoppery.item.ShopperyItems;
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
 * The Shoppery "Main" mod class.
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
     * shopperycraft.
     */
    public static final String VERSION = "1.0.0";

    /**
     * The directory where shoppery will save all its data.
     */
    public static final File SHOPPERY_DIRECTORY = new File(System.getProperty("user.dir") + "/shoppery/");

    /**
     * The proxy class (server or client) for this instance.
     */
    @SuppressWarnings({"FieldMayBeFinal", "deprecation"})
    private static Proxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    /**
     * Default Constructor.
     *
     * Sets up mod event listeners & callbacks.
     */
    public ShopperyMod() {
        ItemPrices.loadPriceRegistry();

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setup);
        modBus.addListener(this::enqueueIMC);
        modBus.addListener(this::processIMC);
        modBus.addListener(this::clientOnlySetup);

        modBus.register(ShopperyBlocks.BLOCKS);
        modBus.register(ShopperyItems.ITEMS);

        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * First mod registration event. Called to
     * initialize and register the shoppery mod.
     *
     * @param event forge provided event.
     */
    private void setup(final FMLCommonSetupEvent event){
        LOGGER.info(String.format("Starting ShopperyCraft %s setup...", VERSION));
        proxy.setup(event);
    }

    /**
     * Client side mod registration event.
     *
     * @param event forge provided event.
     */
    private void clientOnlySetup(final FMLClientSetupEvent event) {
        if(proxy instanceof ClientProxy)
            proxy.clientOnlySetup(event);
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
        if(!(proxy instanceof ClientProxy))
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
    public static Logger getNewLogger(){
        return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * @return the singleton {@link BankManager} instance.
     */
    public static BankManager getBankManager(){
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
    public static ItemPrices getItemPrices(){
        return ItemPrices.INSTANCE;
    }
}
