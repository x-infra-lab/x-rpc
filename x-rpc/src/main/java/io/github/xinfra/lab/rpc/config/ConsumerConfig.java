package io.github.xinfra.lab.rpc.config;

import io.github.xinfra.lab.rpc.bootstrap.ConsumerBootstrap;
import io.github.xinfra.lab.rpc.cluster.ClusterType;
import io.github.xinfra.lab.rpc.proxy.ProxyType;
import lombok.Getter;

import java.util.List;


@Getter
public class ConsumerConfig<T> extends InterfaceConfig<T> {

    private ProxyType proxyType = ProxyType.JDK;
    private ClusterType clusterType = ClusterType.FastFail;

    private ConsumerBootstrap<T> bootstrap;

    public ConsumerConfig<T> interfaceId(Class<T> interfaceId) {
        this.interfaceId = interfaceId;
        return this;
    }

    public ConsumerConfig<T> registryConfig(List<RegistryConfig<?>> registryConfigs) {
        this.registryConfigs = registryConfigs;
        return this;
    }

    public ConsumerConfig<T> proxyType(ProxyType proxyType) {
        this.proxyType = proxyType;
        return this;
    }

    public ConsumerConfig<T> clusterType(ClusterType clusterType) {
        this.clusterType = clusterType;
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
