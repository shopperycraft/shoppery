package com.ki11erwolf.shoppery.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PReceivePlayerBalance extends Packet<PReceivePlayerBalance> {

    private static long lastReceivedBalance = 0;

    private final long balance;

    PReceivePlayerBalance(long balance){
        this.balance = balance;
    }

    @Override
    BiConsumer<PReceivePlayerBalance, PacketBuffer> getEncoder() {
        return (packet, buffer) -> {
            buffer.writeLong(packet.balance);
        };
    }

    @Override
    Function<PacketBuffer, PReceivePlayerBalance> getDecoder() {
        return (buffer -> new PReceivePlayerBalance(buffer.readLong()));
    }

    @Override
    BiConsumer<PReceivePlayerBalance, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            lastReceivedBalance = packet.balance;
        });
    }

    public static long getLastReceivedBalance(){
        return lastReceivedBalance;
    }
}
