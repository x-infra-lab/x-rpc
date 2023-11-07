package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.remoting.Endpoint;

public interface RemotingClient {
    Object syncCall(Object request, Endpoint endpoint);
}
