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
package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.cluster.directory.DefaultDirectory;
import io.github.xinfra.lab.rpc.cluster.directory.Directory;
import io.github.xinfra.lab.rpc.common.Pair;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.transport.ClientTransportManager;
import java.util.List;

public abstract class AbstractCluster implements Cluster {
  protected ReferenceConfig<?> referenceConfig;
  protected Directory directory;
  protected ClientTransportManager clientTransportManager;

  public AbstractCluster(
      ReferenceConfig<?> referenceConfig, ClientTransportManager clientTransportManager) {
    this.referenceConfig = referenceConfig;
    this.clientTransportManager = clientTransportManager;
    this.directory = new DefaultDirectory(referenceConfig.getRouter());
  }

  @Override
  public ServiceConfig<?> serviceConfig() {
    return referenceConfig;
  }

  @Override
  public ReferenceConfig<?> referenceConfig() {
    return referenceConfig;
  }

  @Override
  public Directory directory() {
    return directory;
  }

  @Override
  public ClientTransportManager clientTransportManager() {
    return clientTransportManager;
  }

  @Override
  public void notify(List<ServiceInstance> serviceInstances) {
    Pair<List<ServiceInstance>, List<ServiceInstance>> refreshed =
        directory.refreshAll(serviceInstances);

    clientTransportManager.addServiceInstances(refreshed.left());
    clientTransportManager.removeServiceInstances(refreshed.right());
  }
}
