package io.github.xinfra.lab.rpc.config;


import lombok.Getter;

@Getter
public class RegistryConfig<T> {


    public static enum RegistryType {
        ZOOKEEPER,
        SOFA_REGISTRY;
    }

    private RegistryType type;

    private T config;

    public RegistryConfig<T> type(RegistryType type) {
        this.type = type;
        return this;
    }

    public RegistryConfig<T> setConfig(T config) {
        this.config = config;
        return this;
    }
}
