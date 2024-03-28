package io.github.xinfra.lab.rpc.registry;

import io.github.xinfra.lab.rpc.common.LifeCycle;


public interface Registry extends LifeCycle {

    void register(ServiceInstance serviceInstance);

    void unRegister(ServiceInstance serviceInstance);

    void subscribe(String serviceName);

    void unSubscribe(String serviceName);

}
