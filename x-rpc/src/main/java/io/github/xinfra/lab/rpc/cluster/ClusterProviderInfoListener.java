package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.registry.ProviderInfo;
import io.github.xinfra.lab.rpc.registry.ProviderInfoListener;

import java.util.List;

public class ClusterProviderInfoListener implements ProviderInfoListener {
    private Cluster cluster;

    public ClusterProviderInfoListener(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public void addProviders(List<ProviderInfo> providerInfoList) {
        // TODO
    }

    @Override
    public void removeProviders(List<ProviderInfo> providerInfoList) {
        // TODO
    }
}
