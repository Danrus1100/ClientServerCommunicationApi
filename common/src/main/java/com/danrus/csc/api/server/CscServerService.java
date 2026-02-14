package com.danrus.csc.api.server;

import com.danrus.csc.api.packet.CscPacket;
import com.danrus.csc.api.packet.CscPacketType;

import java.util.UUID;

public interface CscServerService<P> {
    void sendPacket(CscPacket packet, P player);
    void sendPacket(CscPacket packet, UUID player);
    void broadcastPacket(CscPacket packet);
    void broadcastPacket(PacketBroadcaster<P> broadcaster);
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
