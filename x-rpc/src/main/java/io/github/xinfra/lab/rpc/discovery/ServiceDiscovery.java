package io.github.xinfra.lab.rpc.discovery;

import io.github.xinfra.lab.rpc.common.LifeCycle;

import java.util.List;


public interface ServiceDiscovery extends LifeCycle {

    void register(ServiceInstance serviceInstance);

    void update(ServiceInstance serviceInstance);

    void unRegister(ServiceInstance serviceInstance);

    void subscribe(String serviceName);

    void unSubscribe(String serviceName);

    List<ServiceInstance> queryServiceInstances(String serviceName);

    void addServiceInstancesChangedListener(ServiceInstancesChangedListener listener);

    void removeServiceInstancesChangedListener(ServiceInstancesChangedListener listener);

}
