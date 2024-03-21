package io.github.xinfra.lab.rpc.config;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class InterfaceConfig<T> {
    protected Class<T> interfaceId;
    protected List<RegistryConfig<?>> registryConfigs;

}
