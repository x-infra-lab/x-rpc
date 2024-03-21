package io.github.xinfra.lab.rpc.transport;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;

public interface ClientTransport {

    InvocationResult invokeAsync(Invocation invocation);
}
