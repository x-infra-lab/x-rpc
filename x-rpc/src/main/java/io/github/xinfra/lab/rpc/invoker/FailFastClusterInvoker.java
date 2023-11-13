package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;
import io.github.xinfra.lab.rpc.remoting.Endpoint;
import io.github.xinfra.lab.rpc.remoting.rpc.RpcClient;

public class FailFastClusterInvoker implements Invoker {
    private Cluster cluster;

    private RpcClient rpcClient;

    private ConsumerConfig<?> config;

    public FailFastClusterInvoker(Cluster cluster, ConsumerConfig<?> config) {
        this.cluster = cluster;
        this.config = config;
        this.rpcClient = new RpcClient();
    }

    @Override
    public RpcResponse invoke(RpcRequest request) {
        // TODO FailFast
        ProviderInfo providerInfo = cluster.select(request);
        // TODO providerInfo to endpoint
        Endpoint endpoint = null;
        return (RpcResponse) rpcClient.syncCall(request, endpoint);
    }
}
