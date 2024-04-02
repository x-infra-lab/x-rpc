package io.github.xinfra.lab.rpc.config;


import io.github.xinfra.lab.rpc.discovery.ServiceDiscoveryClientConfig;
import io.github.xinfra.lab.rpc.discovery.ServiceDiscoveryType;

public class ServiceDiscoveryConfig<CONFIG extends ServiceDiscoveryClientConfig> {

    private ServiceDiscoveryType serviceDiscoveryType = ServiceDiscoveryType.zookeeper;

    private CONFIG serviceDiscoveryClientConfig;

}
