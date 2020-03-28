package com.ki11erwolf.shoppery.util;

import net.minecraft.entity.player.PlayerEntity;
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
     */
    private PlayerUtil() {}

    /**
     * Gets a specific player given their UUID from the
     * current Minecraft server.
     *
     * @param playerUUID unique player ID.
     * @return the player. Returns null if
     * the player cannot be found or the UUID is null.
     */
    public static PlayerEntity getPlayerFromUUID(UUID playerUUID){
        if(playerUUID == null)
            return null;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getPlayerList().getPlayerByUUID(playerUUID);
    }

}
