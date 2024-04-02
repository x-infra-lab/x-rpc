package io.github.xinfra.lab.rpc.discovery;

import io.github.xinfra.lab.rpc.config.ServiceDiscoveryConfig;

import java.util.HashMap;
import java.util.Map;

public class ServiceDiscoveryManager {
    private Map<ServiceDiscoveryConfig<?>, ServiceDiscovery> serviceDiscoveryMap = new HashMap<>();

    public synchronized ServiceDiscovery getServiceDiscovery(ServiceDiscoveryConfig<?> serviceDiscoveryConfig) {
        ServiceDiscovery serviceDiscovery = serviceDiscoveryMap.get(serviceDiscoveryConfig);
        if (serviceDiscovery == null) {
            serviceDiscovery = ServiceDiscoveryFactory.create(serviceDiscoveryConfig);
            serviceDiscoveryMap.put(serviceDiscoveryConfig, serviceDiscovery);
        }
        return serviceDiscovery;
    }
}
