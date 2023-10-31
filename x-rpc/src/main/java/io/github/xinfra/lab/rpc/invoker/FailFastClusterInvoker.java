package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.RpcResponse;
import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;

public class FailFastClusterInvoker implements Invoker {
    private Cluster cluster;

    @Override
    public RpcResponse invoke(RpcRequest request) {
        ProviderInfo providerInfo = cluster.select(request);
        // TODO
        return null;
    }
}
