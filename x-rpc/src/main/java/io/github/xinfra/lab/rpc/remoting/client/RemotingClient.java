package io.github.xinfra.lab.rpc.remoting.client;

import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;

import java.net.URL;

public interface RemotingClient {
   RpcResponse call(RpcRequest request, URL url);
}
