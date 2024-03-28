package io.github.xinfra.lab.rpc.proxy;

import io.github.xinfra.lab.rpc.invoker.Invoker;

public interface Proxy {

    <T> T createProxyObject(Class<T> serviceClass, Invoker invoker);

}
