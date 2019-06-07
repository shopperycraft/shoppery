package com.ki11erwolf.shoppery.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

/**
 * A set of utilities for handing
 * actions related to players.
 */
public final class PlayerUtil {

    /**
     * Private constructor.
     *
     * @throws IllegalAccessException Always thrown - no creating instances of this class!
     */
    private PlayerUtil() throws IllegalAccessException {
        throw new IllegalAccessException("No creating instances of this class! Bad!");
    }

    /**
     * Gets a specific player given their UUID from the
     * integrated server.
     *
     * @param playerUUID unique player ID.
     * @return the player. Returns null if
     * the player cannot be found or the UUID is null.
     */
    public static EntityPlayer getPlayerFromUUID(UUID playerUUID){
        if(playerUUID == null)
            return null;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getPlayerList().getPlayerByUUID(playerUUID);
    }

}
