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
package io.github.xinfra.lab.rpc.core.bootstrap;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.core.cluster.ClusterFactory;
import io.github.xinfra.lab.rpc.core.filter.FilterChainBuilder;
import io.github.xinfra.lab.rpc.core.invoker.ConsumerInvoker;
import io.github.xinfra.lab.rpc.core.invoker.DirectConnectInvoker;
import io.github.xinfra.lab.rpc.core.proxy.ProxyManager;
import io.github.xinfra.lab.rpc.core.registry.DefaultAppServiceInstancesWatcher;
import io.github.xinfra.lab.rpc.core.registry.RegistryManager;
import io.github.xinfra.lab.rpc.core.transport.ClientTransportManager;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.proxy.Proxy;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import java.io.Closeable;
import java.io.IOException;

public class ConsumerBootstrap implements Closeable {

  private final ConsumerConfig consumerConfig;

  private RegistryManager registryManager = new RegistryManager();

  private ClientTransportManager clientTransportManager = new ClientTransportManager();

  private ConsumerBootstrap(ConsumerConfig consumerConfig) {
    this.consumerConfig = consumerConfig;
  }

  public static ConsumerBootstrap from(ConsumerConfig consumerConfig) {
    return new ConsumerBootstrap(consumerConfig);
  }

  public <T> T refer(ReferenceConfig<T> referenceConfig) {
    referenceConfig.setConsumerConfig(consumerConfig);

    // build client transport
    // now share clientTransport
    ClientTransport clientTransport =
        clientTransportManager.getClientTransport(
            consumerConfig.getProtocolConfig().transportConfig());

    // build proxy
    Proxy proxy = ProxyManager.getProxy(referenceConfig.getProxyType());

    if (referenceConfig.getDirectAddress() != null) {
      Invoker filteringInvoker =
          FilterChainBuilder.buildFilterChainInvoker(
              referenceConfig.getConsumerConfig().getFilters(),
              new ConsumerInvoker(referenceConfig, clientTransport));
      DirectConnectInvoker directConnectInvoker =
          new DirectConnectInvoker(referenceConfig.getDirectAddress(), filteringInvoker);
      return proxy.createProxyObject(
          referenceConfig.getServiceInterfaceClass(), directConnectInvoker);
    } else {
      // build cluster
      Cluster cluster = ClusterFactory.create(referenceConfig, clientTransport);

      // cluster subscribe
      RegistryConfig<?> registryConfig = consumerConfig.getRegistryConfig();
      Registry registry = registryManager.getRegistry(registryConfig);
      registry.addAppServiceInstancesWatcher(
          new DefaultAppServiceInstancesWatcher(referenceConfig.getAppName()));
      registry.subscribe(referenceConfig.getAppName(), cluster.nameService());

      // build invoker & proxy
      ClusterInvoker clusterInvoker = cluster.filteringInvoker();
      return proxy.createProxyObject(referenceConfig.getServiceInterfaceClass(), clusterInvoker);
    }
  }

  public void unRefer(ReferenceConfig<?> referenceConfig) {
    // todo
  }

  @Override
  public void close() throws IOException {
    registryManager.close();
    clientTransportManager.close();
  }
}
