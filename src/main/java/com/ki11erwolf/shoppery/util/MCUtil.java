package com.ki11erwolf.shoppery.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.UUID;

/**
 * A set of common utilities that aid in working with
 * Minecraft in general.
 */
public final class MCUtil {

    /**
     * Private constructor.
     */
    private MCUtil() {}

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

    /**
     * Attempts to obtain the name given to the specific
     * world instance, by the player, at the time of
     * its creation.
     *
     * The name obtained is unique, persistent for the lifetime
     * of the world, and can be used to identify the world among
     * others. Can also be used to save data related to the
     * world using only the name.
     *
     * <p/>In order for the name to be retrieved successfully,
     * the world object provided must be able to be cast to a
     * {@link ServerWorld}, and that world must give a {@link
     * IServerWorldInfo} as well.
     *
     * @param world the world to try and get the name of.
     * @throws IllegalStateException if the world object cannot
     * be used to get a name.
     * @throws IllegalArgumentException if the IWorld cannot be cast to World.
     * @return the name given to the world at creation time,
     * if it could be obtained.
     */
    public static String getWorldName(IWorld world){
        Objects.requireNonNull(world);

        if(!(world instanceof World))
            throw new IllegalArgumentException("Cannot get world name! IWorld object must be child of World class.");

        return getWorldName((World)world);
    }

    /**
     * Attempts to obtain the name given to the specific
     * world instance, by the player, at the time of
     * its creation.
     *
     * The name obtained is unique, persistent for the lifetime
     * of the world, and can be used to identify the world among
     * others. Can also be used to save data related to the
     * world using only the name.
     *
     * <p/>In order for the name to be retrieved successfully,
     * the world object provided must be able to be cast to a
     * {@link ServerWorld}, and that world must give a {@link
     * IServerWorldInfo} as well.
     *
     * @param world the world to try and get the name of.
     * @throws IllegalStateException if the world object cannot
     * be used to get a name.
     * @return the name given to the world at creation time,
     * if it could be obtained.
     */
    public static String getWorldName(World world){
        Objects.requireNonNull(world);  // Null check

        if(world.getServer() == null)   // World Server get & check
            throw new IllegalStateException("Cannot get world name! World object has no server. Client side maybe?");
        ServerWorld serverWorld = world.getServer().getWorld(world.func_234923_W_());

        if(serverWorld == null)         // World Info get & check
            throw new IllegalStateException("Cannot get world name! World object has no server. Client side maybe?");
        IWorldInfo genericWorldInfo = serverWorld.getWorldInfo();

        if(!(genericWorldInfo instanceof IServerWorldInfo)) // Server side World info get & check
            throw new IllegalStateException("Cannot get world name! World gave invalid IWorldInfo.");
        IServerWorldInfo serverWorldInfo = (IServerWorldInfo)genericWorldInfo;

        return serverWorldInfo.getWorldName();
    }
}
