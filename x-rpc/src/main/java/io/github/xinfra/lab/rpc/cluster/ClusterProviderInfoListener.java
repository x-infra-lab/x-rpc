package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.registry.ProviderGroup;
import io.github.xinfra.lab.rpc.registry.ProviderInfoListener;


public class ClusterProviderInfoListener implements ProviderInfoListener {
    private Cluster cluster;

    public ClusterProviderInfoListener(Cluster cluster) {
        this.cluster = cluster;
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
}
