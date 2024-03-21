package io.github.xinfra.lab.rpc.filter;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;

public interface Filter {
    InvocationResult filter(Invoker invoker, Invocation invocation);

    default void onResult(InvocationResult invocationResult) {
    }


    default void onError(Throwable throwable) {
    }

}
