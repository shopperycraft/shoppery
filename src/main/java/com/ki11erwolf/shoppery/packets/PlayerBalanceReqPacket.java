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
 * the players balance (excluding cents)
 * as a {@code long} ({@link Wallet#getBalance()}).
 */
public class PlayerBalanceReqPacket extends Packet<PlayerBalanceReqPacket> {

    /**
     * The player whose balance we're requesting.
     */
    private final String playerUUID;

    /**
     * Constructor.
     *
     * @param playerUUID The player whose balance we're requesting.
     */
    public PlayerBalanceReqPacket(String playerUUID){
        this.playerUUID = playerUUID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PlayerBalanceReqPacket, PacketBuffer> getEncoder() {
        return (packet, buffer) -> writeString(packet.playerUUID, buffer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PlayerBalanceReqPacket> getDecoder() {
        return (buffer) -> new PlayerBalanceReqPacket(readString(buffer));
    }

    /**
     * {@inheritDoc}
     *
     * Sends back a packet containing the players balance (excluding cents).
     */
    @Override
    BiConsumer<PlayerBalanceReqPacket, Supplier<NetworkEvent.Context>> getHandler() {
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
                        new PlayerBalanceRecPacket(senderWallet.getBalance())
                );
            } catch (Exception e){
                ShopperyMod.getNewLogger().error("Failed to send back player balance", e);
            }
        });
    }
}
