package io.github.xinfra.lab.rpc.remoting.codec;

import io.github.xinfra.lab.rpc.remoting.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface Encoder {
    void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out);
}