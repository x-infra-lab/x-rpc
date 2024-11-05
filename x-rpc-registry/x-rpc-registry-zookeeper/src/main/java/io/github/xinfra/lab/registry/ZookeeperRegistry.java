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

import io.github.xinfra.lab.rpc.registry.NotifyListener;
import io.github.xinfra.lab.rpc.registry.Registry;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.registry.ServiceInstancesChangedListener;
import java.util.List;

public class ZookeeperRegistry implements Registry {
  @Override
  public void startup() {
    // todo
  }

  @Override
  public void shutdown() {}

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
