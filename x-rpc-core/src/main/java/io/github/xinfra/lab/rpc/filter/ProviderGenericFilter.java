package io.github.xinfra.lab.rpc.filter;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;

public class ProviderGenericFilter implements Filter{
    @Override
    public InvocationResult filter(Invoker invoker, Invocation invocation) {
        // todo
        return null;
    }

    @Override
    public void onResult(InvocationResult invocationResult) {
        Filter.super.onResult(invocationResult);
    }

    @Override
    public void onError(Throwable throwable) {
        Filter.super.onError(throwable);
    }
}
