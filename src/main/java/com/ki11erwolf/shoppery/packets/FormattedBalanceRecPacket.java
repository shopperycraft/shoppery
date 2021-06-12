package com.ki11erwolf.shoppery.packets;

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
 * {@link FormattedBalanceReqPacket} is sent to keep
 * the balance in sync before retrieving the balance.
 */
public class FormattedBalanceRecPacket extends Packet<FormattedBalanceRecPacket> {

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
    FormattedBalanceRecPacket(String formattedBalance){
        this.formattedBalance = formattedBalance;
    }

    /**
     * Writes the given packets data to the
     * given PacketBuffer.
     *
     * @param msg given packet.
     * @param buf given buffer.
     */
    private static void encode(FormattedBalanceRecPacket msg, PacketBuffer buf){
        writeString(msg.formattedBalance, buf);
    }

    /**
     * Creates and returns a new packet containing the
     * data in the given buffer.
     *
     * @param buf the given buffer.
     * @return the created packet.
     */
    private static FormattedBalanceRecPacket decode(PacketBuffer buf){
        return new FormattedBalanceRecPacket(readString(buf));
    }

    /**
     * Caches the given player balance from the
     * received packet.
     *
     * @param message the received packet.
     * @param ctx the sender.
     */
    private static void handle(final FormattedBalanceRecPacket message, Supplier<NetworkEvent.Context> ctx){
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
    BiConsumer<FormattedBalanceRecPacket, PacketBuffer> getEncoder() {
        return FormattedBalanceRecPacket::encode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, FormattedBalanceRecPacket> getDecoder() {
        return FormattedBalanceRecPacket::decode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<FormattedBalanceRecPacket, Supplier<NetworkEvent.Context>> getHandler() {
        return FormattedBalanceRecPacket::handle;
    }
}
