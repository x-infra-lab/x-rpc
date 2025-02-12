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

import io.github.xinfra.lab.rpc.cluster.loadblancer.LoadBalanceType;
import io.github.xinfra.lab.rpc.cluster.loadblancer.LoadBalancer;
import java.util.HashMap;
import java.util.Map;

public class LoadBalancerManger {

  private static Map<LoadBalanceType, LoadBalancer> loadBalancerMap = new HashMap<>();

  public static synchronized LoadBalancer getLoadBalancer(LoadBalanceType loadBalanceType) {
    LoadBalancer loadBalancer = loadBalancerMap.get(loadBalanceType);
    if (loadBalancer == null) {
      loadBalancer = LoadBalancerFactory.create(loadBalanceType);
      loadBalancerMap.put(loadBalanceType, loadBalancer);
    }
    return loadBalancer;
  }
}
