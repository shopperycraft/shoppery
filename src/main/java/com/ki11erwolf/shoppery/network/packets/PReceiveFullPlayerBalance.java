package com.ki11erwolf.shoppery.network.packets;

import com.ki11erwolf.shoppery.bank.Wallet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used to send clients (EntityPlayerMP) their full balance
 * {@link Wallet#getFullBalance()}}.
 *
 * This class saves the last received full balance so it can
 * be retrieved easily, however, it is recommended a
 * {@link PRequestFullPlayerBalance} is sent to keep
 * the balance in sync before retrieving the balance.
 */
public class PReceiveFullPlayerBalance extends Packet<PReceiveFullPlayerBalance> {

    /**
     * The full balance given from the last
     * received PReceiveFullPlayerBalance packet.
     * This is specific to clients.
     */
    private static String lastReceivedBalance = null;

    /**
     * The players full balance.
     */
    private final String fullBalance;

    /**
     * @param fullBalance The players full balance.
     */
    PReceiveFullPlayerBalance(String fullBalance){
        this.fullBalance = fullBalance;
    }

    /**
     * Writes the given packets data to the
     * given PacketBuffer.
     *
     * @param msg given packet.
     * @param buf given buffer.
     */
    private static void encode(PReceiveFullPlayerBalance msg, PacketBuffer buf){
        writeString(msg.fullBalance, buf);
    }

    /**
     * Creates and returns a new packet containing the
     * data in the given buffer.
     *
     * @param buf the given buffer.
     * @return the created packet.
     */
    private static PReceiveFullPlayerBalance decode(PacketBuffer buf){
        return new PReceiveFullPlayerBalance(readString(buf));
    }

    /**
     * Caches the given player balance from the
     * received packet.
     *
     * @param message the received packet.
     * @param ctx the sender.
     */
    private static void handle(final PReceiveFullPlayerBalance message, Supplier<NetworkEvent.Context> ctx){
        handle(ctx, () -> lastReceivedBalance = message.fullBalance);
    }

    /**
     * @return The full balance given from the last
     * received PReceiveFormattedPlayerBalance packet.
     * This is specific to clients.
     */
    public static String getLastKnownBalance(){
        return lastReceivedBalance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PReceiveFullPlayerBalance, PacketBuffer> getEncoder() {
        return PReceiveFullPlayerBalance::encode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PReceiveFullPlayerBalance> getDecoder() {
        return PReceiveFullPlayerBalance::decode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PReceiveFullPlayerBalance, Supplier<NetworkEvent.Context>> getHandler() {
        return PReceiveFullPlayerBalance::handle;
    }
}
