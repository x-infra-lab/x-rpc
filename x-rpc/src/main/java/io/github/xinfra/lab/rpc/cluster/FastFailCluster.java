package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.invoker.FailFastClusterInvoker;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.registry.ProviderGroup;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;
import io.github.xinfra.lab.rpc.transport.TransportManager;


public class FastFailCluster implements Cluster {
    private ConsumerConfig<?> config;

    private RouterChain routerChain;

    private LoadBalancer loadBalancer;


    public FastFailCluster(ConsumerConfig<?> config) {
        this.config = config;
    }

    @Override
    public Invoker invoker() {
        return new FailFastClusterInvoker(this, config);
    }

    @Override
    public ProviderInfo select(Invocation request) {
        // TODO
        return null;
    }

    @Override
    public TransportManager transportManager() {
        return null;
    }


    @Override
    public void addProvider(ProviderGroup providerGroup) {
        // TODO
    }

    @Override
    public void removeProvider(ProviderGroup providerGroup) {
        // TODO
    }

    @Override
    public void updateProvider(ProviderGroup providerGroup) {
        // TODO
    }

    @Override
    public void startup() {

        // TODO
    }

    @Override
    public void shutDown() {
        // TODO
    }
}
