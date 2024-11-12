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

import io.github.xinfra.lab.rpc.registry.AppServiceInstancesChanger;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

@Slf4j
public class ZookeeperServiceDiscoveryChangeWatcher implements ServiceCacheListener {

  private Registry registry;
  private String appName;
  @Getter private ServiceCache<ZookeeperInstancePayload> serviceCache;

  private AppServiceInstancesChanger appServiceInstancesChanger;

  private CountDownLatch countDownLatch;

  public ZookeeperServiceDiscoveryChangeWatcher(
      String appName,
      ServiceCache<ZookeeperInstancePayload> serviceCache,
      Registry registry,
      CountDownLatch countDownLatch,
      AppServiceInstancesChanger appServiceInstancesChanger) {
    this.appName = appName;
    this.serviceCache = serviceCache;
    this.registry = registry;
    this.countDownLatch = countDownLatch;
    this.appServiceInstancesChanger = appServiceInstancesChanger;
  }

  @Override
  public synchronized void cacheChanged() {
    try {
      countDownLatch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    List<ServiceInstance> serviceInstances = registry.queryServiceInstances(appName);
    try {
      appServiceInstancesChanger.change(serviceInstances);
    } catch (Exception e) {
      log.error("{} appServiceInstancesChanger change fail.", appName, e);
      throw new RuntimeException(appName + " appServiceInstancesChanger change fail.", e);
    }
  }

  @Override
  public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
    // ignore
  }
}
