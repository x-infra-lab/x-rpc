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
package io.github.xinfra.lab.rpc.core.cluster.loadblancer;

import io.github.xinfra.lab.rpc.cluster.loadblancer.LoadBalancer;
import io.github.xinfra.lab.rpc.common.Constants;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedLoadBalancer implements LoadBalancer {

  private static final int DEFAULT_WEIGHT = 100;

  @Override
  public ServiceInstance select(List<ServiceInstance> serviceInstances, Invocation invocation) {
    if (serviceInstances.size() == 1) {
      return serviceInstances.get(0);
    }

    int totalWeight = 0;
    boolean sameWeight = true;
    int firstWeight = -1;

    for (int i = 0; i < serviceInstances.size(); i++) {
      int weight = getWeight(serviceInstances.get(i));
      totalWeight += weight;
      if (i == 0) {
        firstWeight = weight;
      } else if (sameWeight && weight != firstWeight) {
        sameWeight = false;
      }
    }

    if (sameWeight) {
      int idx = ThreadLocalRandom.current().nextInt(serviceInstances.size());
      return serviceInstances.get(idx);
    }

    int offset = ThreadLocalRandom.current().nextInt(totalWeight);
    for (ServiceInstance instance : serviceInstances) {
      offset -= getWeight(instance);
      if (offset < 0) {
        return instance;
      }
    }

    return serviceInstances.get(0);
  }

  private int getWeight(ServiceInstance instance) {
    int weight = DEFAULT_WEIGHT;
    String weightStr = instance.getProps().get(Constants.WEIGHT_KEY);
    if (weightStr != null) {
      try {
        weight = Integer.parseInt(weightStr);
      } catch (NumberFormatException ignored) {
      }
    }

    String warmupStr = instance.getProps().get(Constants.WARMUP_KEY);
    if (warmupStr != null) {
      try {
        int warmupMills = Integer.parseInt(warmupStr);
        if (warmupMills > 0) {
          long uptime = System.currentTimeMillis() - instance.getRegistrationTimestamp();
          if (uptime > 0 && uptime < warmupMills) {
            weight = (int) ((double) weight * uptime / warmupMills);
            if (weight < 1) {
              weight = 1;
            }
          }
        }
      } catch (NumberFormatException ignored) {
      }
    }

    return Math.max(weight, 0);
  }
}
