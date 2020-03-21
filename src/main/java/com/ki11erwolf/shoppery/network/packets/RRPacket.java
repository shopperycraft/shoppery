package com.ki11erwolf.shoppery.network.packets;

/**
 * Represents a special type of Packet: a Request & Receive Packet.
 *
 * <p/>This packet type is treated as a single packet that stores
 * a sent value from the server, which is requested by the client.
 *
 * <p/> In reality, this class is simply a helping wrapper for two
 * packets (a request and a receive packet) that reduces boilerplate
 * and simplifies the creation & implementation process.
 *
 * @param <T> the type of value sent by the server.
 */
public class RRPacket<T> {

    protected T lastKnownValue;

    protected void request(Object... params){

    }


}
