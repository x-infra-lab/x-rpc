package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.registry.ProviderGroup;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.RegistryFactory;

import java.util.ArrayList;
import java.util.List;

public class ClusterFactory {

    public static Cluster create(ConsumerConfig<?> consumerConfig) {
        List<RegistryConfig<?>> registryConfigs = consumerConfig.getRegistryConfigs();

        List<Registry> registries = new ArrayList<>();
        for (RegistryConfig<?> registryConfig : registryConfigs) {
            Registry registry = RegistryFactory.create(registryConfig);
            registry.startup();

            registries.add(registry);
        }

        // todo
        Cluster cluster = new FastFailCluster(consumerConfig);
        cluster.startup();

        for (Registry registry : registries) {
            registry.addListener(cluster);
            // FIXME 并发问题
            List<ProviderGroup> providerGroups = registry.subscribe(consumerConfig);
            providerGroups.forEach(cluster::addProvider);
        }

        return cluster;
    }
}
