package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.filter.FilterChainBuilder;

public class ConsumerProxyClusterInvoker implements ClusterInvoker {

    private ConsumerConfig<?> config;

    private Cluster cluster;

    private ClusterInvoker filterClusterInvoker;

    public ConsumerProxyClusterInvoker(ConsumerConfig<?> config, Cluster cluster) {
        this.config = config;
        this.cluster = cluster;
        this.filterClusterInvoker =
                FilterChainBuilder.buildClusterFilterChainInvoker(config, cluster.invoker());
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        return filterClusterInvoker.invoke(invocation);
    }

    @Override
    public Cluster cluster() {
        return cluster;
    }
}
