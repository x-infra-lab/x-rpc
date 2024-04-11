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
package io.github.xinfra.lab.rpc.bootstrap;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.cluster.ClusterManager;
import io.github.xinfra.lab.rpc.cluster.Directory;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.proxy.Proxy;
import io.github.xinfra.lab.rpc.proxy.ProxyManager;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.RegistryDirectory;
import io.github.xinfra.lab.rpc.registry.RegistryManager;
import io.github.xinfra.lab.rpc.transport.ClientTransportManager;

public class ConsumerBootstrap<T> {

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

    RegistryConfig<?> registryConfig = consumerConfig.getRegistryConfig();
    Registry registry = registryManager.getRegistry(registryConfig);

    Directory directory = new RegistryDirectory(registry, referenceConfig);
    Cluster cluster = ClusterManager.getCluster(referenceConfig.getClusterType());
    ClusterInvoker clusterInvoker =
        cluster.filteringInvoker(referenceConfig, directory, clientTransportManager);
    Proxy proxy = ProxyManager.getProxy(referenceConfig.getProxyType());
    return proxy.createProxyObject(referenceConfig.getServiceClass(), clusterInvoker);
  }
}
