package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.registry.ProviderGroup;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;


public class DefaultCluster implements Cluster {
    private ConsumerConfig<?> config;

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
        // TODO
    }

    @Override
    public void shutDown() {
        // TODO
    }
}
