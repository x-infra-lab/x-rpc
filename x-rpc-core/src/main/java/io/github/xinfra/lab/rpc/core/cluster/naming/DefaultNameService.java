/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.xinfra.lab.rpc.core.cluster.naming;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.naming.NameService;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.exception.NoAvailableProviderException;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import io.github.xinfra.lab.rpc.transport.TransportEvent;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultNameService implements NameService {

  private ClientTransport clientTransport;

  private ReferenceConfig<?> referenceConfig;

  private Set<ServiceInstance> allServiceInstances = new HashSet<>();

  private Set<ServiceInstance> healthServiceInstances = new HashSet<>();

  private Set<ServiceInstance> unHealthServiceInstances = new HashSet<>();

  public DefaultNameService(Cluster cluster) {
    this.referenceConfig = cluster.referenceConfig();
    this.clientTransport = cluster.clientTransport();
    this.clientTransport.addTransportEventListener(this);
  }

  @Override
  public List<ServiceInstance> getInstances(Invocation invocation) {
    // copy
    ArrayList<ServiceInstance> availableInstances = new ArrayList<>(healthServiceInstances);
    if (availableInstances.isEmpty()) {
      throw new NoAvailableProviderException(
          "No available instance for invocation:"
              + invocation
              + ". Please check if the providers have been started and registered.");
    }
    return availableInstances;
  }

  @Override
  public ServiceConfig<?> serviceConfig() {
    return referenceConfig;
  }

  @Override
  public synchronized void notify(List<ServiceInstance> newServiceInstances) {
    // add new instance
    Set<ServiceInstance> addedServiceInstances = new HashSet<>();
    for (ServiceInstance newServiceInstance : newServiceInstances) {
      boolean found = false;
      for (ServiceInstance oldServiceInstance : allServiceInstances) {
        if (Objects.equals(newServiceInstance.getAddress(), oldServiceInstance.getAddress())
            && Objects.equals(newServiceInstance.getPort(), oldServiceInstance.getPort())) {
          found = true;
          break;
        }
      }
      if (!found) {
        addedServiceInstances.add(newServiceInstance);
      }
    }

    for (ServiceInstance serviceInstance : addedServiceInstances) {
      try {
        clientTransport.connect(serviceInstance.getSocketAddress());
        healthServiceInstances.add(serviceInstance);
      } catch (Exception e) {
        log.warn("connect serviceInstance:{} fail.", serviceInstance, e);
        unHealthServiceInstances.add(serviceInstance);
        clientTransport.reconnect(serviceInstance.getSocketAddress());
      }
    }

    // remove instance
    Set<ServiceInstance> removedServiceInstances = new HashSet<>();
    for (ServiceInstance oldServiceInstance : allServiceInstances) {
      boolean found = false;
      for (ServiceInstance newServiceInstance : newServiceInstances) {
        if (Objects.equals(newServiceInstance.getAddress(), oldServiceInstance.getAddress())
            && Objects.equals(newServiceInstance.getPort(), oldServiceInstance.getPort())) {
          found = true;
          break;
        }
      }
      if (!found) {
        removedServiceInstances.add(oldServiceInstance);
      }
    }

    healthServiceInstances.removeAll(removedServiceInstances);
    unHealthServiceInstances.removeAll(removedServiceInstances);
    for (ServiceInstance serviceInstance : removedServiceInstances) {
      clientTransport.disconnect(serviceInstance.getSocketAddress());
    }

    allServiceInstances = new HashSet<>(newServiceInstances);
  }

  @Override
  public synchronized void onEvent(TransportEvent event, InetSocketAddress socketAddress) {
    if (TransportEvent.CONNECT == event) {
      ServiceInstance targetServiceInstance = null;
      for (ServiceInstance serviceInstance : unHealthServiceInstances) {
        if (serviceInstance.getSocketAddress().equals(socketAddress)) {
          targetServiceInstance = serviceInstance;
          break;
        }
      }
      if (targetServiceInstance != null) {
        unHealthServiceInstances.remove(targetServiceInstance);
        healthServiceInstances.add(targetServiceInstance);
      }
    }

    if (TransportEvent.DISCONNECT == event) {
      ServiceInstance targetServiceInstance = null;
      for (ServiceInstance serviceInstance : healthServiceInstances) {
        if (serviceInstance.getSocketAddress().equals(socketAddress)) {
          targetServiceInstance = serviceInstance;
          break;
        }
      }
      if (targetServiceInstance != null) {
        healthServiceInstances.remove(targetServiceInstance);
        unHealthServiceInstances.add(targetServiceInstance);
      }
    }
  }
}
