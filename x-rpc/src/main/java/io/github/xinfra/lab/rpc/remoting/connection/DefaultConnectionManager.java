package io.github.xinfra.lab.rpc.remoting.connection;

import io.github.xinfra.lab.rpc.config.ConsumerConfig;

import java.net.URL;

public class DefaultConnectionManager implements ConnectionManager {

    private ConsumerConfig<?> config;
    private ConnectionFactory connectionFactory;

    public DefaultConnectionManager(ConsumerConfig<?> config) {
        this.config = config;
        this.connectionFactory = new DefaultConnectionFactory(config);
    }

    @Override
    public Connection getConnection(URL url) {
        // todo
        return null;
    }
}
