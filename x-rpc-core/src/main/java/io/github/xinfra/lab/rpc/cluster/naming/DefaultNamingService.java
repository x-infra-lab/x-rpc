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
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import io.github.xinfra.lab.rpc.transport.TransportEvent;
import java.net.InetSocketAddress;
import java.util.List;

public class DefaultNamingService implements NamingService {
  private ClientTransport clientTransport;

  private ReferenceConfig<?> referenceConfig;

  public DefaultNamingService(Cluster cluster) {
    this.referenceConfig = cluster.referenceConfig();
    this.clientTransport = cluster.clientTransport();
  }

  @Override
  public List<ServiceInstance> queryService(ServiceConfig<?> serviceConfig) {
    return null;
  }

  // notifyListener
  @Override
  public ServiceConfig<?> serviceConfig() {
    return referenceConfig;
  }

  @Override
  public void notify(List<ServiceInstance> serviceInstances) {}

  // notifyListener

  // TransportEventListener
  @Override
  public void onEvent(TransportEvent event, InetSocketAddress socketAddress) {}

  // TransportEventListener
}
