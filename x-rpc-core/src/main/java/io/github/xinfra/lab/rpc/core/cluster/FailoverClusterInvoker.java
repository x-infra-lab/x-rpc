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
package io.github.xinfra.lab.rpc.core.cluster;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.exception.RpcClientException;
import io.github.xinfra.lab.rpc.exception.RpcException;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FailoverClusterInvoker extends AbstractClusterInvoker {

  public FailoverClusterInvoker(Cluster cluster) {
    super(cluster);
  }

  @Override
  protected InvocationResult doInvoke(
      Invocation invocation, List<ServiceInstance> serviceInstances) {
    int retries = referenceConfig.getRetries();
    int totalAttempts = retries + 1;

    Set<ServiceInstance> triedInstances = new HashSet<>();
    Exception lastException = null;

    for (int i = 0; i < totalAttempts; i++) {
      List<ServiceInstance> available =
          serviceInstances.stream()
              .filter(inst -> !triedInstances.contains(inst))
              .collect(Collectors.toList());

      if (available.isEmpty()) {
        available = new ArrayList<>(serviceInstances);
      }

      ServiceInstance serviceInstance = select(invocation, available);
      triedInstances.add(serviceInstance);
      invocation.setTargetAddress(serviceInstance.getSocketAddress());

      try {
        InvocationResult result = filteringConsumerInvoker.invoke(invocation);
        if (i > 0) {
          log.warn(
              "Failover succeeded after {} retries. service: {} method: {}",
              i,
              invocation.getServiceName(),
              invocation.getMethodName());
        }
        return result;
      } catch (RpcClientException e) {
        lastException = e;
        log.warn(
            "Failover retry {}/{} failed. service: {} method: {} target: {}",
            i + 1,
            totalAttempts,
            invocation.getServiceName(),
            invocation.getMethodName(),
            serviceInstance.getSocketAddress(),
            e);
      }
    }

    throw new RpcException(
        "Failover exhausted all "
            + totalAttempts
            + " attempts. service: "
            + invocation.getServiceName()
            + " method: "
            + invocation.getMethodName(),
        lastException);
  }
}
