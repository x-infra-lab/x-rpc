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
package io.github.xinfra.lab.rpc.cluster.naming;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.router.RouterChain;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import io.github.xinfra.lab.rpc.transport.TransportEvent;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultNameService implements NameService {

  private ClientTransport clientTransport;

  private ReferenceConfig<?> referenceConfig;

  private RouterChain routerChain;

  private List<ServiceInstance> allServiceInstances = new ArrayList<>();

  private List<ServiceInstance> healthServiceInstances = new ArrayList<>();

  private List<ServiceInstance> unHealthServiceInstances = new ArrayList<>();

  public DefaultNameService(Cluster cluster) {
    this.referenceConfig = cluster.referenceConfig();
    this.clientTransport = cluster.clientTransport();
    this.routerChain = referenceConfig.getConsumerConfig().getRouterChain();
  }

  @Override
  public List<ServiceInstance> getInstances(Invocation invocation) {
    // copy
    ArrayList<ServiceInstance> copiedInstances = new ArrayList<>(healthServiceInstances);
    List<ServiceInstance> availableInstances = routerChain.route(invocation, copiedInstances);
    if (availableInstances.isEmpty()) {
      //      throw new RpcException("");
    }
    // todo
    return null;
  }

  // notifyListener
  @Override
  public ServiceConfig<?> serviceConfig() {
    return referenceConfig;
  }

  @Override
  public synchronized void notify(List<ServiceInstance> serviceInstances) {
    for (ServiceInstance serviceInstance : serviceInstances) {
      if (allServiceInstances.contains(serviceInstance)) {
        // add new instance
        allServiceInstances.add(serviceInstance);
        try {
          clientTransport.connect(serviceInstance.getSocketAddress());
          healthServiceInstances.add(serviceInstance);
        } catch (Exception e) {
          log.warn("connect serviceInstance:{} fail.", serviceInstance, e);
          unHealthServiceInstances.add(serviceInstance);
          clientTransport.reconnect(serviceInstance.getSocketAddress());
        }
      }
    }

    for (ServiceInstance serviceInstance : allServiceInstances) {
      if (!serviceInstances.contains(serviceInstance)) {
        // remove old instance
        healthServiceInstances.remove(serviceInstance);
        unHealthServiceInstances.remove(serviceInstance);
        clientTransport.disconnect(serviceInstance.getSocketAddress());
      }
    }
  }

  // notifyListener

  // TransportEventListener
  @Override
  public void onEvent(TransportEvent event, InetSocketAddress socketAddress) {}

  // TransportEventListener
}
