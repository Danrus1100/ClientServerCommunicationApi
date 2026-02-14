package com.danrus.csc.api.packet;

import io.netty.buffer.ByteBuf;

public class UnknownPacket implements com.danrus.csc.api.packet.CscPacket {
    public UnknownPacket(ByteBuf buf) {
        read(buf);
    }

    @Override
    public void read(ByteBuf buf) {
        buf.skipBytes(buf.readableBytes());
    }

    @Override
    public void write(ByteBuf buf) {}
}
