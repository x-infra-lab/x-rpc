package io.github.xinfra.lab.rpc.invoker;

public interface Invoker {
    RpcResponse invoke(RpcRequest request);
}
