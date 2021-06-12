package com.ki11erwolf.shoppery.packets;

import com.ki11erwolf.shoppery.ShopperyMod;
import com.ki11erwolf.shoppery.price.ItemPrice;
import com.ki11erwolf.shoppery.price.ItemPrices;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Sent by client players to request the server send back the
 * {@link ItemPrice} of a defined item.
 */
public class ItemPriceReqPacket extends Packet<ItemPriceReqPacket> {

    /**
     * The item to do the price check on.
     */
    private final ResourceLocation itemRegistryName;

    /**
     * @param itemRegistryName the registry name of the item
     *                         to get the price of.
     */
    public ItemPriceReqPacket(ResourceLocation itemRegistryName){
        this.itemRegistryName = itemRegistryName;
    }

    /**
     * A static utility method that allows quickly
     * sending price check requests to the server.
     *
     * @param itemRegistryName the registry name of
     *        the item to price check.
     */
    public static void send(@Nullable ResourceLocation itemRegistryName){
        if(itemRegistryName != null)
            Packet.send(PacketDistributor.SERVER.noArg(), new ItemPriceReqPacket(itemRegistryName));
    }

    /**
     * Writes the given packets data to the
     * given PacketBuffer.
     *
     * @param msg given packet.
     * @param buf given buffer.
     */
    private static void encode(ItemPriceReqPacket msg, PacketBuffer buf){
        writeString(msg.itemRegistryName.getNamespace(), buf);
        writeString(msg.itemRegistryName.getPath(), buf);
    }

    /**
     * Creates and returns a new packet containing the
     * data in the given buffer.
     *
     * @param buf the given buffer.
     * @return the created packet.
     */
    private static ItemPriceReqPacket decode(PacketBuffer buf){
        String namespace = readString(buf);
        String path = readString(buf);

        return new ItemPriceReqPacket(new ResourceLocation(namespace, path));
    }

    /**
     * Sends back the requested items price.
     *
     * @param message the received packet.
     * @param ctx the sender.
     */
    private static void handle(final ItemPriceReqPacket message, Supplier<NetworkEvent.Context> ctx){
        handle(ctx, () -> {
            ItemStack itemStack;

            if(ForgeRegistries.BLOCKS.containsKey(message.itemRegistryName)){
                Block block = ForgeRegistries.BLOCKS.getValue(message.itemRegistryName);
                itemStack = new ItemStack(block == null ? Items.AIR : block);
            } else if(ForgeRegistries.ITEMS.containsKey(message.itemRegistryName)){
                Item item = ForgeRegistries.ITEMS.getValue(message.itemRegistryName);
                itemStack = new ItemStack(item == null ? Items.AIR : item);
            } else {
                ShopperyMod.getNewLogger().error(
                        "Failed to get requested item for item price check: ["
                                + message.itemRegistryName.toString() + "]"
                );

                send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()),
                        new ItemPriceRecPacket(false, 0, 0)
                );

                return;
            }

            ItemPrice itemStackPrice = ItemPrices.getPrice(itemStack);

            send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()),
                    (itemStackPrice == null) ?
                    new ItemPriceRecPacket(false, 0, 0) :
                    new ItemPriceRecPacket(true, itemStackPrice.getBuyPrice(),
                            itemStackPrice.getSellPrice()
                )
            );
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<ItemPriceReqPacket, PacketBuffer> getEncoder() {
        return ItemPriceReqPacket::encode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, ItemPriceReqPacket> getDecoder() {
        return ItemPriceReqPacket::decode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<ItemPriceReqPacket, Supplier<NetworkEvent.Context>> getHandler() {
        return ItemPriceReqPacket::handle;
    }
}
