package com.danrus.csc.api.packet;

public record CscPacketType<P extends CscPacket>(short id, String tId, Class<P> packetClass, CscPacketFactory<P> factory) {}
