package com.ki11erwolf.shoppery.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Receives the balance of the clients EntityPlayer sent by
 * the server and caches it.
 */
public class PReceivePlayerCents extends Packet<PReceivePlayerCents> {

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
    PReceivePlayerCents(byte cents){
        this.cents = cents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PReceivePlayerCents, PacketBuffer> getEncoder() {
        return (packet, buffer) -> buffer.writeByte(packet.cents);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PReceivePlayerCents> getDecoder() {
        return (buffer -> new PReceivePlayerCents(buffer.readByte()));
    }

    /**
     * {@inheritDoc}
     *
     * Caches the cents balance in the received packet.
     */
    @Override
    BiConsumer<PReceivePlayerCents, Supplier<NetworkEvent.Context>> getHandler() {
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