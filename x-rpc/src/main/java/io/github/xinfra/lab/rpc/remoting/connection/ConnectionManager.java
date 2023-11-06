package io.github.xinfra.lab.rpc.remoting.connection;

import io.github.xinfra.lab.rpc.remoting.Endpoint;


public interface ConnectionManager {

    Connection getConnection(Endpoint endpoint);
}
