package com.danrus.csc.api.server;

import com.danrus.csc.api.packet.CscPacket;
import com.danrus.csc.api.packet.CscPacketType;

import java.util.UUID;

public interface CscServerService<P> {
    /**
     * Sends a packet to a player.
     * @param packet the packet to send
     * @param player the player to send the packet to
     */
    void sendPacket(CscPacket packet, P player);
    void sendPacket(CscPacket packet, UUID player);

    /**
     * Broadcasts a packet to all players.
     * @param packet the packet to broadcast
     */
    void broadcastPacket(CscPacket packet);


    /**
     * Broadcasts a packet to all players, using the provided broadcaster to create the packet for each player.
     * example usage:
     * <pre>
     *     service.broadcastPacket(player -> new MyPacket(player.getUniqueId()));
     * </pre>
     * @param broadcaster the broadcaster to create the packet for each player
     */
    void broadcastPacket(PacketBroadcaster<P> broadcaster);


    /**
     * Registers a listener for the specified packet types. The listener will be called whenever a packet of the specified type(s) is received from a player.
     * @param listener the listener to register
     * @param types the packet types to listen for
     */
    void registerListener(ServerPacketListener<P> listener, CscPacketType<?>... types);
    <T extends CscPacket> void registerListener(ServerSinglePacketListener<P, T> listener, CscPacketType<T> type);

    @FunctionalInterface
    public interface PacketBroadcaster<P> {
        CscPacket send(P player);
    }

    @FunctionalInterface
    public interface ServerPacketListener<P> {
        void receive(CscPacket packet, P player);
    }

    @FunctionalInterface
    public interface ServerSinglePacketListener<P, T extends CscPacket> {
        void receive(T packet, P player);
    }
}
