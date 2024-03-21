package io.github.xinfra.lab.rpc.filter;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;

public class MockFilter implements Filter {
    @Override
    public InvocationResult filter(Invoker invoker, Invocation invocation) {
        boolean isMock = false; // TODO
        if (isMock) {
            return doMockInvoke(invocation);
        }
        return invoker.invoke(invocation);
    }

    private InvocationResult doMockInvoke(Invocation request) {
        // TODO
        return null;
    }
}
