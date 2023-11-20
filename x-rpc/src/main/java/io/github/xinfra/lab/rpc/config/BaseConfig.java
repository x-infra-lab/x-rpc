package io.github.xinfra.lab.rpc.config;

import lombok.Getter;

@Getter
public abstract class BaseConfig<T> {

    protected Class<T> interfaceId;
    protected RegistryConfig<?> registryConfig;
}
