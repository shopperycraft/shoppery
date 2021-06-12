package com.ki11erwolf.shoppery;

import com.ki11erwolf.shoppery.config.ModConfig;
import com.ki11erwolf.shoppery.config.categories.SoundConfig;
import com.ki11erwolf.shoppery.packets.Packet;
import com.ki11erwolf.shoppery.packets.PlaySoundOnClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

/**
 * List of sound effects added & used by the Shoppery
 * mod.
 */
public final class ShopperySoundEvents {

    /**
     * A public global reference to the configuration settings related to
     * sound & sound events.
     */
    public static final SoundConfig SOUND_CONFIG = ModConfig.GENERAL_CONFIG.getCategory(SoundConfig.class);

    /**
     * An internal map of Shoppery sound events mapped to their registry names.
     * Used to enable looking up and obtaining sound events without an object
     * reference.
     */
    private static final Map<String, SoundEvent> SOUND_EVENT_MAP = new HashMap<>();

    /**
     * The cash withdraw sound effect. Used when taking money from the
     * wallet/money gui.
     */
    public static final SoundEvent WITHDRAW = newSoundEvent("withdraw");

    /**
     * The cash deposit sound effect. Used when adding money to the
     * wallet/money gui.
     */
    public static final SoundEvent DEPOSIT = newSoundEvent("deposit");

    /**
     * The (primary) cash register transaction sound effect. Used when a shop has sold/purchased
     * an item.
     */
    public static final SoundEvent TRANSACT = newSoundEvent("transact");

    /**
     * The (alternative) cash register transaction sound effect. Used when a shop has
     * sold/purchased an item.
     */
    public static final SoundEvent TRANSACT_ALT = newSoundEvent("transact_alt");

    /**
     * The shop cash register sound effect. Used when a shop has been opened or
     * activated.
     */
    public static final SoundEvent CASH_REGISTER = newSoundEvent("cash_register");

    /**
     * The transaction declined (failed/not allowed) sound effect. Used when a
     * player cannot buy/sell an item.
     */
    public static final SoundEvent DECLINE = newSoundEvent("decline");

    /**
     * Creates a new sound event for use in the mod under the given name. The
     * created sound event is also saved in the {@link #SOUND_EVENT_MAP} map.
     *
     * @param registryName the simple registry name (path) of the sound event.
     * @return the created sound event.
     */
    private static SoundEvent newSoundEvent(String registryName){
        SoundEvent event = new SoundEvent(new ResourceLocation(ShopperyMod.MODID, registryName));
        SOUND_EVENT_MAP.put(event.getName().toString(), event);
        return event;
    }

    /**
     * Searches for a registered sound event that was registered
     * under the given registry name.
     *
     * @param registryName the full registry name of the sound event.
     * @return the sound event registered under the given name or
     * {@code null} if no sound event could be found.
     */
    public static SoundEvent getSoundEvent(String registryName){
        return SOUND_EVENT_MAP.get(registryName);
    }

    /**
     * Sends a packet requesting a client play a specific sound event
     * to the player. Sent by servers to enable playing sound events
     * from server side logic.
     *
     * @param player the player to send the event to.
     * @param soundEvent the sound event to play.
     * @param volume the volume to play the event at.
     * @param pitch the pitch to play the event at.
     */
    public static void sendSoundEvent(PlayerEntity player, SoundEvent soundEvent, float volume, float pitch){
        if(!(player instanceof ServerPlayerEntity)){
            return;
        }

        Packet.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                new PlaySoundOnClientPacket(player, soundEvent, volume, pitch)
        );
    }

    /**
     * Private constructor.
     */
    private ShopperySoundEvents(){}

}
