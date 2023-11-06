package io.github.xinfra.lab.rpc.remoting.connection;

import io.github.xinfra.lab.rpc.remoting.protocol.Protocol;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolManager;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProtocolDecoder extends ByteToMessageDecoder {
    private int protocolLength = 1;

    private ProtocolManager protocolManager;

    public ProtocolDecoder(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= protocolLength) {
            in.markReaderIndex();
            byte protocolCode = in.readByte();
            in.resetReaderIndex();
            ProtocolType protocolType = ProtocolType.valueOfProtocolCode(protocolCode);
            protocolManager.getProtocol(protocolType).decoder().decode(ctx, in, out);
        }
    }
}
