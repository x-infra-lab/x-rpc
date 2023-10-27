package io.github.xinfra.lab.rpc.proxy;


import io.github.xinfra.lab.rpc.config.ConsumerConfig;

public class ProxyFactory {
    public static <T> Proxy<T> getProxy(ConsumerConfig<T> config) {
        // TODO: SPI
        ProxyType proxyType = config.getProxyType();
        if (proxyType == null || proxyType == ProxyType.JDK) {
            return new JDKProxy<T>();
        }
        // can not reach here
        throw new UnsupportedOperationException("proxyType not support");
    }
}
