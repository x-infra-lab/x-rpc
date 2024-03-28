package io.github.xinfra.lab.rpc.proxy;


public class ProxyFactory {
    public static Proxy create(ProxyType proxyType) {
        // todo
        return new JDKProxy();
    }
}
