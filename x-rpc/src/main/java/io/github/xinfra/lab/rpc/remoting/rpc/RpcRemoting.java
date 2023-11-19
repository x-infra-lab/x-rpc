package io.github.xinfra.lab.rpc.remoting.rpc;

import io.github.xinfra.lab.rpc.remoting.Endpoint;
import io.github.xinfra.lab.rpc.remoting.client.BaseRemoting;
import io.github.xinfra.lab.rpc.remoting.connection.Connection;
import io.github.xinfra.lab.rpc.remoting.connection.ConnectionManager;
import io.github.xinfra.lab.rpc.remoting.exception.RemotingException;
import io.github.xinfra.lab.rpc.remoting.message.RpcRequestMessage;
import io.github.xinfra.lab.rpc.remoting.message.RpcResponseMessage;
import io.github.xinfra.lab.rpc.remoting.message.RpcMessageFactory;
import io.github.xinfra.lab.rpc.remoting.message.RpcStatusCode;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolManager;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.github.xinfra.lab.rpc.remoting.protocol.RpcProtocol;


public class RpcRemoting extends BaseRemoting {

    static {
        ProtocolManager.registerProtocol(ProtocolType.RPC, new RpcProtocol());
    }

    private RpcMessageFactory rpcMessageFactory;

    private ConnectionManager connectionManager;

    public RpcRemoting(ConnectionManager connectionManager) {
        super(ProtocolManager.getProtocol(ProtocolType.RPC).messageFactory());
        this.rpcMessageFactory = (RpcMessageFactory) ProtocolManager.getProtocol(ProtocolType.RPC).messageFactory();
        this.connectionManager = connectionManager;
    }

    public <R> R syncCall(Object request, Endpoint endpoint, int timeoutMills) throws InterruptedException {
        RpcRequestMessage requestMessage = rpcMessageFactory.createRequestMessage();
        requestMessage.setContent(request);
        requestMessage.setContentType(RpcRequestMessage.class.getName());

        Connection connection = connectionManager.getConnection(endpoint);
        // TODO check connection ??

        return syncCall(requestMessage, connection, timeoutMills);
    }

    public <R> R syncCall(RpcRequestMessage requestMessage, Connection connection, int timeoutMills) throws InterruptedException {
        // TODO FIXME
        RpcResponseMessage responseMessage = (RpcResponseMessage) super.syncCall(requestMessage, connection, timeoutMills);
        if (responseMessage.getStatus() != RpcStatusCode.SUCCESS) {
            Object result = responseMessage.getContent();
            if (result instanceof Throwable) {
                throw new RemotingException((Throwable) result);
            }
            throw new RemotingException("Remoting fail. unknown exception");
        }
        return (R) responseMessage.getContent();
    }
}