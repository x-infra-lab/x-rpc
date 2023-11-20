package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;


public class FailFastClusterInvoker implements Invoker {
    private Cluster cluster;


    private ConsumerConfig<?> config;

    public FailFastClusterInvoker(Cluster cluster, ConsumerConfig<?> config) {
        this.cluster = cluster;
        this.config = config;
    }

    @Override
    public RpcResponse invoke(RpcRequest request) {
        // TODO FailFast
        ProviderInfo providerInfo = cluster.select(request);
        // TODO providerInfo to endpoint

        // TODO
        return null;
    }
}
