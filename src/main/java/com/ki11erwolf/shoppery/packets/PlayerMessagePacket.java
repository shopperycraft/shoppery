package com.ki11erwolf.shoppery.packets;

import com.ki11erwolf.shoppery.util.ClientPlayerFetcher;
import com.ki11erwolf.shoppery.util.LocaleDomain;
import com.ki11erwolf.shoppery.util.LocaleDomains;
import com.ki11erwolf.shoppery.util.MCUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Allows sending players formatted client side localized chat messages
 * from the server side.
 */
public class PlayerMessagePacket extends Packet<PlayerMessagePacket> {

    /**
     * The UUID of the player to send the message to in string form.
     */
    private final String playerUUID;

    /**
     * The LocaleDomain of the message.
     */
    private final String messageDomain;

    /**
     * The locale messages identifying String.
     */
    private final String messageIdentifier;

    /**
     * Formatting parameters.
     */
    private final Object[] parameters;

    /**
     * Sends a localized message to the given player.
     *
     * Will send a request to the given players client,
     * requesting that it send the specified localized
     * message to the player in the chat.
     *
     * @param player the given player to send the message to.
     * @param domain the localized messages domain.
     * @param identifier the localized messages identifier.
     * @param parameters the formatting parameters.
     */
    public static void send(PlayerEntity player, LocaleDomain domain,
                               String identifier, Object... parameters){
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        Packet.send(
                PacketDistributor.PLAYER.with(() -> serverPlayer),
                new PlayerMessagePacket(
                        player.getUniqueID().toString(),
                        domain.getDomain(),
                        identifier, parameters
                )
        );
    }

    /**
     * Generic Constructor.
     */
    PlayerMessagePacket(String playerUUID, String messageDomain,
                               String messageIdentifier, Object... parameters){
        this.playerUUID = playerUUID;
        this.messageDomain = messageDomain;
        this.messageIdentifier = messageIdentifier;
        this.parameters = parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    BiConsumer<PlayerMessagePacket, PacketBuffer> getEncoder() {
        return (packet, buffer) -> {
            writeString(packet.playerUUID, buffer);
            writeString(packet.messageDomain, buffer);
            writeString(packet.messageIdentifier, buffer);

            for(Object parameter : packet.parameters){
                writeString(parameter.toString(), buffer);
            }

            writeString("§", buffer);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Function<PacketBuffer, PlayerMessagePacket> getDecoder() {
        return (buffer) -> {
            String playerUUID = readString(buffer);
            String messageDomain = readString(buffer);
            String messageIdentifier = readString(buffer);
            List<String> parameters = new ArrayList<>();

            String parameter;
            while(!(parameter = readString(buffer)).equals("§")){
                parameters.add(parameter);
            }

            return new PlayerMessagePacket(
                    playerUUID, messageDomain, messageIdentifier, parameters.toArray(new Object[0])
            );
        };
    }

    /**
     * {@inheritDoc}
     *
     * <p/>Performs the sending of the message to player from the
     * client side, where localization classes are available.
     */
    @Override
    BiConsumer<PlayerMessagePacket, Supplier<NetworkEvent.Context>> getHandler() {
        return (packet, ctx) -> handle(ctx, () -> {
            PlayerEntity player = ((FMLEnvironment.dist.isClient())
                    ? ClientPlayerFetcher.getClientPlayer()
                    : MCUtil.getPlayerFromUUID(UUID.fromString(packet.playerUUID))
            );

            if(player != null)
                player.sendMessage(new StringTextComponent(
                        ((LocaleDomain) () -> removeLastChar(packet.messageDomain
                                .replace(LocaleDomains.DEFAULT.getName() + ".", ""))
                        ).format(packet.messageIdentifier, packet.parameters)
                ), player.getUniqueID());
        });
    }

    /**
     * Removes the last character from a given String.
     *
     * @param str the given String.
     * @return the given String with the last character
     * removed.
     */
    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }
}
