package com.ki11erwolf.shoppery.network.packets;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Used to request that the server send back a
 * packet containing the given players balance.
 *
 * This packet will intercept itself and send back
 * the requested balance when received.
 */
public class PRequestFormattedPlayerBalance extends Packet<PRequestFormattedPlayerBalance> {

    /**
     * The player requesting their balance.
     */
    private final String playerUUID;

    /**
     * @param playerUUID the player requesting their balance.
     */
    public PRequestFormattedPlayerBalance(String playerUUID){
        this.playerUUID = playerUUID;
    }

    /**
     * Writes the given packets data to the
     * given PacketBuffer.
     *
     * @param msg given packet.
     * @param buf given buffer.
     */
    private static void encode(PRequestFormattedPlayerBalance msg, PacketBuffer buf){
        writeString(msg.playerUUID, buf);
    }

    /**
     * Creates and returns a new packet containing the
     * data in the given buffer.
     *
     * @param buf the given buffer.
     * @return the created packet.
     */
    private static PRequestFormattedPlayerBalance decode(PacketBuffer buf){
        return new PRequestFormattedPlayerBalance(readString(buf));
    }

    /**
     * Sends back the requesting players formatted balance.
     *
     * @param message the received packet.
     * @param ctx the sender.
     */
    private static void handle(final PRequestFormattedPlayerBalance message, Supplier<NetworkEvent.Context> ctx){
        handle(ctx, () -> {
            try{
                EntityPlayer player = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer())
                        .getPlayerList().getPlayerByUUID(UUID.fromString(message.playerUUID));

                if(player == null){
                    ShopperyMod.getNewLogger().error(
                            "Player requesting balance cannot be found: " + message.playerUUID
                    );
                    return;
                }

                Wallet senderWallet = BankManager._getWallet(player.getEntityWorld(), player);
                send(
                        PacketDistributor.PLAYER.with(() -> ctx.get().getSender()),
                        new PReceiveFormattedPlayerBalance(senderWallet.getFormattedBalance())
                );
            } catch (Exception e){
                ShopperyMod.getNewLogger().error("Failed to send back player balance", e);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PRequestFormattedPlayerBalance, PacketBuffer> getEncoder() {
        return PRequestFormattedPlayerBalance::encode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PRequestFormattedPlayerBalance> getDecoder() {
        return PRequestFormattedPlayerBalance::decode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PRequestFormattedPlayerBalance, Supplier<NetworkEvent.Context>> getHandler() {
        return PRequestFormattedPlayerBalance::handle;
    }
}
