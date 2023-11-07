package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;
import io.github.xinfra.lab.rpc.remoting.Endpoint;
import io.github.xinfra.lab.rpc.remoting.client.DefaultRemotingClient;
import io.github.xinfra.lab.rpc.remoting.client.RemotingClient;

public class FailFastClusterInvoker implements Invoker {
    private Cluster cluster;

    private RemotingClient remotingClient;

    private ConsumerConfig<?> config;

    public FailFastClusterInvoker(Cluster cluster, ConsumerConfig<?> config) {
        this.cluster = cluster;
        this.config = config;
        this.remotingClient = new DefaultRemotingClient();
    }

    @Override
    public RpcResponse invoke(RpcRequest request) {
        // TODO FailFast
        ProviderInfo providerInfo = cluster.select(request);
        // TODO providerInfo to endpoint
        Endpoint endpoint = null;
        return (RpcResponse) remotingClient.syncCall(request, endpoint);
    }
}
