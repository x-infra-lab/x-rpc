package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.registry.ProviderGroup;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;


public class DefaultCluster implements Cluster {
    private ConsumerConfig<?> config;

    private RouterChain routerChain;

    private LoadBalancer loadBalancer;


    public DefaultCluster(ConsumerConfig<?> config) {
        this.config = config;
    }

    @Override
    public ProviderInfo select(RpcRequest request) {
        // TODO
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
        config.providerInfoListener(new ClusterProviderInfoListener(this));

        // TODO
    }

    @Override
    public void shutDown() {
        // TODO
    }
}
