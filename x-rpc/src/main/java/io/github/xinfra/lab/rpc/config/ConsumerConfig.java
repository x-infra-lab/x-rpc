package io.github.xinfra.lab.rpc.config;

import io.github.xinfra.lab.rpc.bootstrap.ConsumerBootstrap;
import io.github.xinfra.lab.rpc.proxy.ProxyType;
import io.github.xinfra.lab.rpc.registry.ProviderInfoListener;
import io.github.xinfra.lab.rpc.remoting.protocol.ProtocolType;
import io.github.xinfra.lab.rpc.remoting.serialization.SerializationType;
import lombok.Getter;


@Getter
public class ConsumerConfig<T> extends BaseConfig<T> {

    private ProxyType proxyType;

    private ProviderInfoListener providerInfoListener;

    private ConsumerBootstrap<T> bootstrap;

    public ConsumerConfig<T> interfaceId(Class<T> interfaceId) {
        this.interfaceId = interfaceId;
        return this;
    }

    public ConsumerConfig<T> protocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
        return this;
    }

    public ConsumerConfig<T> serializationType(SerializationType serializationType) {
        this.serializationType = serializationType;
        return this;
    }

    public ConsumerConfig<T> registryConfig(RegistryConfig<?> registryConfig) {
        this.registryConfig = registryConfig;
        return this;
    }

    public ConsumerConfig<T> proxyType(ProxyType proxyType) {
        this.proxyType = proxyType;
        return this;
    }

    public ConsumerConfig<T> providerInfoListener(ProviderInfoListener providerInfoListener) {
        this.providerInfoListener = providerInfoListener;
        return this;
    }

    public T refer() {
        this.bootstrap = new ConsumerBootstrap<>(this);
        return bootstrap.refer();
    }

    public void unRefer() {
        if (this.bootstrap != null) {
            bootstrap.unRefer();
        }
    }
}
