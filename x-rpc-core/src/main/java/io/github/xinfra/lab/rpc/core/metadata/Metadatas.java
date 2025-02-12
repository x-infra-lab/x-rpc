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
package io.github.xinfra.lab.rpc.core.metadata;

import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.core.invoker.ConsumerInvoker;
import io.github.xinfra.lab.rpc.core.invoker.DirectConnectInvoker;
import io.github.xinfra.lab.rpc.core.proxy.ProxyManager;
import io.github.xinfra.lab.rpc.core.transport.Transports;
import io.github.xinfra.lab.rpc.metadata.MetadataInfo;
import io.github.xinfra.lab.rpc.metadata.MetadataService;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.curator.utils.CloseableUtils;

public class Metadatas {

  private static Map<String, MetadataInfo> metadataCache = new ConcurrentHashMap<>();

  public static MetadataInfo getMetadataInfo(
      String revision, List<ServiceInstance> serviceInstances) {
    MetadataInfo metadataInfo = metadataCache.get(revision);
    if (metadataInfo != null) {
      return metadataInfo;
    }
    synchronized (metadataCache) {
      metadataInfo = metadataCache.get(revision);
      if (metadataInfo != null) {
        return metadataInfo;
      }

      ServiceInstance serviceInstance = select(serviceInstances);

      // todo invoke
      // todo fail and retry
      ClientTransport clientTransport = null;
      try {
        clientTransport = Transports.getClientTransport(serviceInstance.getProtocol());
        MetadataService metadataService =
            referMetadataService(clientTransport, serviceInstance.getSocketAddress());
        metadataInfo = metadataService.getMetadataInfo();
      } finally {
        if (clientTransport != null) {
          CloseableUtils.closeQuietly(clientTransport);
        }
      }

      metadataCache.put(revision, metadataInfo);
      return metadataInfo;
    }
  }

  private static MetadataService referMetadataService(
      ClientTransport clientTransport, InetSocketAddress socketAddress) {
    ReferenceConfig<MetadataService> referenceConfig = new ReferenceConfig<>(MetadataService.class);
    ConsumerInvoker consumerInvoker = new ConsumerInvoker(referenceConfig, clientTransport);
    DirectConnectInvoker directConnectInvoker =
        new DirectConnectInvoker(socketAddress, consumerInvoker);
    return ProxyManager.getProxy(referenceConfig.getProxyType())
        .createProxyObject(referenceConfig.getServiceInterfaceClass(), directConnectInvoker);
  }

  public static ServiceInstance select(List<ServiceInstance> serviceInstances) {
    if (serviceInstances.size() == 1) {
      return serviceInstances.get(0);
    } else {
      int idx = ThreadLocalRandom.current().nextInt(serviceInstances.size());
      return serviceInstances.get(idx);
    }
  }
}
