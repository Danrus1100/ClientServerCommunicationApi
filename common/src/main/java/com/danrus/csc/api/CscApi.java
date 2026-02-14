package com.danrus.csc.api;

import com.danrus.csc.api.packet.*;

public class CscApi {
    public static final String CHANNEL = "csc-main";

    public static <P extends CscPacket> CscPacketType<P> registerPacket(String id, Class<P> packetClass, CscPacketFactory<P> factory) {
        return CscPackets.register(id, packetClass, factory);
    }
}
