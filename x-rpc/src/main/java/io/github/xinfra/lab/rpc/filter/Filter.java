package io.github.xinfra.lab.rpc.filter;

import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;
import io.github.xinfra.lab.rpc.invoker.Invoker;

public interface Filter {
    RpcResponse filter(Invoker invoker, RpcRequest request);
}
