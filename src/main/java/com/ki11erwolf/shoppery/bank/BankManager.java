package com.ki11erwolf.shoppery.bank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.util.MCUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to retrieve {@link Bank} & {@link Wallet}
 * objects as well as handle the saving & loading of them.
 *
 * <p/>Handles storing and retrieving {@link Bank}s
 * from file as well as provides an API for
 * obtaining instances to the various Banks.
 *
 * <p/>A Bank is linked to a {@link net.minecraft.world.World}
 * object and only stores data related to that world. This
 * means multiple worlds need multiple banks (such as in
 * a multiplayer server or simply different world saves in
 * singleplayer). Any bank must be obtained through
 * this class with a World object.
 *
 * <p/>This class is self managed. It will only load in banks
 * when called to do so with lazy loading ({@link #getBank(World)})
 * and it uses hooks to save the loaded banks in sync with
 * Minecraft world saving. This means no external
 * input is required for this class to run. You CAN still choose
 * to save all banks or a single bank, although it is not
 * unnecessarily required.
 */
public enum BankManager {

    //The thought of completely removing all dependencies
    //on MC, Forge, & Mojang code. Then making wrapper objects
    //to depend on instead seemed promising for a moment...

    /**
     * Singleton instance of this class.
     */
    INSTANCE;

    /**
     * The file name of every bank, to be formatted
     * with the banks name.
     */
    private static final String FILE_NAME = "WorldBank-%s.json";

    /*
     * Registers the bank saver shutdown
     * hook & forge hooks when the class
     * is first used.
     */
    static {
        registerShutdownSaver();
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = ShopperyMod.getNewLogger();

    /**
     * The directory where shoppery will save all its
     * Bank data.
     */
    public final File bankDirectory;

    /**
     * Map that links a world name
     * to its bank.
     */
    private final Map<String, Bank> worldToBank;

    /**
     * Initializes fields.
     */
    BankManager(){
        this.bankDirectory = new File(System.getProperty("user.dir") + "/shoppery/banks/");
        this.worldToBank = new HashMap<>();
    }

    //************
    // Public API
    //************

    /**
     * Gets the bank linked to the given
     * world object.
     *
     * This method will either return the
     * cached bank object if it's been
     * read from file or created already by calling
     * this method, or, it will
     * try read the bank from file and load
     * it. If no cached version can be found and
     * no bank can be found on file, a
     * new empty bank will be created, cached
     * and returned.
     *
     * @param world the world object.
     * @return the bank object linked
     * to the given world.
     */
    public Bank getBank(World world){
        String worldName = MCUtil.getWorldName(world);
        Bank givenBank = worldToBank.get(worldName);

        if(givenBank == null){
            givenBank = readBank(world);

            if(givenBank == null){
                givenBank = new Bank(world);
            }
        }

        worldToBank.put(worldName, givenBank);
        return givenBank;
    }


    /**
     * Static shortcut for {@link #getBank(World)}
     *
     * Gets the bank linked to the given
     * world object.
     *
     * This method will either return the
     * cached bank object if it's been
     * read from file or created already by calling
     * this method, or, it will
     * try read the bank from file and load
     * it. If no cached version can be found and
     * no bank can be found on file, a
     * new empty bank will be created, cached
     * and returned.
     *
     * @param world the world object.
     * @return the bank object linked
     * to the given world.
     */
    //Underscore used to differentiate
    //between static & member method.
    public static Bank _getBank(World world){
        return INSTANCE.getBank(world);
    }

    /**
     * Shortcut method to retrieve the given players
     * wallet from the given world. Equivalent to:
     * {@code _getBank(bank).getWallet(player);}.
     *
     * @param world the players wallet in the given world.
     * @param playerUUID the UUID of the player who's wallet
     *                   we're retrieving.
     * @return the players wallet for the given world.
     */
    public static Wallet _getWallet(World world, UUID playerUUID){
        return _getBank(world).getWallet(playerUUID);
    }

    /**
     * Shortcut method to retrieve the given players
     * wallet from the given world. Equivalent to:
     * {@code _getBank(bank).getWallet(player);}.
     *
     * @param world the players wallet in the given world.
     * @param player the player who's wallet we're retrieving.
     * @return the players wallet for the given world.
     */
    public static Wallet _getWallet(World world, PlayerEntity player){
        return _getBank(world).getWallet(player);
    }

    /**
     * Shortcut method to retrieve the given players
     * wallet from the given world. Equivalent to:
     * {@code _getBank(bank).getWallet(player);}.
     *
     * @param world the players wallet in the given world.
     * @param playerProfile the GameProfile of the player
     *                      who's wallet we're retrieving.
     * @return the players wallet for the given world.
     */
    public static Wallet _getWallet(World world, GameProfile playerProfile){
        return _getBank(world).getWallet(playerProfile);
    }

    /**
     * Shortcut method to retrieve the given players
     * wallet from the world they are currently in.
     * Equivalent to: {@code _getBank(bank).getWallet(player);}.
     *
     * @param playerUUID the UUID of the player
     *                      who's wallet we're retrieving.
     * @return the players wallet for the world they are currently in.
     */
    public static Wallet _getWallet(UUID playerUUID){
        PlayerEntity player = MCUtil.getPlayerFromUUID(playerUUID);
        return _getBank(player.getEntityWorld()).getWallet(player);
    }

    /**
     * Shortcut method to retrieve the given players
     * wallet from the world they are currently in.
     * Equivalent to: {@code _getBank(bank).getWallet(player);}.
     *
     * @param player the the player who's wallet we're retrieving.
     * @return the players wallet for the world they are currently in.
     */
    public static Wallet _getWallet(PlayerEntity player){
        return _getBank(player.getEntityWorld()).getWallet(player);
    }

    /**
     * Shortcut method to retrieve the given players
     * wallet from the world they are currently in.
     * Equivalent to: {@code _getBank(bank).getWallet(player);}.
     *
     * @param playerProfile the GameProfile of the player
     *                      who's wallet we're retrieving.
     * @return the players wallet for the world they are currently in.
     */
    public static Wallet _getWallet(GameProfile playerProfile){
        PlayerEntity player = MCUtil.getPlayerFromUUID(playerProfile.getId());
        return _getBank(player.getEntityWorld()).getWallet(player);
    }

    /**
     * Saves the given bank to file. If the
     * given bank is {@code null}, this method
     * will save every bank in the cache to file.
     *
     * @param b the bank to save or null to
     *          save all banks.
     */
    public void save(Bank b){
        if(b != null){
            //Just make sure it exists within the map.
            if(worldToBank.containsKey(b.getWorldName())){
                worldToBank.put(b.getWorldName(), b);
            }

            saveBank(b);
            return;
        }

        if(worldToBank.isEmpty()){
            LOGGER.info("No cached banks. Skipping save...");
            return;
        }

        worldToBank.values().forEach(this::saveBank);
    }

    /**
     * Saves every bank in the BankManagers cache.
     * Shortcut for {@code save(null);}.
     */
    public void save(){
        save(null);
    }

    //****************
    // INTERNAL LOGIC
    //****************

    /**
     * Gson object used to format (pretty print) json text.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Tries to retrieve the given worlds bank from file
     * if it exists.
     *
     * @param world the given world object.
     * @return a reconstructed Bank object from file, or
     * {@code null} if no save could be found.
     */
    private Bank readBank(World world){
        String worldName = MCUtil.getWorldName(world);
        LOGGER.info("Reading bank save file: " + worldName);

        File saveFile = getFileFromName(worldName);

        //Ensure existence
        if(!saveFile.exists()){
            LOGGER.info("Bank save file not found: " + saveFile);
            return null;
        }

        //Read content
        StringBuilder saveFileContent = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(saveFile));

            String line;
            while((line = reader.readLine()) != null){
                saveFileContent.append(line);
            }

            reader.close();
        } catch (IOException e) {
            LOGGER.error("Failed to read bank save file: " + worldName, e);
            return null;
        }

        //Load into memory
        try{
            JsonObject jBank = GSON.fromJson(saveFileContent.toString(), JsonObject.class);
            return Bank.createBankFromJsonObject(jBank, world);
        } catch (JsonSyntaxException e){
            LOGGER.error(
                    "Bank save file: "
                            + worldName +
                            " is not in json format (could be corrupted)!",
                    e
            );
        }

        return null;
    }

    /**
     * Writes the given bank object to a save
     * file with the same name as the world
     * linked to the bank.
     *
     * @param bank the given bank object.
     * @return {@code true} if the file was successfully
     * written to, {@code false} otherwise.
     */
    private boolean saveBank(Bank bank){
        //Print (logger is unreliable) out when we save the bank
        //and from where.
        System.out.printf(
            "Attempting to save shoppery bank %s from %s%n",
            bank.getWorldName(), StackLocatorUtil.getCallerClass(6)
        );

        JsonObject jBank = bank.getBankAsJsonObject();
        File saveFile = getFileFromName(bank.getWorldName());

        try {
            String bankJson = GSON.toJson(jBank);

            FileWriter writer = new FileWriter(saveFile);
            writer.write(bankJson);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Failed to save bank: " + saveFile, e);
            return false;
        }

        return true;
    }

    /**
     * Creates a save file path for the given banks
     * world name.
     *
     * @param name the given bank name.
     * @return a file to read/save the content of the bank.
     */
    private static File getFileFromName(String name){
        if(name == null)
            return null;

        return new File(
                INSTANCE.bankDirectory
                +"/" + String.format(FILE_NAME, name)
        );
    }

    //*******
    // Hooks
    //*******

    /**
     * Registers a ShutdownHook ({@link Runtime#addShutdownHook(Thread)}
     * that will save all banks when the JVM exists (Not guaranteed).
     */
    private static void registerShutdownSaver(){
        Runtime.getRuntime().addShutdownHook(new Thread(BankManager.INSTANCE::save));
    }

    /**
     * Called whenever a Minecraft world is saved.
     *
     * This allows the BankManager to save the bank
     * for a world when Minecraft saves the world.
     * This in turn should keep the world saves
     * and bank saves in sync.
     *
     * @param worldSaveEvent forge event.
     */
    @SubscribeEvent
    void onWorldSave(WorldEvent.Save worldSaveEvent){
        if(!(worldSaveEvent.getWorld() instanceof World))
            throw new IllegalStateException("Failed to convert IWorld to World.");

        World world = (World)worldSaveEvent.getWorld();
        if(worldToBank.containsKey(MCUtil.getWorldName(world))){
            save(getBank(world));
        }
    }

    /**
     * Called whenever a Minecraft world is unloaded
     * (closed).
     *
     * This allows us to remove in references in the
     * cache ({@link #worldToBank}) that we are unlikely
     * to use anytime soon, which will save memory
     * and time spent saving unnecessarily.
     *
     * This method WILL NOT save the bank before
     * removing it from the cache. It's assumed the
     * world is saved before it's unloaded.
     *
     * @param worldUnloadEvent forge event.
     */
    @SubscribeEvent
    void onWorldUnload(WorldEvent.Unload worldUnloadEvent){
        if(worldUnloadEvent.getWorld().isRemote())
            return;

        LOGGER.info("World with bank: " + MCUtil.getWorldName(worldUnloadEvent.getWorld()) +
                " was unloaded! Removing bank from cache!"
        );
        worldToBank.remove(MCUtil.getWorldName(worldUnloadEvent.getWorld()));
    }
}
