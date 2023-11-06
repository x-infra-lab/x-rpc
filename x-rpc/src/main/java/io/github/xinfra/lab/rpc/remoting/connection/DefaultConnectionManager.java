package io.github.xinfra.lab.rpc.remoting.connection;


import io.github.xinfra.lab.rpc.remoting.Endpoint;


public class DefaultConnectionManager implements ConnectionManager {

    private ConnectionFactory connectionFactory;

    public DefaultConnectionManager() {
        this.connectionFactory = new DefaultConnectionFactory();
    }


    @Override
    public Connection getConnection(Endpoint endpoint) {
        // TODO
        return null;
    }
}
