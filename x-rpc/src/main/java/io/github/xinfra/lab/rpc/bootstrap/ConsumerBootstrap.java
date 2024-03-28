package io.github.xinfra.lab.rpc.bootstrap;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.cluster.ClusterManager;
import io.github.xinfra.lab.rpc.cluster.Directory;
import io.github.xinfra.lab.rpc.cluster.RegisterDirectory;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.proxy.Proxy;
import io.github.xinfra.lab.rpc.proxy.ProxyManager;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.RegistryManager;

public class ConsumerBootstrap<T> {

    private final ConsumerConfig consumerConfig;

    private RegistryManager registryManager = new RegistryManager();

    private ConsumerBootstrap(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public static ConsumerBootstrap from(ConsumerConfig consumerConfig) {
        return new ConsumerBootstrap(consumerConfig);
    }


    public <T> T refer(ReferenceConfig<T> referenceConfig) {
        referenceConfig.setConsumerConfig(consumerConfig);

        RegistryConfig<?> registryConfig = consumerConfig.getRegistryConfig();
        Registry registry = registryManager.getRegistry(registryConfig);

        Directory directory = new RegisterDirectory(registry, referenceConfig);
        Cluster cluster = ClusterManager.getCluster(referenceConfig.getClusterType());
        ClusterInvoker clusterInvoker = cluster.filteringInvoker(directory);
        Proxy proxy = ProxyManager.getProxy(referenceConfig.getProxyType());
        return proxy.createProxyObject(referenceConfig.getServiceClass(), clusterInvoker);
    }
}
