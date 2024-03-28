package io.github.xinfra.lab.rpc.config;

import lombok.Data;

@Data
public class BaseConfig {

    private ApplicationConfig applicationConfig;

    private RegistryConfig<?> registryConfig;

}
