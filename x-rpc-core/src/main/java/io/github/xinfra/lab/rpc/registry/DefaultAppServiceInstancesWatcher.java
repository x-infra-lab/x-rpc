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
package io.github.xinfra.lab.rpc.registry;

import com.google.common.collect.Lists;
import io.github.xinfra.lab.rpc.common.ServiceMatcher;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.metadata.MetadataInfo;
import io.github.xinfra.lab.rpc.metadata.Metadatas;
import io.github.xinfra.lab.rpc.metadata.ServiceInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** app level service instances watcher */
@Slf4j
public class DefaultAppServiceInstancesWatcher implements AppServiceInstancesWatcher {

  @Getter private String appName;

  private List<NotifyListener> notifyListeners = new ArrayList<>();

  /** key: interface name value: service info <--> instances */
  private volatile Map<String, Map<ServiceInfo, List<ServiceInstance>>> serviceToInstancesMap =
      new HashMap<>();

  public DefaultAppServiceInstancesWatcher(String appName) {
    this.appName = appName;
  }

  public synchronized void change(List<ServiceInstance> serviceInstances) {
    log.info("app: {} service instances changed: {}", appName, serviceInstances);

    Map<String, Map<ServiceInfo, List<ServiceInstance>>> newServiceToInstancesMap = new HashMap<>();

    Map<String, List<ServiceInstance>> revisionToInstancesMap = new HashMap<>();
    serviceInstances.forEach(
        serviceInstance -> {
          revisionToInstancesMap
              .computeIfAbsent(serviceInstance.getRevision(), x -> new ArrayList<>())
              .add(serviceInstance);
        });
    for (Map.Entry<String, List<ServiceInstance>> entry : revisionToInstancesMap.entrySet()) {
      String revision = entry.getKey();
      List<ServiceInstance> subInstances = entry.getValue();

      // todo
      MetadataInfo metadataInfo =
          subInstances.stream()
              .map(ServiceInstance::getMetadataInfo)
              .filter(Objects::nonNull)
              .findFirst()
              .orElse(Metadatas.getMetadataInfo(revision, subInstances));

      subInstances.forEach(instance -> instance.setMetadataInfo(metadataInfo));

      metadataInfo
          .getServiceInfos()
          .forEach(
              (interfaceName, serviceInfo) -> {
                Map<ServiceInfo, List<ServiceInstance>> map =
                    newServiceToInstancesMap.computeIfAbsent(interfaceName, (k) -> new HashMap<>());
                List<ServiceInstance> instances =
                    map.computeIfAbsent(serviceInfo, (k) -> new ArrayList<>());
                instances.addAll(subInstances);
              });
    }
    serviceToInstancesMap = newServiceToInstancesMap;

    for (NotifyListener notifyListener : notifyListeners) {
      try {
        notifyListener.notify(matchedServiceInstances(notifyListener.serviceConfig()));
      } catch (Exception e) {
        // todo retry?
        log.error("notify listener fail. service config: {} ", notifyListener.serviceConfig(), e);
      }
    }
  }

  public synchronized void addNotifyListener(NotifyListener notifyListener) {
    if (!notifyListeners.contains(notifyListener)) {
      notifyListeners.add(notifyListener);
      notifyListener.notify(matchedServiceInstances(notifyListener.serviceConfig()));
    }
  }

  private List<ServiceInstance> matchedServiceInstances(ServiceConfig<?> serviceConfig) {
    List<ServiceInstance> serviceInstances = new ArrayList<>();
    String interfaceName = serviceConfig.getServiceInterfaceName();

    Map<ServiceInfo, List<ServiceInstance>> serviceInfoToInstancesMap =
        serviceToInstancesMap.get(interfaceName);
    if (serviceInfoToInstancesMap == null) {
      return Lists.newArrayList();
    }
    for (Map.Entry<ServiceInfo, List<ServiceInstance>> entry :
        serviceInfoToInstancesMap.entrySet()) {
      ServiceInfo serviceInfo = entry.getKey();
      if (ServiceMatcher.isMatch(serviceInfo, serviceConfig)) {
        serviceInstances.addAll(entry.getValue());
      }
    }
    return serviceInstances;
  }
}
