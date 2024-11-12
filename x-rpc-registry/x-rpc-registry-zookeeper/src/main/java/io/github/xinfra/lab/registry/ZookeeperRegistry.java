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
import io.github.xinfra.lab.rpc.registry.AppServiceInstancesChanger;
import io.github.xinfra.lab.rpc.registry.NotifyListener;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class ZookeeperRegistry implements Registry {

  private static final Logger log = LoggerFactory.getLogger(ZookeeperRegistry.class);

  private ZookeeperRegistryConfig zookeeperRegistryConfig;

  private ZookeeperConfig zookeeperConfig;

  private Map<String, AppServiceInstancesChanger> changers = new ConcurrentHashMap<>();
  private Map<String, ZookeeperServiceDiscoveryChangeWatcher> watchers = new ConcurrentHashMap<>();
  private CuratorFramework curatorFramework;
  private ServiceDiscovery<ZookeeperInstancePayload> serviceDiscovery;

  public ZookeeperRegistry(RegistryConfig<?> registryConfig) {
    if (!(registryConfig instanceof ZookeeperRegistryConfig)) {
      throw new IllegalArgumentException("registryConfig must be ZookeeperRegistryConfig");
    }
    zookeeperRegistryConfig = (ZookeeperRegistryConfig) registryConfig;
    zookeeperConfig = zookeeperRegistryConfig.getRegistryClientConfig();

    try {
      curatorFramework =
          CuratorFrameworkFactory.newClient(
              zookeeperConfig.getZkAddress(),
              new RetryOneTime(zookeeperConfig.getSleepMsBetweenRetry()));
      curatorFramework.start();

      serviceDiscovery =
          ServiceDiscoveryBuilder.builder(ZookeeperInstancePayload.class)
              .basePath(zookeeperConfig.getBasePath())
              .client(curatorFramework)
              .build();
      serviceDiscovery.start();

      log.info("ZookeeperRegistry init successfully");
    } catch (Exception e) {
      log.error("ZookeeperRegistry init fail", e);
      CloseableUtils.closeQuietly(serviceDiscovery);
      CloseableUtils.closeQuietly(curatorFramework);
      throw new RuntimeException("ZookeeperRegistry start fail", e);
    }
  }

  @Override
  public void close() throws IOException {
    for (ZookeeperServiceDiscoveryChangeWatcher watcher : watchers.values()) {
      CloseableUtils.closeQuietly(watcher.getServiceCache());
    }
    CloseableUtils.closeQuietly(serviceDiscovery);
    CloseableUtils.closeQuietly(curatorFramework);
  }

  @Override
  public void register(ServiceInstance serviceInstance) {}

  @Override
  public void update(ServiceInstance serviceInstance) {}

  @Override
  public void unRegister(ServiceInstance serviceInstance) {}

  @Override
  public void subscribe(String appName, NotifyListener notifyListener) {
    AppServiceInstancesChanger appServiceInstancesChanger =
        changers.computeIfAbsent(
            appName,
            name -> {
              AppServiceInstancesChanger changer = new AppServiceInstancesChanger(appName);
              addAppServiceInstancesChanger(changer);
              return changer;
            });

    appServiceInstancesChanger.addNotifyListener(notifyListener);
  }

  @Override
  public void unSubscribe(String appName, NotifyListener notifyListener) {}

  @Override
  public List<ServiceInstance> queryServiceInstances(String appName) {
    try {
      Collection<org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload>>
          serviceInstances = serviceDiscovery.queryForInstances(appName);
      return serviceInstances.stream().map(InstanceConverter::convert).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("queryForInstances fail. appName:{}", appName, e);
      throw new RuntimeException("queryForInstances fail. appName:" + appName, e);
    }
  }

  @Override
  public void addAppServiceInstancesChanger(AppServiceInstancesChanger appServiceInstancesChanger) {

    CountDownLatch latch = new CountDownLatch(1);
    String appName = appServiceInstancesChanger.getAppName();
    watchers.computeIfAbsent(
        appName,
        name -> {
          ServiceCache<ZookeeperInstancePayload> serviceCache =
              serviceDiscovery.serviceCacheBuilder().name(name).build();
          ZookeeperServiceDiscoveryChangeWatcher w =
              new ZookeeperServiceDiscoveryChangeWatcher(
                  name, serviceCache, this, latch, appServiceInstancesChanger);
          serviceCache.addListener(w);

          try {
            serviceCache.start();
          } catch (Exception e) {
            log.error("subscribe fail. appName:{}", appName, e);
            throw new RuntimeException("subscribe fail. appName: " + name, e);
          }

          return w;
        });

    appServiceInstancesChanger.change(queryServiceInstances(appName));
    latch.countDown();
  }

  @Override
  public void removeAppServiceInstancesChanger(
      AppServiceInstancesChanger appServiceInstancesChanger) {
    // todo
  }
}
