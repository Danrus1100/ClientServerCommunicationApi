package com.danrus.csc.api.packet;

import java.util.HashMap;
import java.util.Map;

public class CscPackets {
    private static final Map<String, com.danrus.csc.api.packet.CscPacketType<? extends com.danrus.csc.api.packet.CscPacket>> BY_ID = new HashMap<>();
    private static final Map<Class<? extends com.danrus.csc.api.packet.CscPacket>, com.danrus.csc.api.packet.CscPacketType<? extends com.danrus.csc.api.packet.CscPacket>> BY_CLASS = new HashMap<>();
    private static final Map<Short, com.danrus.csc.api.packet.CscPacketType<? extends com.danrus.csc.api.packet.CscPacket>> BY_NUM_ID = new HashMap<>();
    private static final Map<String, Short> ID_TO_NUM_ID = new HashMap<>();
    private static short nextId = Short.MIN_VALUE;

    public static <P extends com.danrus.csc.api.packet.CscPacket> com.danrus.csc.api.packet.CscPacketType<P> register(String id, Class<P> packetClass, com.danrus.csc.api.packet.CscPacketFactory<P> factory) {
        short numId = ID_TO_NUM_ID.getOrDefault(id, nextId++);
        com.danrus.csc.api.packet.CscPacketType<P> packetType = new com.danrus.csc.api.packet.CscPacketType<>(numId, packetClass, factory);
        BY_ID.put(id, packetType);
        BY_CLASS.put(packetClass, packetType);
        BY_NUM_ID.put(numId, packetType);
        ID_TO_NUM_ID.put(id, numId);
        return packetType;
    }

    public static com.danrus.csc.api.packet.CscPacketType<? extends com.danrus.csc.api.packet.CscPacket> getById(String id) {
        return BY_ID.get(id);
    }

    @SuppressWarnings("unchecked")
    public static <P extends com.danrus.csc.api.packet.CscPacket> com.danrus.csc.api.packet.CscPacketType<P> getByClass(Class<P> packetClass) {
        return (com.danrus.csc.api.packet.CscPacketType<P>) BY_CLASS.get(packetClass);
    }

    public static com.danrus.csc.api.packet.CscPacketType<? extends com.danrus.csc.api.packet.CscPacket> getByNumId(short numId) {
        return BY_NUM_ID.get(numId);
    }

    public static Short getNumIdById(String id) {
        return ID_TO_NUM_ID.get(id);
    }

    public static com.danrus.csc.api.packet.CscPacketType<? extends com.danrus.csc.api.packet.CscPacket> getByIdOrThrow(String id) {
        com.danrus.csc.api.packet.CscPacketType<? extends com.danrus.csc.api.packet.CscPacket> packetType = getById(id);
        if (packetType == null) {
            throw new IllegalArgumentException("No packet type registered with id: " + id);
        }
        return packetType;
    }
}
