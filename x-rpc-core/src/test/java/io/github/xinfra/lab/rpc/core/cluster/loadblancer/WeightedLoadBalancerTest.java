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

import io.github.xinfra.lab.rpc.common.Constants;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeightedLoadBalancerTest {

  private WeightedLoadBalancer loadBalancer;
  private Invocation invocation;

  @BeforeEach
  void setUp() {
    loadBalancer = new WeightedLoadBalancer();
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
  void selectWithSameWeightDistributesRandomly() {
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    List<ServiceInstance> instances = Arrays.asList(i1, i2);

    int count1 = 0, count2 = 0;
    for (int i = 0; i < 1000; i++) {
      ServiceInstance selected = loadBalancer.select(instances, invocation);
      if (selected == i1) count1++;
      else count2++;
    }
    assertTrue(count1 > 300 && count1 < 700, "Should distribute roughly evenly, got " + count1);
  }

  @Test
  void selectWithDifferentWeights() {
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    i1.getProps().put(Constants.WEIGHT_KEY, "900");
    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    i2.getProps().put(Constants.WEIGHT_KEY, "100");
    List<ServiceInstance> instances = Arrays.asList(i1, i2);

    int count1 = 0;
    for (int i = 0; i < 10000; i++) {
      ServiceInstance selected = loadBalancer.select(instances, invocation);
      if (selected == i1) count1++;
    }
    assertTrue(count1 > 8000, "Heavy weight instance should be selected ~90%, got " + count1);
  }

  @Test
  void selectWithZeroWeightFallsBack() {
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    i1.getProps().put(Constants.WEIGHT_KEY, "0");
    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    i2.getProps().put(Constants.WEIGHT_KEY, "100");
    List<ServiceInstance> instances = Arrays.asList(i1, i2);

    int count2 = 0;
    for (int i = 0; i < 100; i++) {
      ServiceInstance selected = loadBalancer.select(instances, invocation);
      if (selected == i2) count2++;
    }
    assertEquals(100, count2, "Zero-weight instance should never be selected");
  }

  @Test
  void selectWithInvalidWeightUsesDefault() {
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    i1.getProps().put(Constants.WEIGHT_KEY, "not-a-number");
    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    List<ServiceInstance> instances = Arrays.asList(i1, i2);

    assertDoesNotThrow(() -> {
      for (int i = 0; i < 100; i++) {
        loadBalancer.select(instances, invocation);
      }
    });
  }

  @Test
  void selectWithWarmup() {
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    i1.getProps().put(Constants.WEIGHT_KEY, "100");
    i1.getProps().put(Constants.WARMUP_KEY, "600000");
    i1.setRegistrationTimestamp(System.currentTimeMillis() - 1000);

    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    i2.getProps().put(Constants.WEIGHT_KEY, "100");
    List<ServiceInstance> instances = Arrays.asList(i1, i2);

    int count1 = 0;
    for (int i = 0; i < 10000; i++) {
      ServiceInstance selected = loadBalancer.select(instances, invocation);
      if (selected == i1) count1++;
    }
    assertTrue(count1 < 2000, "Warming-up instance should get fewer requests, got " + count1);
  }
}
