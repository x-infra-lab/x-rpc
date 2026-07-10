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
package io.github.xinfra.lab.rpc.registry.zookeeper;

import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.exception.RegistryException;
import io.github.xinfra.lab.rpc.registry.AppServiceInstancesWatcher;
import io.github.xinfra.lab.rpc.registry.NotifyListener;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperRegistry implements Registry {

  private static final Logger log = LoggerFactory.getLogger(ZookeeperRegistry.class);

  private ZookeeperRegistryConfig zookeeperRegistryConfig;
  private ZookeeperConfig zookeeperConfig;

  private AtomicBoolean instanceInited = new AtomicBoolean(false);
  private AtomicBoolean instanceRegistered = new AtomicBoolean(false);

  private ServiceInstance serviceInstance;

  private Map<String, ZookeeperServiceDiscoveryChangeWatcher> watchers = new ConcurrentHashMap<>();
  private CuratorFramework curatorFramework;
  private ServiceDiscovery<ZookeeperInstancePayload> serviceDiscovery;

  public ZookeeperRegistry(RegistryConfig<?> registryConfig) {
    if (!(registryConfig instanceof ZookeeperRegistryConfig)) {
      throw new IllegalArgumentException("registryConfig must be ZookeeperRegistryConfig");
    }
    zookeeperRegistryConfig = (ZookeeperRegistryConfig) registryConfig;
    zookeeperConfig = zookeeperRegistryConfig.getRegistryClientConfig();
    if (zookeeperConfig == null) {
      throw new IllegalArgumentException("zookeeperConfig must not be null");
    }
    if (zookeeperConfig.getZkAddress() == null || zookeeperConfig.getZkAddress().trim().isEmpty()) {
      throw new IllegalArgumentException("zkAddress must not be blank");
    }

    try {
      curatorFramework =
          CuratorFrameworkFactory.newClient(
              zookeeperConfig.getZkAddress(),
              new ExponentialBackoffRetry(
                  zookeeperConfig.getSleepMsBetweenRetry(), zookeeperConfig.getMaxRetries()));
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
      throw new RegistryException("ZookeeperRegistry start fail", e);
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
  public void initInstance(String appName, String protocol, InetSocketAddress address) {
    if (instanceInited.compareAndSet(false, true)) {
      this.serviceInstance = new ServiceInstance(appName, protocol, address);
    }
  }

  @Override
  public ServiceInstance getServiceInstance() {
    return this.serviceInstance;
  }

  private void registerInstance() throws Exception {
    serviceDiscovery.registerService(InstanceConverter.convert(serviceInstance));
  }

  private void updateInstance() throws Exception {
    serviceDiscovery.updateService(InstanceConverter.convert(serviceInstance));
  }

  @Override
  public void register(List<? extends ServiceConfig<?>> serviceConfigs) {
    try {
      serviceConfigs.forEach(serviceConfig -> serviceInstance.addService(serviceConfig));
      boolean changed = serviceInstance.isRevisionChanged();
      if (changed) {
        if (instanceRegistered.compareAndSet(false, true)) {
          registerInstance();
        } else {
          updateInstance();
        }
      }
    } catch (Exception e) {
      log.error("register service failed", e);
      throw new RegistryException("register service failed", e);
    }
  }

  @Override
  public void update(ServiceInstance serviceInstance) {
    try {
      if (instanceRegistered.get()) {
        updateInstance();
      }
    } catch (Exception e) {
      log.error("update service instance failed", e);
      throw new RegistryException("update service instance failed", e);
    }
  }

  @Override
  public void unRegister(ServiceInstance serviceInstance) {
    try {
      serviceDiscovery.unregisterService(InstanceConverter.convert(serviceInstance));
      instanceRegistered.set(false);
    } catch (Exception e) {
      log.error("unregister service failed", e);
      throw new RegistryException("unregister service failed", e);
    }
  }

  @Override
  public void subscribe(String appName, NotifyListener notifyListener) {
    if (!watchers.containsKey(appName)) {
      throw new RegistryException("you need addAppServiceInstancesWatcher first");
    }
    AppServiceInstancesWatcher appServiceInstancesWatcher =
        watchers.get(appName).getAppServiceInstancesWatcher();

    appServiceInstancesWatcher.addNotifyListener(notifyListener);
  }

  @Override
  public void unSubscribe(String appName, NotifyListener notifyListener) {
    ZookeeperServiceDiscoveryChangeWatcher watcher = watchers.get(appName);
    if (watcher != null) {
      watcher.getAppServiceInstancesWatcher().removeNotifyListener(notifyListener);
    }
  }

  @Override
  public List<ServiceInstance> queryServiceInstances(String appName) {
    try {
      Collection<org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload>>
          serviceInstances = serviceDiscovery.queryForInstances(appName);
      return serviceInstances.stream().map(InstanceConverter::convert).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("queryForInstances fail. appName:{}", appName, e);
      throw new RegistryException("queryForInstances fail. appName:" + appName, e);
    }
  }

  @Override
  public synchronized void addAppServiceInstancesWatcher(
      AppServiceInstancesWatcher appServiceInstancesWatcher) {
    String appName = appServiceInstancesWatcher.getAppName();

    if (watchers.containsKey(appName)) {
      return;
    }

    CountDownLatch latch = new CountDownLatch(1);
    ServiceCache<ZookeeperInstancePayload> serviceCache =
        serviceDiscovery.serviceCacheBuilder().name(appName).build();
    ZookeeperServiceDiscoveryChangeWatcher w =
        new ZookeeperServiceDiscoveryChangeWatcher(
            appName, serviceCache, this, latch, appServiceInstancesWatcher);
    serviceCache.addListener(w);

    try {
      serviceCache.start();
    } catch (Exception e) {
      CloseableUtils.closeQuietly(serviceCache);
      log.error("subscribe fail. appName:{}", appName, e);
      throw new RegistryException("subscribe fail. appName: " + appName, e);
    }

    watchers.put(appName, w);
    appServiceInstancesWatcher.change(queryServiceInstances(appName));
    latch.countDown();
  }

  @Override
  public synchronized void removeAppServiceInstancesWatcher(
      AppServiceInstancesWatcher appServiceInstancesWatcher) {
    String appName = appServiceInstancesWatcher.getAppName();
    ZookeeperServiceDiscoveryChangeWatcher watcher = watchers.remove(appName);
    if (watcher != null) {
      CloseableUtils.closeQuietly(watcher.getServiceCache());
    }
  }
}
