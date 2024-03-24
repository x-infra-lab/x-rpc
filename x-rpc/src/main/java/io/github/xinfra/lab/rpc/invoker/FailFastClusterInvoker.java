package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;


public class FailFastClusterInvoker implements ClusterInvoker {
    private Cluster cluster;
    private ConsumerConfig<?> config;

    public FailFastClusterInvoker(Cluster cluster, ConsumerConfig<?> config) {
        this.cluster = cluster;
        this.config = config;
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        Invoker invoker = cluster.select(invocation);
        return invoker.invoke(invocation);
    }

    @Override
    public Cluster cluster() {
        return cluster;
    }
}
