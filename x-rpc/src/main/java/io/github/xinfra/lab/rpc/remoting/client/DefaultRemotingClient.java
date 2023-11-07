package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.remoting.Endpoint;
import io.github.xinfra.lab.rpc.remoting.connection.Connection;
import io.github.xinfra.lab.rpc.remoting.connection.ConnectionManager;
import io.github.xinfra.lab.rpc.remoting.connection.DefaultConnectionManager;

public class DefaultRemotingClient implements RemotingClient {

    private ConnectionManager connectionManager;

    public DefaultRemotingClient() {
        this.connectionManager = new DefaultConnectionManager();
    }

    @Override
    public Object syncCall(Object request, Endpoint endpoint) {
        Connection connection = connectionManager.getConnection(endpoint);
        // TODO

        return null;
    }
}
