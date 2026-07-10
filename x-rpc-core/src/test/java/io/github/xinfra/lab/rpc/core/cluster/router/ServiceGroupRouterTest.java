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
package io.github.xinfra.lab.rpc.core.cluster.router;

import static org.junit.jupiter.api.Assertions.*;

import io.github.xinfra.lab.rpc.common.Constants;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceGroupRouterTest {

  private ServiceGroupRouter router;
  private ServiceInstance i1, i2, i3;

  @BeforeEach
  void setUp() {
    router = new ServiceGroupRouter();
    i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    i1.getProps().put(Constants.GROUP_KEY, "groupA");
    i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    i2.getProps().put(Constants.GROUP_KEY, "groupB");
    i3 = new ServiceInstance("app", "127.0.0.3", 8080);
    i3.getProps().put(Constants.GROUP_KEY, "groupA");
  }

  @Test
  void noGroupReturnsAll() {
    Invocation invocation = new Invocation();
    invocation.setServiceName("testService");
    List<ServiceInstance> instances = Arrays.asList(i1, i2, i3);

    List<ServiceInstance> result = router.route(invocation, instances);

    assertEquals(3, result.size());
  }

  @Test
  void emptyGroupReturnsAll() {
    Invocation invocation = new Invocation();
    invocation.setServiceName("testService");
    invocation.addAttachment(Constants.GROUP_KEY, "");
    List<ServiceInstance> instances = Arrays.asList(i1, i2, i3);

    List<ServiceInstance> result = router.route(invocation, instances);

    assertEquals(3, result.size());
  }

  @Test
  void matchingGroupFilters() {
    Invocation invocation = new Invocation();
    invocation.setServiceName("testService");
    invocation.addAttachment(Constants.GROUP_KEY, "groupA");
    List<ServiceInstance> instances = Arrays.asList(i1, i2, i3);

    List<ServiceInstance> result = router.route(invocation, instances);

    assertEquals(2, result.size());
    assertTrue(result.contains(i1));
    assertTrue(result.contains(i3));
  }

  @Test
  void singleGroupMatch() {
    Invocation invocation = new Invocation();
    invocation.setServiceName("testService");
    invocation.addAttachment(Constants.GROUP_KEY, "groupB");
    List<ServiceInstance> instances = Arrays.asList(i1, i2, i3);

    List<ServiceInstance> result = router.route(invocation, instances);

    assertEquals(1, result.size());
    assertEquals(i2, result.get(0));
  }

  @Test
  void nonExistentGroupFallsBackToAll() {
    Invocation invocation = new Invocation();
    invocation.setServiceName("testService");
    invocation.addAttachment(Constants.GROUP_KEY, "nonExistent");
    List<ServiceInstance> instances = Arrays.asList(i1, i2, i3);

    List<ServiceInstance> result = router.route(invocation, instances);

    assertEquals(3, result.size());
  }
}
