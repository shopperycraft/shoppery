package com.ki11erwolf.shoppery.packets;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.ShopperySoundEvents;
import com.ki11erwolf.shoppery.util.ClientPlayerFetcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Event, which is sent by servers, to the client side,
 * requesting the client play a specific sound event to
 * the player. Allows playing sound events from server
 * side logic.
 */
public class PlaySoundOnClientPacket extends Packet<PlaySoundOnClientPacket> {

    /**
     * The mod logger instance for this class.
     */
    private static final Logger LOG = ShopperyMod.getNewLogger();

    /**
     * The player who's requesting the money.
     */
    private final String playerUUID;

    /**
     * The registry name identifying the sound event.
     */
    private final String soundID;

    /**
     * The volume to play the sound event at.
     */
    private final float volume;

    /**
     * The pitch to play the sound event at.
     */
    private final float pitch;

    /**
     * Creates a new sound event requesting the given sound be played.
     *
     * @param player the player to send the request to.
     * @param soundEvent the sound event to play.
     * @param volume the volume of the sound event.
     * @param pitch the pitch of the sound event.
     */
    public PlaySoundOnClientPacket(PlayerEntity player, SoundEvent soundEvent, float volume, float pitch){
        this.playerUUID = player.getUniqueID().toString();
        this.soundID = soundEvent.getName().toString();
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * Creates a new sound event requesting the given sound be played.
     *
     * @param playerUUID the UUUID of the player to send the request to.
     * @param soundID the registry name of the sound event to play.
     * @param volume the volume of the sound event.
     * @param pitch the pitch of the sound event.
     */
    PlaySoundOnClientPacket(String playerUUID, String soundID, float volume, float pitch){
        this.playerUUID = playerUUID;
        this.soundID = soundID;
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PlaySoundOnClientPacket, PacketBuffer> getEncoder() {
        return (packet, buffer) -> {
            writeString(packet.playerUUID, buffer);
            writeString(packet.soundID, buffer);
            buffer.writeFloat(packet.volume);
            buffer.writeFloat(packet.pitch);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PlaySoundOnClientPacket> getDecoder() {
        return (buffer) -> new PlaySoundOnClientPacket(
                readString(buffer), readString(buffer), buffer.readFloat(), buffer.readFloat()
        );
    }

    /**
     * {@inheritDoc}
     *
     * Handles receiving the event and playing the sound event
     * that came with it to the client side player.
     */
    @Override
    BiConsumer<PlaySoundOnClientPacket, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            //Make sure we're on client before
            if(!FMLEnvironment.dist.isClient())
                return;

            PlayerEntity player = ClientPlayerFetcher.getClientPlayer();
            if(!player.getUniqueID().toString().equals(packet.playerUUID)) {
                LOG.warn(String.format("Sound event received with mismatching player UUID's! To: '%s'. From: %s",
                        playerUUID, player.getUniqueID().toString()
                ));
            }

            player.playSound(ShopperySoundEvents.getSoundEvent(packet.soundID), packet.volume, packet.pitch);
        });
    }
}
