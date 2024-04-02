package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.discovery.ServiceDiscovery;

import java.util.List;

public class ServiceDiscoveryDirectory implements Directory{
    private ServiceDiscovery serviceDiscovery;
    private ReferenceConfig<?> referenceConfig;

    public ServiceDiscoveryDirectory(ServiceDiscovery serviceDiscovery, ReferenceConfig<?> referenceConfig) {
        this.serviceDiscovery = serviceDiscovery;
        this.referenceConfig = referenceConfig;
    }

    @Override
    public List<Invoker> list(Invocation invocation) {
        // todo
        return null;
    }
}
