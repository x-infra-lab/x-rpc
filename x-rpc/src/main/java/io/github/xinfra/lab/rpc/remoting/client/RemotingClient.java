package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;
import io.github.xinfra.lab.rpc.remoting.Endpoint;

public interface RemotingClient {
   RpcResponse syncCall(RpcRequest request, Endpoint endpoint);
}
