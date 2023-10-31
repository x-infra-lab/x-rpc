package io.github.xinfra.lab.rpc.filter;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.RpcResponse;
import io.github.xinfra.lab.rpc.invoker.Invoker;

public class MockFilter implements Filter {
    @Override
    public RpcResponse filter(Invoker invoker, RpcRequest request) {
        boolean isMock = false; // TODO
        if (isMock) {
            return doMockInvoke(request);
        }
        return invoker.invoke(request);
    }

    private RpcResponse doMockInvoke(RpcRequest request) {
        // TODO
        return null;
    }
}
