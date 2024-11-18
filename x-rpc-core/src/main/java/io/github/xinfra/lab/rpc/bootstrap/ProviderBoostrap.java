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

import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;
import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.config.ServerConfig;
import io.github.xinfra.lab.rpc.filter.FilterChainBuilder;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.invoker.ProviderInvoker;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.RegistryManager;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.transport.ServerTransport;
import io.github.xinfra.lab.rpc.transport.ServerTransportManager;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderBoostrap implements Closeable {
  private ProviderConfig providerConfig;

  private ServerTransportManager serverTransportManager = new ServerTransportManager();

  private RegistryManager registryManager = new RegistryManager();

  private List<ServiceInstance> serviceInstances;

  public ProviderBoostrap(ProviderConfig providerConfig) {
    this.providerConfig = providerConfig;
  }

  public static ProviderBoostrap form(ProviderConfig providerConfig) {
    return new ProviderBoostrap(providerConfig);
  }

  public void export(ExporterConfig<?> exporterConfig) {
    exporterConfig.setProviderConfig(providerConfig);

    Invoker providerInvoker = new ProviderInvoker(exporterConfig);
    Invoker filteringInvoker =
        FilterChainBuilder.buildFilterChainInvoker(providerConfig.getFilters(), providerInvoker);

    // start server
    Map<ServerConfig, ServiceInstance> serviceInstanceMap = new HashMap<>();
    for (ServerConfig serverConfig : providerConfig.getServerConfigs()) {
      exporterConfig.getProtocol().add(serverConfig.transportType().name());
      ServerTransport serverTransport = serverTransportManager.getServerTransport(serverConfig);
      serverTransport.register(exporterConfig, filteringInvoker);
      ServiceInstance serviceInstance =
          serviceInstanceMap.computeIfAbsent(
              serverConfig,
              config ->
                  new ServiceInstance(
                      providerConfig.getApplicationConfig().getAppName(),
                      config.host(),
                      config.port()));

      serviceInstance.getMetadataInfo().addService(exporterConfig);
    }

    // register
    RegistryConfig<?> registryConfig = providerConfig.getRegistryConfig();
    Registry registry = registryManager.getRegistry(registryConfig);
    for (ServiceInstance serviceInstance : serviceInstanceMap.values()) {
      // todo calc reversion ??
      registry.register(serviceInstance);
    }
  }

  public void unExport(ExporterConfig<?> exporterConfig) {
    // too
  }

  @Override
  public void close() throws IOException {
    // todo
  }
}
