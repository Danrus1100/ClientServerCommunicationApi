package com.danrus.csc.api.packet;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CscPackets {
    private static final Map<String, CscPacketType<? extends CscPacket>> BY_ID = new HashMap<>();
    private static final Map<Class<? extends CscPacket>, CscPacketType<? extends CscPacket>> BY_CLASS = new HashMap<>();
    private static final Map<Short, CscPacketType<? extends CscPacket>> BY_NUM_ID = new HashMap<>();
    private static final Map<String, Short> ID_TO_NUM_ID = new HashMap<>();

    public static <P extends CscPacket> CscPacketType<P> register(String id, Class<P> packetClass, CscPacketFactory<P> factory) {
        short numId = generateCrc16(id);
        if (BY_NUM_ID.containsKey(numId)) {
            CscPacketType<?> existing = BY_NUM_ID.get(numId);
            if (!existing.tId().equals(id)) {
                throw new IllegalStateException(String.format(
                        "CRITICAL COLLISION: Packets '%s' and '%s' both mapped to ID %d. Rename one of them!",
                        id, existing.tId(), numId));
            }
        }
        CscPacketType<P> packetType = new CscPacketType<>(numId, id, packetClass, factory);
        BY_ID.put(id, packetType);
        BY_CLASS.put(packetClass, packetType);
        BY_NUM_ID.put(numId, packetType);
        ID_TO_NUM_ID.put(id, numId);
        return packetType;
    }

    private static short generateCrc16(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        int crc = 0xFFFF;

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= 0x1021;
            }
        }
        return (short) (crc & 0xFFFF);
    }

    public static CscPacketType<? extends CscPacket> getById(String id) {
        return BY_ID.get(id);
    }

    @SuppressWarnings("unchecked")
    public static <P extends CscPacket> CscPacketType<P> getByClass(Class<P> packetClass) {
        return (CscPacketType<P>) BY_CLASS.get(packetClass);
    }

    public static CscPacketType<? extends CscPacket> getByNumId(short numId) {
        return BY_NUM_ID.get(numId);
    }

    public static Short getNumIdById(String id) {
        return ID_TO_NUM_ID.get(id);
    }

    public static CscPacketType<? extends CscPacket> getByIdOrThrow(String id) {
        CscPacketType<? extends CscPacket> packetType = getById(id);
        if (packetType == null) {
            throw new IllegalArgumentException("No packet type registered with id: " + id);
        }
        return packetType;
    }
}
