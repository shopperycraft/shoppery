package com.ki11erwolf.shoppery.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Retrieves the players {@code long balance} from the sending
 * server and caches it.
 */
public class PReceivePlayerBalance extends Packet<PReceivePlayerBalance> {

    /**
     * The last received player long balance.
     */
    private static long lastReceivedBalance = 0;

    /**
     * The long balance of the player
     * when sending/receiving the
     * packet.
     */
    private final long balance;

    /**
     * Constructor.
     *
     * @param balance The long balance of the player
     *                when sending/receiving the
     *                packet.
     */
    PReceivePlayerBalance(long balance){
        this.balance = balance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PReceivePlayerBalance, PacketBuffer> getEncoder() {
        return (packet, buffer) -> buffer.writeLong(packet.balance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PReceivePlayerBalance> getDecoder() {
        return (buffer -> new PReceivePlayerBalance(buffer.readLong()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PReceivePlayerBalance, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> lastReceivedBalance = packet.balance);
    }

    /**
     * @return the last long balance of the player
     * send by the server.
     */
    public static long getLastReceivedBalance(){
        return lastReceivedBalance;
    }
}
