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

import io.github.xinfra.lab.rpc.metadata.MetadataInfo;
import io.github.xinfra.lab.rpc.metadata.Metadatas;
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

  private List<NotifyListener> notifyListeners;

  public DefaultAppServiceInstancesWatcher(String appName) {
    this.appName = appName;
  }

  public synchronized void change(List<ServiceInstance> serviceInstances) {
    log.info("app: {} service instances changed: {}", appName, serviceInstances);

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
              .filter(meta -> Objects.equals(revision, meta.getRevision()))
              .findFirst()
              .orElse(Metadatas.getMetadataInfo(revision, subInstances));
    }

    // todo

  }

  public synchronized void addNotifyListener(NotifyListener notifyListener) {
    // todo

  }
}
