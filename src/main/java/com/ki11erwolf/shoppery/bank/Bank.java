package com.ki11erwolf.shoppery.bank;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ki11erwolf.shoppery.util.PlayerUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * The Bank is a {@link Wallet} storage, and
 * access and retrieval system. It's responsible
 * for storing Wallets as well as providing
 * a solid API for accessing Wallets.
 *
 * A Bank stores only the Wallets for a single world
 * (including dimensions e.g. nether & end). This means
 * multiple banks can exist within a multiplayer server
 * with multiple worlds, and as such, a bank needs to
 * be obtained from the {@link BankManager} using a
 * {@link net.minecraft.world.World}.
 *
 * @see BankManager for obtaining Bank & Wallet objects.
 */
@SuppressWarnings("WeakerAccess")
public class Bank {

    /**
     * Map for accessing player wallets from their UUID's.
     */
    private final Map<UUID, Wallet> walletMap = new HashMap<>();

    /**
     * The world this bank is linked to.
     */
    private final World world;

    /**
     * Creates a new bank linked to a world.
     *
     * @param world the world this bank holds
     *              Wallets for.
     */
    Bank(World world){
        this.world = Objects.requireNonNull(world);
    }

    //************
    // PUBLIC API
    //************

    /**
     * Gets a players wallet from the Bank.
     *
     * This method will return the
     * wallet from the bank if it exists,
     * otherwise it will create a new empty
     * wallet and add that wallet to the bank.
     *
     * @param playerUUID the players UUID.
     * @return the players wallet or a new wallet
     * for the player if the player does not have
     * a wallet.
     */
    public Wallet getWallet(UUID playerUUID){
        Wallet givenWallet = walletMap.get(playerUUID);

        //If player has no wallet.
        if(givenWallet == null){
            EntityPlayer player = PlayerUtil.getPlayerFromUUID(playerUUID);

            //If not player can be found.
            if(player == null)
                return null;

            givenWallet = new Wallet(player, 0, (byte)0);
            walletMap.put(playerUUID, givenWallet);
        }

        return givenWallet;
    }

    /**
     * Gets a players wallet from the Bank.
     *
     * This method will return the
     * wallet from the bank if it exists,
     * otherwise it will create a new empty
     * wallet and add that wallet to the bank.
     *
     * @param player the players entity object.
     * @return the players wallet or a new wallet
     * for the player if the player does not have
     * a wallet.
     */
    public Wallet getWallet(EntityPlayer player){
        return getWallet(player.getUniqueID());
    }

    /**
     * Gets a players wallet from the Bank.
     *
     * This method will return the
     * wallet from the bank if it exists,
     * otherwise it will create a new empty
     * wallet and add that wallet to the bank.
     *
     * @param profile the players game profile.
     * @return the players wallet or a new wallet
     * for the player if the player does not have
     * a wallet.
     */
    public Wallet getWallet(GameProfile profile){
        return getWallet(profile.getId());
    }

    /**
     * @return the world this bank is linked to.
     */
    public World getWorld(){return world;}

    /**
     * @return the name (given at world creation)
     * of the world this bank links to.
     */
    public String getWorldName(){
        return world.getWorldInfo().getWorldName();
    }

    //****************
    // INTERNAL LOGIC
    //****************

    /**
     * The key used to store the world name.
     */
    private static final String WORLD_NAME_KEY = "WorldName";

    /*
        Json structure used to store
        a bank (all the wallets in the
        bank to be specific):
        {
            "WorldName": "<World Name>,
            "<Wallets Player UUID>": "<Json Wallet Object>"
        }
     */

    /**
     * Creates a JsonObject containing the bank
     * data and all of its wallets.
     *
     * @return a JsonObject containing all bank
     * data and wallets.
     */
    JsonObject getBankAsJsonObject(){
        JsonObject jBank = new JsonObject();

        jBank.add(WORLD_NAME_KEY, new JsonPrimitive(world.getWorldInfo().getWorldName()));
        walletMap.forEach((uuid, wallet) -> jBank.add(uuid.toString(), wallet.getWalletAsJsonObject()));

        return jBank;
    }

    /**
     * Creates a new bank instances containing
     * all its wallets from a JsonObject containing
     * bank object data (wallets).
     *
     * @param jBank the JsonObject holding bank data
     *              and wallets.
     * @param world the world this bank belongs to.
     * @return the newly created bank instance with
     * all its wallets or null if the bank could not
     * be created from the JsonObject.
     */
    static Bank createBankFromJsonObject(JsonObject jBank, World world){
        Bank bank = new Bank(world);
        jBank.remove(WORLD_NAME_KEY);//We don't need to parse this.

        for(Map.Entry<String, JsonElement> entry : jBank.entrySet()){
            UUID uuid = UUID.fromString(entry.getKey());
            JsonObject walletObject = entry.getValue().getAsJsonObject();

            Wallet wallet = Wallet.createWalletFromJsonObject(walletObject, uuid);

            if(wallet != null)//Ignore invalid wallets. Already logged.
                bank.walletMap.put(uuid, Wallet.createWalletFromJsonObject(walletObject, uuid));
        }

        return bank;
    }
}
