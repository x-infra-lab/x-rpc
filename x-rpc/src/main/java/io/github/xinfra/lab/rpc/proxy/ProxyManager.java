package io.github.xinfra.lab.rpc.proxy;

import java.util.HashMap;
import java.util.Map;

public class ProxyManager {
    private static Map<ProxyType, Proxy> proxyMap = new HashMap<>();

    public synchronized static Proxy getProxy(ProxyType proxyType) {
        Proxy proxy = proxyMap.get(proxyType);
        if (proxy == null) {
            proxy = ProxyFactory.create(proxyType);
            proxyMap.put(proxyType, proxy);
        }

        return proxy;
    }
}
