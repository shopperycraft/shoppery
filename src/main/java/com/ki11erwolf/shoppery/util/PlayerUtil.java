package com.ki11erwolf.shoppery.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

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
     * Gets a specific player given their UUID.
     *
     * @param playerUUID unique player ID.
     * @return the player. Returns null if
     * the player cannot be found or the UUID is null.
     */
    public static EntityPlayer getPlayerFromUUID(UUID playerUUID){
        if(playerUUID == null)
            return null;

        if(Minecraft.getInstance().getIntegratedServer() == null)
            return null;

        return Minecraft.getInstance().getIntegratedServer().getPlayerList().getPlayerByUUID(playerUUID);
    }
}
