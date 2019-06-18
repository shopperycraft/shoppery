package com.ki11erwolf.shoppery.network.packets;

import com.ki11erwolf.shoppery.ShopperyMod;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class for any type of packet.
 *
 * This class handles the registering of packets
 * as well as providing a base class for packets.
 *
 * @param <S> the class implementing this class.
 */
@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class Packet<S extends Packet<S>> {

    /**
     * Manager subclass instance that handles the registering of packets.
     */
    private static final PacketManager MANAGER = new PacketManager();

    /**
     * Protected constructor.
     */
    Packet(){}

    /**
     * Used to get the static encoder method
     * that turns a packet into a byte array.
     *
     * @return Should return the static
     * encode method (e.g. Class::encode).
     */
    abstract BiConsumer<S, PacketBuffer> getEncoder();

    /**
     * Used to get the static decoder method
     * that turns a byte array into a packet.
     *
     * @return Should return the static
     * decode method (e.g. Class::decode).
     */
    abstract Function<PacketBuffer, S> getDecoder();

    /**
     * Used to get the static onPacketReceived
     * method that handles the event when it's
     * sent/received.
     *
     * @return Should return the static
     * handle method (e.g. Class::handle)
     */
    abstract BiConsumer<S, Supplier<NetworkEvent.Context>> getHandler();

    /**
     * Writes a String (and the length of the string)
     * to the given PacketBuffer.
     *
     * @param s the String to write.
     * @param buffer the buffer to write the String to.
     */
    static void writeString(String s, PacketBuffer buffer){
        int chars = s.length();
        buffer.writeInt(chars);

        for(char c : s.toCharArray()){
            buffer.writeChar(c);
        }
    }

    /**
     * Reads the next String in the given
     * PacketBuffer. Must be used with {@link
     * #writeString(String, PacketBuffer)}.
     *
     * @param buffer the given packet buffer.
     * @return the next String in the given packet buffer.
     */
    static String readString(PacketBuffer buffer){
        int chars = buffer.readInt();
        StringBuilder string = new StringBuilder();

        for(int i = 0; i < chars; i++){
            char c = buffer.readChar();
            string.append(c);
        }

        return string.toString();
    }

    /**
     * Utility method to handle receiving a packet
     * on the work thread & flag the packet as handled.
     *
     * @param context packet handle parameters.
     * @param runnable code to execute.
     */
    static void handle(Supplier<NetworkEvent.Context> context, Runnable runnable){
        context.get().enqueueWork(runnable);
        context.get().setPacketHandled(true);
    }

    /**
     * Private subclass used to register packets.
     */
    private static class PacketManager{

        /**
         * Communication protocol version. Value = 1.
         */
        private final String protocolVersion = Integer.toString(1);

        /**
         * Forge handler provided to register packets.
         */
        private final SimpleChannel handler
                = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(ShopperyMod.MODID, "main_channel"))
                    .clientAcceptedVersions(protocolVersion::equals)
                    .serverAcceptedVersions(protocolVersion::equals)
                    .networkProtocolVersion(() -> protocolVersion)
                    .simpleChannel();

        /**
         * Last used ID that a packet
         * was registered with. Incremented
         * with each new packet registration.
         */
        private int lastID = 0;

        /**
         * Private constructor.
         */
        private PacketManager(){}

        /**
         * Registers the given packet (must be an
         * object instance) to the game.
         *
         * @param packet the given packet object instance.
         */
        @SuppressWarnings("unchecked")
        void register(Packet packet){
            handler.registerMessage(
                    lastID++, packet.getClass(),
                    packet.getEncoder(), packet.getDecoder(), packet.getHandler()
            );
        }
    }

    /**
     * Sends the given packet to the given
     * recipient.
     *
     * @param target the recipient. See {@link PacketDistributor}.
     * @param packet the packet to send.
     * @param <M> the packet class type.
     */
    public static <M> void send(PacketDistributor.PacketTarget target, M packet){
        MANAGER.handler.send(target, packet);
    }

    /**
     * Initializes the packets system.
     */
    //Dummy method that allows us to
    //register/initialize the class with a static block.
    public static void init(){}

    /*
        Static block used to do
        one-time initialization
        & registration.
     */
    static {
        MANAGER.register(new PRequestPlayerBalance(null));
        MANAGER.register(new PReceivePlayerBalance(null));
        MANAGER.register(new PRequestFullPlayerBalance(null));
        MANAGER.register(new PReceiveFullPlayerBalance(null));
    }
}
