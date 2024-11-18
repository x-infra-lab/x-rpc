package io.github.xinfra.lab.rpc.config;

import io.github.xinfra.lab.rpc.transport.TransportType;

public interface ServerConfig {

    int serverPort();

    TransportType transportType();

}
