package com.danrus.csc.api.packet;

import io.netty.buffer.ByteBuf;

public interface CscPacket {
    void read(ByteBuf buf);
    void write(ByteBuf buf);
}
