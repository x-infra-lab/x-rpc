package io.github.xinfra.lab.rpc.remoting.connection;

import io.github.xinfra.lab.rpc.remoting.Endpoint;


public interface ConnectionFactory {
    Connection create(Endpoint endpoint) throws Exception;
}
