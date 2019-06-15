package com.ki11erwolf.shoppery.network.packets;

import com.ki11erwolf.shoppery.bank.Wallet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used to send clients (EntityPlayerMP) their formatted balance
 * {@link Wallet#getFormattedBalance()}.
 *
 * This class saves the last received balance so it can
 * be retrieved easily, however, it is recommended a
 * {@link PReceivePlayerBalance} is sent to keep
 * the balance in sync before retrieving the balance.
 */
public class PReceivePlayerBalance extends Packet<PReceivePlayerBalance> {

    /**
     * The formatted balance given from the last
     * received PReceivePlayerBalance packet.
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
    PReceivePlayerBalance(String formattedBalance){
        this.formattedBalance = formattedBalance;
    }

    /**
     * Writes the given packets data to the
     * given PacketBuffer.
     *
     * @param msg given packet.
     * @param buf given buffer.
     */
    private static void encode(PReceivePlayerBalance msg, PacketBuffer buf){
        writeString(msg.formattedBalance, buf);
    }

    /**
     * Creates and returns a new packet containing the
     * data in the given buffer.
     *
     * @param buf the given buffer.
     * @return the created packet.
     */
    private static PReceivePlayerBalance decode(PacketBuffer buf){
        return new PReceivePlayerBalance(readString(buf));
    }

    /**
     * Caches the given player balance from the
     * received packet.
     *
     * @param message the received packet.
     * @param ctx the sender.
     */
    private static void handle(final PReceivePlayerBalance message, Supplier<NetworkEvent.Context> ctx){
        handle(ctx, () -> lastReceivedBalance = message.formattedBalance);
    }

    /**
     * @return The formatted balance given from the last
     * received PReceivePlayerBalance packet.
     * This is specific to clients.
     */
    public static String getLastKnownBalance(){
        return lastReceivedBalance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PReceivePlayerBalance, PacketBuffer> getEncoder() {
        return PReceivePlayerBalance::encode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PReceivePlayerBalance> getDecoder() {
        return PReceivePlayerBalance::decode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PReceivePlayerBalance, Supplier<NetworkEvent.Context>> getHandler() {
        return PReceivePlayerBalance::handle;
    }
}
