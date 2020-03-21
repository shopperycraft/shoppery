package com.ki11erwolf.shoppery.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Receives the balance of the clients EntityPlayer sent by
 * the server and caches it.
 */
public class PlayerCentsRecPacket extends Packet<PlayerCentsRecPacket> {

    /**
     * The cents balance contained in the last
     * packet sent.
     */
    private static byte lastReceivedBalance = 0;

    /**
     * The cents balance of the player
     * contained in the packet.
     */
    private final byte cents;

    /**
     * Constructor.
     *
     * @param cents the cents balance of the player.
     */
    PlayerCentsRecPacket(byte cents){
        this.cents = cents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PlayerCentsRecPacket, PacketBuffer> getEncoder() {
        return (packet, buffer) -> buffer.writeByte(packet.cents);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PlayerCentsRecPacket> getDecoder() {
        return (buffer -> new PlayerCentsRecPacket(buffer.readByte()));
    }

    /**
     * {@inheritDoc}
     *
     * Caches the cents balance in the received packet.
     */
    @Override
    BiConsumer<PlayerCentsRecPacket, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> lastReceivedBalance = packet.cents);
    }

    /**
     * @return the cents balance of the player
     * given by the last received packet.
     */
    public static byte getLastReceivedBalance(){
        return lastReceivedBalance;
    }
}