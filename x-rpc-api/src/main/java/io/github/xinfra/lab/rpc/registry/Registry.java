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

import io.github.xinfra.lab.rpc.config.ServiceConfig;
import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.List;

public interface Registry extends Closeable {
  void initInstance(String appName, InetSocketAddress address);

  void register(ServiceConfig<?> serviceConfig);

  void update(ServiceInstance serviceInstance);

  void unRegister(ServiceInstance serviceInstance);

  void subscribe(String appName, NotifyListener notifyListener);

  void unSubscribe(String appName, NotifyListener notifyListener);

  List<ServiceInstance> queryServiceInstances(String appName);

  void addAppServiceInstancesWatcher(AppServiceInstancesWatcher appServiceInstancesWatcher);

  void removeAppServiceInstancesWatcher(AppServiceInstancesWatcher appServiceInstancesWatcher);
}
