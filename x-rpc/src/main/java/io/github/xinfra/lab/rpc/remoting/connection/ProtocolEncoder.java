package io.github.xinfra.lab.rpc.remoting.connection;

import io.github.xinfra.lab.rpc.remoting.protocol.Message;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolManager;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtocolEncoder extends MessageToByteEncoder<Message> {

    private ProtocolManager protocolManager;

    public ProtocolEncoder(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        ProtocolType protocolType = ctx.channel().attr(Connection.PROTOCOL).get();
        protocolManager.getProtocol(protocolType).encoder().encode(ctx, msg, out);
    }
}
