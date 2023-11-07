package io.github.xinfra.lab.rpc.remoting.connection;

import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class Connection {

    /**
     * Attribute key for protocol
     */
    public static final AttributeKey<ProtocolType> PROTOCOL = AttributeKey.valueOf("protocol");

    public static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");


    private Channel channel;

    public Connection(Channel channel, ProtocolType protocolType) {
        this.channel = channel;
        this.channel.attr(PROTOCOL).set(protocolType);
        this.channel.attr(CONNECTION).set(this);
    }

}
