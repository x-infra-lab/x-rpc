package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.exception.RpcException;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;
import io.github.xinfra.lab.rpc.transport.ClientTransport;


public class FailFastClusterInvoker implements Invoker {
    private Cluster cluster;
    private ConsumerConfig<?> config;

    public FailFastClusterInvoker(Cluster cluster, ConsumerConfig<?> config) {
        this.cluster = cluster;
        this.config = config;
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        ProviderInfo providerInfo = cluster.select(invocation);
        ClientTransport client = cluster.transportManager().getClient(providerInfo);
        if (client == null) {
            // TODO
            throw new RuntimeException();
        }
        return client.invokeAsync(invocation);
    }
}
