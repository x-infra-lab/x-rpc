package io.github.xinfra.lab.rpc.proxy;

import io.github.xinfra.lab.rpc.invoker.Invoker;

public interface Proxy<T> {

    T getObject(Class<T> interfaceId, Invoker invoker);

}
