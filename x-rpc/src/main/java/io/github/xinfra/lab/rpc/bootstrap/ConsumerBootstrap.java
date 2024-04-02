package io.github.xinfra.lab.rpc.bootstrap;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.cluster.ClusterManager;
import io.github.xinfra.lab.rpc.cluster.Directory;
import io.github.xinfra.lab.rpc.cluster.ServiceDiscoveryDirectory;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.ServiceDiscoveryConfig;
import io.github.xinfra.lab.rpc.proxy.Proxy;
import io.github.xinfra.lab.rpc.proxy.ProxyManager;
import io.github.xinfra.lab.rpc.discovery.ServiceDiscovery;
import io.github.xinfra.lab.rpc.discovery.ServiceDiscoveryManager;

public class ConsumerBootstrap<T> {

    private final ConsumerConfig consumerConfig;

    private ServiceDiscoveryManager serviceDiscoveryManager = new ServiceDiscoveryManager();

    private ConsumerBootstrap(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public static ConsumerBootstrap from(ConsumerConfig consumerConfig) {
        return new ConsumerBootstrap(consumerConfig);
    }


    public <T> T refer(ReferenceConfig<T> referenceConfig) {
        referenceConfig.setConsumerConfig(consumerConfig);

        ServiceDiscoveryConfig<?> serviceDiscoveryConfig = consumerConfig.getServiceDiscoveryConfig();
        ServiceDiscovery serviceDiscovery = serviceDiscoveryManager.getServiceDiscovery(serviceDiscoveryConfig);

        Directory directory = new ServiceDiscoveryDirectory(serviceDiscovery, referenceConfig);
        Cluster cluster = ClusterManager.getCluster(referenceConfig.getClusterType());
        ClusterInvoker clusterInvoker = cluster.filteringInvoker(directory);
        Proxy proxy = ProxyManager.getProxy(referenceConfig.getProxyType());
        return proxy.createProxyObject(referenceConfig.getServiceClass(), clusterInvoker);
    }
}
