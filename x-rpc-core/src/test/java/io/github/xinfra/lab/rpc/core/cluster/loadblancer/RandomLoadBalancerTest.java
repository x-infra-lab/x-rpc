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

import static org.junit.jupiter.api.Assertions.*;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RandomLoadBalancerTest {

  private RandomLoadBalancer loadBalancer;
  private Invocation invocation;

  @BeforeEach
  void setUp() {
    loadBalancer = new RandomLoadBalancer();
    invocation = new Invocation();
    invocation.setServiceName("testService");
  }

  @Test
  void selectSingleInstance() {
    ServiceInstance instance = new ServiceInstance("app", "127.0.0.1", 8080);
    List<ServiceInstance> instances = Arrays.asList(instance);

    ServiceInstance selected = loadBalancer.select(instances, invocation);
    assertEquals(instance, selected);
  }

  @Test
  void selectMultipleInstancesCoversAll() {
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    ServiceInstance i3 = new ServiceInstance("app", "127.0.0.3", 8080);
    List<ServiceInstance> instances = Arrays.asList(i1, i2, i3);

    Set<ServiceInstance> selected = new HashSet<>();
    for (int i = 0; i < 1000; i++) {
      selected.add(loadBalancer.select(instances, invocation));
    }
    assertEquals(3, selected.size(), "All instances should be selected at least once");
  }

  @Test
  void selectDistributesRoughlyEvenly() {
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    List<ServiceInstance> instances = Arrays.asList(i1, i2);

    int count1 = 0;
    for (int i = 0; i < 10000; i++) {
      if (loadBalancer.select(instances, invocation) == i1) count1++;
    }
    assertTrue(count1 > 4000 && count1 < 6000, "Should be roughly 50/50, got " + count1);
  }
}
