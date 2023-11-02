package io.github.xinfra.lab.rpc.remoting.connection;

import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.remoting.codec.ProtocolType;
import io.github.xinfra.lab.rpc.remoting.serialization.SerializationType;

import java.net.URL;

public class DefaultConnectionFactory implements ConnectionFactory {

   private ConsumerConfig<?> config;

    public DefaultConnectionFactory(ConsumerConfig<?> config) {
        this.config = config;
    }

    @Override
    public Connection create(URL url) {
        return null;
    }
}
