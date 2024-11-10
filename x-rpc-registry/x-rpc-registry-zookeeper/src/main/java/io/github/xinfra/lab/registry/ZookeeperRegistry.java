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
package io.github.xinfra.lab.registry;

import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.registry.NotifyListener;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.registry.ServiceInstancesChangedListener;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class ZookeeperRegistry implements Registry {
  private static final Logger log = LoggerFactory.getLogger(ZookeeperRegistry.class);
  private ZookeeperRegistryConfig zookeeperRegistryConfig;

  private ZookeeperConfig zookeeperConfig;

  public ZookeeperRegistry(RegistryConfig<?> registryConfig) {
    if (!(registryConfig instanceof ZookeeperRegistryConfig)) {
      throw new IllegalArgumentException("registryConfig must be ZookeeperRegistryConfig");
    }
    zookeeperRegistryConfig = (ZookeeperRegistryConfig) registryConfig;
    zookeeperConfig = zookeeperRegistryConfig.getRegistryClientConfig();
    init();
  }

  private void init() {
    CuratorFramework client = null;
    ServiceDiscovery<String> discovery = null;
    try {
      client =
          CuratorFrameworkFactory.newClient(
              zookeeperConfig.getZkAddress(),
              new RetryOneTime(zookeeperConfig.getSleepMsBetweenRetry()));
      client.start();

      discovery =
          ServiceDiscoveryBuilder.builder(String.class)
              .basePath(zookeeperConfig.getBasePath())
              .client(client)
              .build();
      discovery.start();

      // todo ServiceCache
      log.info("ZookeeperRegistry started successfully");
    } catch (Exception e) {
      log.error("ZookeeperRegistry start fail", e);
      CloseableUtils.closeQuietly(discovery);
      CloseableUtils.closeQuietly(client);
      throw new RuntimeException("ZookeeperRegistry start fail", e);
    }
  }

  @Override
  public void close() throws IOException {
    // todo
  }

  @Override
  public void register(ServiceInstance serviceInstance) {}

  @Override
  public void update(ServiceInstance serviceInstance) {}

  @Override
  public void unRegister(ServiceInstance serviceInstance) {}

  @Override
  public void subscribe(String serviceName, NotifyListener notifyListener) {}

  @Override
  public void unSubscribe(String serviceName, NotifyListener notifyListener) {}

  @Override
  public List<ServiceInstance> queryServiceInstances(String serviceName) {
    return null;
  }

  @Override
  public void addServiceInstancesChangedListener(
      ServiceInstancesChangedListener instancesChangedListener) {}

  @Override
  public void removeServiceInstancesChangedListener(
      ServiceInstancesChangedListener instancesChangedListener) {}
}
