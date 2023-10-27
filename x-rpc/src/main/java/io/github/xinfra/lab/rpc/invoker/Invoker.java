package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.RpcResponse;

public interface Invoker {
    RpcResponse invoke(RpcRequest request);
}
