package com.ki11erwolf.shoppery.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Servers {@link com.ki11erwolf.shoppery.price.ItemPrice}
 * response packet, receiver and cache.
 */
public class ItemPriceRecPacket extends Packet<ItemPriceRecPacket> {

    /**
     * The cached flag from the last received packet
     * telling us whether the item has a price.
     */
    private static boolean lastReceivedHasPrice;

    /**
     * The cached buy price from the last received packet.
     */
    private static double lastReceivedBuyPrice;

    /**
     * The cached sell price from the last received packet.
     */
    private static double lastReceivedSellPrice;

    /**
     * The flag telling us whether the item has a price.
     */
    private boolean hasPrice;

    /**
     * The buy price of the item.
     */
    private double buyPrice;

    /**
     * The sell price of the item.
     */
    private double sellPrice;

    /**
     * @param hasPrice The flag telling us whether the item has a price.
     * @param buyPrice The buy price of the item.
     * @param sellPrice The sell price of the item.
     */
    ItemPriceRecPacket(boolean hasPrice, double buyPrice, double sellPrice){
        this.hasPrice = hasPrice;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    /**
     * Writes the given packets data to the
     * given PacketBuffer.
     *
     * @param msg given packet.
     * @param buf given buffer.
     */
    private static void encode(ItemPriceRecPacket msg, PacketBuffer buf){
        buf.writeBoolean(msg.hasPrice);
        buf.writeDouble(msg.buyPrice);
        buf.writeDouble(msg.sellPrice);
    }

    /**
     * Creates and returns a new packet containing the
     * data in the given buffer.
     *
     * @param buf the given buffer.
     * @return the created packet.
     */
    private static ItemPriceRecPacket decode(PacketBuffer buf){
        boolean hasPrice = buf.readBoolean();
        double buyPrice = buf.readDouble();
        double sellPrice = buf.readDouble();

        return new ItemPriceRecPacket(hasPrice, buyPrice, sellPrice);
    }

    /**
     * Caches the given item price information
     * from the received packet.
     *
     * @param message the received packet.
     * @param ctx the sender.
     */
    private static void handle(final ItemPriceRecPacket message, Supplier<NetworkEvent.Context> ctx){
        handle(ctx, () -> {
            lastReceivedHasPrice = message.hasPrice;
            lastReceivedBuyPrice = message.buyPrice;
            lastReceivedSellPrice = message.sellPrice;
        });
    }

    /**
     * @return The flag telling us whether the item in the last
     * request packet has a price.
     */
    public static boolean doesLastReceivedHavePrice() {
        return lastReceivedHasPrice;
    }

    /**
     * @return The buy price of the item in the last
     * request packet.
     */
    public static double getLastReceivedBuyPrice() {
        return lastReceivedBuyPrice;
    }

    /**
     * @return The sell price of the item in the last
     * request packet.
     */
    public static double getLastReceivedSellPrice() {
        return lastReceivedSellPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<ItemPriceRecPacket, PacketBuffer> getEncoder() {
        return ItemPriceRecPacket::encode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, ItemPriceRecPacket> getDecoder() {
        return ItemPriceRecPacket::decode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<ItemPriceRecPacket, Supplier<NetworkEvent.Context>> getHandler() {
        return ItemPriceRecPacket::handle;
    }
}
