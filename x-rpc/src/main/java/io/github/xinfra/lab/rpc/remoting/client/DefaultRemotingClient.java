package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.remoting.Endpoint;
import io.github.xinfra.lab.rpc.remoting.connection.Connection;
import io.github.xinfra.lab.rpc.remoting.connection.ConnectionManager;
import io.github.xinfra.lab.rpc.remoting.connection.DefaultConnectionManager;
import io.github.xinfra.lab.rpc.remoting.protocol.Message;
import io.github.xinfra.lab.rpc.remoting.protocol.MessageFactory;

import java.util.concurrent.Future;

public class DefaultRemotingClient implements RemotingClient {

    private ConnectionManager connectionManager;

    public DefaultRemotingClient() {
        this.connectionManager = new DefaultConnectionManager();
    }


    @Override
    public <R> R syncCall(Object request, Endpoint endpoint) {
        // TODO
        Connection connection = connectionManager.getConnection(endpoint);
        // TODO
        // Message message = MessageFactory.createMessage();
        return null;
    }

    @Override
    public <R> Future<R> asyncCall(Object request, Endpoint endpoint) {
        // TODO
        return null;
    }

    @Override
    public <R> void asyncCall(Object request, Endpoint endpoint, CallBack<R> callBack) {
    }

    @Override
    public void oneway(Object request, Endpoint endpoint) {
    }
}
