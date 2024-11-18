package io.github.xinfra.lab.transport;

import io.github.xinfra.lab.rpc.config.ServerConfig;
import io.github.xinfra.lab.rpc.transport.TransportType;

public class XRemotingServerConfig implements ServerConfig {

    private int serverPort;



    @Override
    public int serverPort() {
        return serverPort;
    }

    @Override
    public TransportType transportType() {
        return TransportType.X_REMOTING;
    }
}
