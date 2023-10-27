package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.registry.ProviderInfo;

import java.util.List;

public class DefaultCluster implements Cluster{
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
    public void addProviders(List<ProviderInfo> providerInfoList) {
        // TODO
    }

    @Override
    public void removeProviders(List<ProviderInfo> providerInfoList) {
        // TODO
    }

    public void init() {
        // TODO
    }
}
