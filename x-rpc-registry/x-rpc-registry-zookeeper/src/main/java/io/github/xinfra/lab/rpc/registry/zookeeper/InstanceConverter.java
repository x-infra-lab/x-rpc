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

import io.github.xinfra.lab.rpc.registry.ServiceInstance;

public class InstanceConverter {
  public static ServiceInstance convert(
      org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload>
          zookeeperServiceInstance) {

    ServiceInstance serviceInstance =
        new ServiceInstance(
            zookeeperServiceInstance.getName(),
            zookeeperServiceInstance.getAddress(),
            zookeeperServiceInstance.getPort());
    serviceInstance.setEnabled(zookeeperServiceInstance.isEnabled());
    serviceInstance.setRegistrationTimestamp(zookeeperServiceInstance.getRegistrationTimeUTC());

    serviceInstance.setRevision(zookeeperServiceInstance.getPayload().getRevision());
    serviceInstance.setProtocol(zookeeperServiceInstance.getPayload().getProtocol());
    return serviceInstance;
  }

  public static org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload> convert(
      ServiceInstance serviceInstance) throws Exception {

    ZookeeperInstancePayload zookeeperInstancePayload = new ZookeeperInstancePayload();
    zookeeperInstancePayload.setRevision(serviceInstance.getRevision());
    zookeeperInstancePayload.setProtocol(serviceInstance.getProtocol());

    org.apache.curator.x.discovery.ServiceInstance<ZookeeperInstancePayload>
        zookeeperServiceInstance =
            org.apache.curator.x.discovery.ServiceInstance.<ZookeeperInstancePayload>builder()
                .name(serviceInstance.getAppName())
                .address(serviceInstance.getAddress())
                .port(serviceInstance.getPort())
                .enabled(serviceInstance.getEnabled())
                .payload(zookeeperInstancePayload)
                .build();

    return zookeeperServiceInstance;
  }
}
