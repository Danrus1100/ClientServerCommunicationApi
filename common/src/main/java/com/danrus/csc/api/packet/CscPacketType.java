package com.danrus.csc.api.packet;

public record CscPacketType<P extends com.danrus.csc.api.packet.CscPacket>(short id, Class<P> packetClass, com.danrus.csc.api.packet.CscPacketFactory<P> factory) {}
