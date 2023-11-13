package io.github.xinfra.lab.rpc.remoting.connection;

import io.github.xinfra.lab.rpc.remoting.client.InvokeFuture;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Connection {

    /**
     * Attribute key for protocol
     */
    public static final AttributeKey<ProtocolType> PROTOCOL = AttributeKey.valueOf("protocol");

    public static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");

    private ConcurrentHashMap<Integer, InvokeFuture<?>> invokeMap = new ConcurrentHashMap<>();

    @Getter
    private Channel channel;
    private AtomicInteger requestIdGenerator = new AtomicInteger(0);

    public Connection(Channel channel, ProtocolType protocolType) {
        this.channel = channel;
        this.channel.attr(PROTOCOL).set(protocolType);
        this.channel.attr(CONNECTION).set(this);
    }


    public Integer nextRequestId() {
        return requestIdGenerator.getAndIncrement();
    }

    public void addInvokeFuture(Integer requestId, InvokeFuture<?> invokeFuture) {
        invokeMap.put(requestId, invokeFuture);
    }

    public void removeInvokeFuture(Integer requestId) {
        invokeMap.remove(requestId);
    }
}
