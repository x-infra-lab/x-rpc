package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;
import io.github.xinfra.lab.rpc.remoting.connection.Connection;
import io.github.xinfra.lab.rpc.remoting.connection.ConnectionManager;
import io.github.xinfra.lab.rpc.remoting.connection.DefaultConnectionManager;

import java.net.URL;

public class DefaultRemotingClient implements RemotingClient {

    private ConsumerConfig<?> config;
    private ConnectionManager connectionManager;

    public DefaultRemotingClient(ConsumerConfig<?> config) {
        this.config = config;
        this.connectionManager = new DefaultConnectionManager(config);
    }

    @Override
    public RpcResponse call(RpcRequest request, URL url) {
        Connection connection = connectionManager.getConnection(url);


        // TODO
        return null;
    }
}
