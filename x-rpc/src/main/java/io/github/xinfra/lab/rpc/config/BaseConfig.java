package io.github.xinfra.lab.rpc.config;

import lombok.Data;

@Data
public abstract class BaseConfig {

    private ApplicationConfig applicationConfig;

    private ServiceDiscoveryConfig<?> serviceDiscoveryConfig;

}
