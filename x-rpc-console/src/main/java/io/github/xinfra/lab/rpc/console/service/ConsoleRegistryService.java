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
package io.github.xinfra.lab.rpc.console.service;

import io.github.xinfra.lab.rpc.console.config.ConsoleConfig;
import io.github.xinfra.lab.rpc.console.model.AppInfo;
import io.github.xinfra.lab.rpc.console.model.InstanceVO;
import io.github.xinfra.lab.rpc.console.model.ServiceVO;
import io.github.xinfra.lab.rpc.registry.zookeeper.ZookeeperInstancePayload;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsoleRegistryService implements Closeable {

  private final ConsoleConfig config;
  private CuratorFramework curatorFramework;
  private ServiceDiscovery<ZookeeperInstancePayload> serviceDiscovery;

  public ConsoleRegistryService(ConsoleConfig config) {
    this.config = config;
  }

  @PostConstruct
  public void init() throws Exception {
    curatorFramework =
        CuratorFrameworkFactory.newClient(config.getZkAddress(), new RetryOneTime(1000));
    curatorFramework.start();
    curatorFramework.blockUntilConnected();

    serviceDiscovery =
        ServiceDiscoveryBuilder.builder(ZookeeperInstancePayload.class)
            .basePath(config.getBasePath())
            .client(curatorFramework)
            .build();
    serviceDiscovery.start();
    log.info("Console connected to ZooKeeper: {}", config.getZkAddress());
  }

  public List<AppInfo> listApps() throws Exception {
    Collection<String> names = serviceDiscovery.queryForNames();
    List<AppInfo> apps = new ArrayList<>();
    for (String name : names) {
      AppInfo app = new AppInfo();
      app.setAppName(name);
      app.setInstanceCount(serviceDiscovery.queryForInstances(name).size());
      apps.add(app);
    }
    return apps;
  }

  public List<InstanceVO> listInstances(String appName) throws Exception {
    Collection<ServiceInstance<ZookeeperInstancePayload>> instances =
        serviceDiscovery.queryForInstances(appName);
    List<InstanceVO> result = new ArrayList<>();
    for (ServiceInstance<ZookeeperInstancePayload> inst : instances) {
      result.add(convertInstance(inst));
    }
    return result;
  }

  public InstanceVO getInstance(String appName, String instanceId) throws Exception {
    ServiceInstance<ZookeeperInstancePayload> inst =
        serviceDiscovery.queryForInstance(appName, instanceId);
    if (inst == null) {
      return null;
    }
    return convertInstance(inst);
  }

  public void disableInstance(String appName, String instanceId) throws Exception {
    ServiceInstance<ZookeeperInstancePayload> inst =
        serviceDiscovery.queryForInstance(appName, instanceId);
    if (inst == null) {
      throw new IllegalArgumentException("Instance not found: " + appName + "/" + instanceId);
    }
    ServiceInstance<ZookeeperInstancePayload> updated =
        ServiceInstance.<ZookeeperInstancePayload>builder()
            .name(inst.getName())
            .id(inst.getId())
            .address(inst.getAddress())
            .port(inst.getPort())
            .payload(inst.getPayload())
            .enabled(false)
            .build();
    serviceDiscovery.updateService(updated);
    log.info("Disabled instance: {}/{}", appName, instanceId);
  }

  public void enableInstance(String appName, String instanceId) throws Exception {
    ServiceInstance<ZookeeperInstancePayload> inst =
        serviceDiscovery.queryForInstance(appName, instanceId);
    if (inst == null) {
      throw new IllegalArgumentException("Instance not found: " + appName + "/" + instanceId);
    }
    ServiceInstance<ZookeeperInstancePayload> updated =
        ServiceInstance.<ZookeeperInstancePayload>builder()
            .name(inst.getName())
            .id(inst.getId())
            .address(inst.getAddress())
            .port(inst.getPort())
            .payload(inst.getPayload())
            .enabled(true)
            .build();
    serviceDiscovery.updateService(updated);
    log.info("Enabled instance: {}/{}", appName, instanceId);
  }

  public List<ServiceVO> listServices() throws Exception {
    Map<String, ServiceVO> serviceMap = new HashMap<>();
    Collection<String> appNames = serviceDiscovery.queryForNames();

    for (String appName : appNames) {
      Collection<ServiceInstance<ZookeeperInstancePayload>> instances =
          serviceDiscovery.queryForInstances(appName);
      for (ServiceInstance<ZookeeperInstancePayload> inst : instances) {
        ZookeeperInstancePayload payload = inst.getPayload();
        if (payload == null) {
          continue;
        }
        InstanceVO instanceVO = convertInstance(inst);

        String serviceName = appName;
        ServiceVO svc = serviceMap.computeIfAbsent(serviceName, k -> {
          ServiceVO s = new ServiceVO();
          s.setInterfaceName(k);
          s.setProviders(new ArrayList<>());
          return s;
        });
        svc.getProviders().add(instanceVO);
        svc.setProviderCount(svc.getProviders().size());
      }
    }
    return new ArrayList<>(serviceMap.values());
  }

  public List<InstanceVO> getServiceProviders(String appName) throws Exception {
    return listInstances(appName);
  }

  private InstanceVO convertInstance(ServiceInstance<ZookeeperInstancePayload> inst) {
    InstanceVO vo = new InstanceVO();
    vo.setId(inst.getId());
    vo.setAppName(inst.getName());
    vo.setAddress(inst.getAddress());
    vo.setPort(inst.getPort());
    vo.setEnabled(inst.isEnabled());
    vo.setRegistrationTimestamp(inst.getRegistrationTimeUTC());

    ZookeeperInstancePayload payload = inst.getPayload();
    if (payload != null) {
      vo.setRevision(payload.getRevision());
      vo.setProtocol(payload.getProtocol());
      vo.setProps(payload.getProps());
    }
    return vo;
  }

  @PreDestroy
  @Override
  public void close() throws IOException {
    CloseableUtils.closeQuietly(serviceDiscovery);
    CloseableUtils.closeQuietly(curatorFramework);
    log.info("Console disconnected from ZooKeeper");
  }
}
