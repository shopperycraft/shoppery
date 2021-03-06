package com.ki11erwolf.shoppery.packets;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.bank.BankManager;
import com.ki11erwolf.shoppery.bank.Wallet;
import net.minecraft.entity.player.PlayerEntity;
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
 * Sent by clients to request the server send back
 * the given players cents balance ({@link Wallet#getCents()}.
 */
public class PlayerCentsReqPacket extends Packet<PlayerCentsReqPacket> {

    /**
     * The UUID of the player who's
     * balance we're requesting.
     */
    private final String playerUUID;

    /**
     * Constructor.
     *
     * @param playerUUID the uuid of the player who's
     *                   balance we're requesting.
     */
    public PlayerCentsReqPacket(String playerUUID){
        this.playerUUID = playerUUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PlayerCentsReqPacket, PacketBuffer> getEncoder() {
        return (packet, buffer) -> writeString(packet.playerUUID, buffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PlayerCentsReqPacket> getDecoder() {
        return (buffer) -> new PlayerCentsReqPacket(readString(buffer));
    }

    /**
     * {@inheritDoc}
     *
     * Sends back the players cents balance in a new packet.
     */
    @Override
    BiConsumer<PlayerCentsReqPacket, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            try{
                PlayerEntity player = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer())
                        .getPlayerList().getPlayerByUUID(UUID.fromString(packet.playerUUID));

                if(player == null){
                    ShopperyMod.getNewLogger().error(
                            "Player requesting balance cannot be found: " + packet.playerUUID
                    );
                    return;
                }

                Wallet senderWallet = BankManager._getWallet(player.getEntityWorld(), player);
                send(
                        PacketDistributor.PLAYER.with(() -> ctx.get().getSender()),
                        new PlayerCentsRecPacket(senderWallet.getCents())
                );
            } catch (Exception e){
                ShopperyMod.getNewLogger().error("Failed to send back player balance", e);
            }
        });
    }
}
