package com.ki11erwolf.shoppery.network.packets;

import com.ki11erwolf.shoppery.bank.Wallet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used to send clients (EntityPlayerMP) their formatted balance
 * {@link Wallet#getShortenedBalance()}.
 *
 * This class saves the last received balance so it can
 * be retrieved easily, however, it is recommended a
 * {@link PRequestFormattedPlayerBalance} is sent to keep
 * the balance in sync before retrieving the balance.
 */
public class PReceiveFormattedPlayerBalance extends Packet<PReceiveFormattedPlayerBalance> {

    /**
     * The formatted balance given from the last
     * received PReceiveFormattedPlayerBalance packet.
     * This is specific to clients.
     */
    private static String lastReceivedBalance = null;

    /**
     * The players formatted balance.
     */
    private final String formattedBalance;

    /**
     * @param formattedBalance The players formatted balance.
     */
    PReceiveFormattedPlayerBalance(String formattedBalance){
        this.formattedBalance = formattedBalance;
    }

    /**
     * Writes the given packets data to the
     * given PacketBuffer.
     *
     * @param msg given packet.
     * @param buf given buffer.
     */
    private static void encode(PReceiveFormattedPlayerBalance msg, PacketBuffer buf){
        writeString(msg.formattedBalance, buf);
    }

    /**
     * Creates and returns a new packet containing the
     * data in the given buffer.
     *
     * @param buf the given buffer.
     * @return the created packet.
     */
    private static PReceiveFormattedPlayerBalance decode(PacketBuffer buf){
        return new PReceiveFormattedPlayerBalance(readString(buf));
    }

    /**
     * Caches the given player balance from the
     * received packet.
     *
     * @param message the received packet.
     * @param ctx the sender.
     */
    private static void handle(final PReceiveFormattedPlayerBalance message, Supplier<NetworkEvent.Context> ctx){
        handle(ctx, () -> lastReceivedBalance = message.formattedBalance);
    }

    /**
     * @return The formatted balance given from the last
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
    BiConsumer<PReceiveFormattedPlayerBalance, PacketBuffer> getEncoder() {
        return PReceiveFormattedPlayerBalance::encode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PReceiveFormattedPlayerBalance> getDecoder() {
        return PReceiveFormattedPlayerBalance::decode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PReceiveFormattedPlayerBalance, Supplier<NetworkEvent.Context>> getHandler() {
        return PReceiveFormattedPlayerBalance::handle;
    }
}
