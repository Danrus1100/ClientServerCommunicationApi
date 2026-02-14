package com.danrus.csc.api.client;

import com.danrus.csc.api.packet.CscPacket;
import com.danrus.csc.api.packet.CscPacketType;

public interface CscClientService {
    /**
     * Sends a packet to the server.
     * @param packet The packet to send.
     */
    void sendPacket(CscPacket packet);


    /**
     * Registers a listener for the specified packet types.
     * @param listener The listener to register. Will be not typed, so you will need to check the packet type in the listener.
     * @param packets The packet types to listen for.
     */
    void registerListener(ClientPacketListener listener, CscPacketType<?>... packets);

    /**
    * Registers a listener for the specified packet type.
    * @param listener The listener to register. Will be typed, so you don't need to check the packet type in the listener.
    * @param type The packet type to listen for.
    * @param <A> The type of the packet.
    */
    <A extends CscPacket> void registerListener(ClientSinglePacketListener<A> listener, CscPacketType<A> type);

    @FunctionalInterface
    interface ClientPacketListener {
        void receive(CscPacket packet);
    }

    @FunctionalInterface
    interface ClientSinglePacketListener<T extends CscPacket> {
        void receive(T packet);
    }
}
