package io.github.xinfra.lab.rpc.config;


import io.github.xinfra.lab.rpc.registry.RegistryClientConfig;
import io.github.xinfra.lab.rpc.registry.RegistryType;

public class RegistryConfig<CONFIG extends RegistryClientConfig> {

    private RegistryType type = RegistryType.zookeeper;

    private CONFIG registryClientConfig;

}
