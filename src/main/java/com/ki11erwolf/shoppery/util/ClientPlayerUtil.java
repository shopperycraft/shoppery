package com.ki11erwolf.shoppery.util;

import net.minecraft.entity.player.PlayerEntity;

/**
 * A wrapper class that allows us to reference (but not call)
 * the client instance player from server side code without
 * a crash.
 */
public class ClientPlayerUtil {

    /**
     * @return {@link net.minecraft.client.Minecraft#player}
     */
    public static PlayerEntity getClientPlayer(){
        return net.minecraft.client.Minecraft.getInstance().player;
    }

}
