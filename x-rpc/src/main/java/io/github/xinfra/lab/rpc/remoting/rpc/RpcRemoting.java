package io.github.xinfra.lab.rpc.remoting.rpc;

import io.github.xinfra.lab.rpc.remoting.client.BaseRemoting;
import io.github.xinfra.lab.rpc.remoting.connection.Connection;
import io.github.xinfra.lab.rpc.remoting.exception.RemotingException;
import io.github.xinfra.lab.rpc.remoting.message.RpcRequestMessage;
import io.github.xinfra.lab.rpc.remoting.message.RpcResponseMessage;
import io.github.xinfra.lab.rpc.remoting.message.RpcMessageFactory;
import io.github.xinfra.lab.rpc.remoting.message.RpcStatusCode;

public class RpcRemoting extends BaseRemoting {
    private RpcMessageFactory rpcMessageFactory;

    public RpcRemoting(RpcMessageFactory rpcMessageFactory) {
        super(rpcMessageFactory);
        this.rpcMessageFactory = rpcMessageFactory;
    }

    public <R> R syncCall(Object request, Connection connection, int timeoutMills) throws InterruptedException {
        RpcRequestMessage requestMessage = rpcMessageFactory.createRequestMessage();
        requestMessage.setContent(request);
        requestMessage.setContentType(RpcRequestMessage.class.getName());

        // TODO
        RpcResponseMessage responseMessage = (RpcResponseMessage) this.syncCall(requestMessage, connection, timeoutMills);
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
