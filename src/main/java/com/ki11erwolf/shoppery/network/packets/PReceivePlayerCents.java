package com.ki11erwolf.shoppery.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PReceivePlayerCents extends Packet<PReceivePlayerCents> {

    private static byte lastReceivedBalance = 0;

    private final byte cents;

    PReceivePlayerCents(byte cents){
        this.cents = cents;
    }

    @Override
    BiConsumer<PReceivePlayerCents, PacketBuffer> getEncoder() {
        return (packet, buffer) -> {
            buffer.writeByte(packet.cents);
        };
    }

    @Override
    Function<PacketBuffer, PReceivePlayerCents> getDecoder() {
        return (buffer -> new PReceivePlayerCents(buffer.readByte()));
    }

    @Override
    BiConsumer<PReceivePlayerCents, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            lastReceivedBalance = packet.cents;
        });
    }

    public static byte getLastReceivedBalance(){
        return lastReceivedBalance;
    }
}