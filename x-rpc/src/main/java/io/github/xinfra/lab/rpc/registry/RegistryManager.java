package io.github.xinfra.lab.rpc.registry;

import io.github.xinfra.lab.rpc.config.RegistryConfig;

import java.util.HashMap;
import java.util.Map;

public class RegistryManager {
    private Map<RegistryConfig<?>, Registry> registryMap = new HashMap<>();

    public synchronized Registry getRegistry(RegistryConfig<?> registryConfig) {
        Registry registry = registryMap.get(registryConfig);
        if (registry == null) {
            registry = RegistryFactory.create(registryConfig);
            registryMap.put(registryConfig, registry);
        }
        return registry;
    }
}
