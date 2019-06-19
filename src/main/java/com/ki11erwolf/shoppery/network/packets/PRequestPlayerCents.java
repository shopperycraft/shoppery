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

public class PRequestPlayerCents extends Packet<PRequestPlayerCents> {

    private final String playerUUID;

    public PRequestPlayerCents(String playerUUID){
        this.playerUUID = playerUUID;
    }

    @Override
    BiConsumer<PRequestPlayerCents, PacketBuffer> getEncoder() {
        return (packet, buffer) -> {
            writeString(packet.playerUUID, buffer);
        };
    }

    @Override
    Function<PacketBuffer, PRequestPlayerCents> getDecoder() {
        return (buffer) -> new PRequestPlayerCents(readString(buffer));
    }

    @Override
    BiConsumer<PRequestPlayerCents, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            try{
                EntityPlayer player = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer())
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
                        new PReceivePlayerCents(senderWallet.getCents())
                );
            } catch (Exception e){
                ShopperyMod.getNewLogger().error("Failed to send back player balance", e);
            }
        });
    }
}