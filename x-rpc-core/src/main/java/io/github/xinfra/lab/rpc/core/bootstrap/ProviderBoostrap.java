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

import com.google.common.collect.Lists;
import io.github.xinfra.lab.rpc.common.Constants;
import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;
import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.core.filter.FilterChainBuilder;
import io.github.xinfra.lab.rpc.core.invoker.ProviderInvoker;
import io.github.xinfra.lab.rpc.core.metadata.MetadataServiceImpl;
import io.github.xinfra.lab.rpc.core.registry.RegistryManager;
import io.github.xinfra.lab.rpc.core.transport.ServerTransportManager;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.metadata.MetadataService;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.transport.ServerTransport;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProviderBoostrap implements Closeable {
  private static final Logger log = LoggerFactory.getLogger(ProviderBoostrap.class);
  private ProviderConfig providerConfig;

  private ServerTransportManager serverTransportManager = new ServerTransportManager();

  private RegistryManager registryManager = new RegistryManager();

  private AtomicBoolean metadataServiceExported = new AtomicBoolean(false);

  private List<ExporterConfig<?>> exportedExporterConfigs = new ArrayList<>();

  public ProviderBoostrap(ProviderConfig providerConfig) {
    Validate.notNull(providerConfig, "providerConfig must not be null");
    Validate.notNull(providerConfig.getApplicationConfig(), "applicationConfig must not be null");
    Validate.notBlank(
        providerConfig.getApplicationConfig().getAppName(), "appName must not be blank");
    Validate.notNull(providerConfig.getRegistryConfig(), "registryConfig must not be null");
    Validate.notNull(providerConfig.getProtocolConfig(), "protocolConfig must not be null");
    Validate.notNull(providerConfig.getFilters(), "filters must not be null");
    this.providerConfig = providerConfig;
    GracefulShutdown.INSTANCE.registerShutdownHook(this);
  }

  public static ProviderBoostrap from(ProviderConfig providerConfig) {
    return new ProviderBoostrap(providerConfig);
  }

  public synchronized void export(ExporterConfig<?> exporterConfig) {
    Validate.notNull(exporterConfig, "exporterConfig must not be null");
    Validate.notNull(
        exporterConfig.getServiceInterfaceClass(), "serviceInterfaceClass must not be null");
    Validate.notNull(exporterConfig.getServiceImpl(), "serviceImpl must not be null");
    exporterConfig.setProviderConfig(providerConfig);

    // build invoker
    Invoker providerInvoker = new ProviderInvoker(exporterConfig);
    Invoker filteringInvoker =
        FilterChainBuilder.buildFilterChainInvoker(providerConfig.getFilters(), providerInvoker);

    // start server
    ServerTransport serverTransport =
        serverTransportManager.getServerTransport(
            providerConfig.getProtocolConfig().transportConfig());
    serverTransport.register(exporterConfig, filteringInvoker);

    RegistryConfig<?> registryConfig = providerConfig.getRegistryConfig();
    Registry registry = registryManager.getRegistry(registryConfig);

    // init
    registry.initInstance(
        providerConfig.getApplicationConfig().getAppName(),
        providerConfig.getProtocolConfig().protocol(),
        serverTransport.address());

    ServiceInstance serviceInstance = registry.getServiceInstance();
    if (exporterConfig.getWeight() != 100) {
      serviceInstance
          .getProps()
          .put(Constants.WEIGHT_KEY, String.valueOf(exporterConfig.getWeight()));
    }
    if (exporterConfig.getWarmupMills() > 0) {
      serviceInstance
          .getProps()
          .put(Constants.WARMUP_KEY, String.valueOf(exporterConfig.getWarmupMills()));
    }

    // export metadata service
    if (metadataServiceExported.compareAndSet(false, true)) {
      exportMetadataService(serverTransport, registry.getServiceInstance());
    }

    for (ExporterConfig<?> existing : exportedExporterConfigs) {
      if (existing.getServiceInterfaceName().equals(exporterConfig.getServiceInterfaceName())) {
        throw new IllegalStateException(
            "duplicate export service: " + exporterConfig.getServiceInterfaceName());
      }
    }
    exportedExporterConfigs.add(exporterConfig);
    if (providerConfig.isAutoRegister()) {
      registry.register(Lists.newArrayList(exporterConfig));
    }
  }

  public synchronized void register() {
    if (exportedExporterConfigs.isEmpty()) {
      log.info("XRpc no service exported, skip registry register.");
      return;
    }
    RegistryConfig<?> registryConfig = providerConfig.getRegistryConfig();
    Registry registry = registryManager.getRegistry(registryConfig);
    registry.register(exportedExporterConfigs);
    log.info("XRpc {} service registered.", exportedExporterConfigs.size());
  }

  private void exportMetadataService(
      ServerTransport serverTransport, ServiceInstance serviceInstance) {
    ExporterConfig<MetadataService> exporterConfig = new ExporterConfig<>(MetadataService.class);
    exporterConfig.setServiceImpl(new MetadataServiceImpl(serviceInstance));
    Invoker providerInvoker = new ProviderInvoker(exporterConfig);
    serverTransport.register(exporterConfig, providerInvoker);
  }

  public synchronized void unExport(ExporterConfig<?> exporterConfig) {
    exportedExporterConfigs.remove(exporterConfig);

    ServerTransport serverTransport =
        serverTransportManager.getServerTransport(
            providerConfig.getProtocolConfig().transportConfig());
    serverTransport.unRegister(exporterConfig, null);

    RegistryConfig<?> registryConfig = providerConfig.getRegistryConfig();
    Registry registry = registryManager.getRegistry(registryConfig);
    ServiceInstance serviceInstance = registry.getServiceInstance();
    serviceInstance.removeService(exporterConfig);
    if (serviceInstance.isRevisionChanged()) {
      registry.update(serviceInstance);
    }
  }

  @Override
  public void close() throws IOException {
    IOException ex = null;
    try {
      registryManager.close();
    } catch (IOException e) {
      ex = e;
    }
    try {
      serverTransportManager.close();
    } catch (IOException e) {
      if (ex != null) {
        ex.addSuppressed(e);
      } else {
        ex = e;
      }
    }
    if (ex != null) {
      throw ex;
    }
  }
}
