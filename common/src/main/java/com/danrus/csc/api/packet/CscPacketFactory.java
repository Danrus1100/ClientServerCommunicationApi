package com.danrus.csc.api.packet;

import io.netty.buffer.ByteBuf;

@FunctionalInterface
public interface CscPacketFactory<P extends com.danrus.csc.api.packet.CscPacket> {
    P create(ByteBuf buf);
}
