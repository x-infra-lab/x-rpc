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
package io.github.xinfra.lab.rpc.cluster.router;

import static org.junit.jupiter.api.Assertions.*;

import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class RouterChainTest {

  @Test
  void emptyChainReturnsOriginalList() {
    RouterChain chain = new RouterChain();
    Invocation invocation = new Invocation();
    List<ServiceInstance> instances =
        Arrays.asList(new ServiceInstance("app", "127.0.0.1", 8080));

    List<ServiceInstance> result = chain.route(invocation, instances);

    assertEquals(1, result.size());
    assertSame(instances, result);
  }

  @Test
  void singleRouterFilters() {
    RouterChain chain = new RouterChain();
    chain.addRouter(
        (inv, list) -> {
          List<ServiceInstance> filtered = new ArrayList<>();
          for (ServiceInstance si : list) {
            if (si.getPort() == 8080) filtered.add(si);
          }
          return filtered;
        });

    Invocation invocation = new Invocation();
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 8080);
    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 9090);
    List<ServiceInstance> instances = Arrays.asList(i1, i2);

    List<ServiceInstance> result = chain.route(invocation, instances);

    assertEquals(1, result.size());
    assertEquals(i1, result.get(0));
  }

  @Test
  void multipleRoutersChainInOrder() {
    RouterChain chain = new RouterChain();
    chain.addRouter(
        (inv, list) -> {
          List<ServiceInstance> filtered = new ArrayList<>();
          for (ServiceInstance si : list) {
            if (si.getPort() >= 8080) filtered.add(si);
          }
          return filtered;
        });
    chain.addRouter(
        (inv, list) -> {
          List<ServiceInstance> filtered = new ArrayList<>();
          for (ServiceInstance si : list) {
            if (si.getPort() <= 8081) filtered.add(si);
          }
          return filtered;
        });

    Invocation invocation = new Invocation();
    ServiceInstance i1 = new ServiceInstance("app", "127.0.0.1", 7070);
    ServiceInstance i2 = new ServiceInstance("app", "127.0.0.2", 8080);
    ServiceInstance i3 = new ServiceInstance("app", "127.0.0.3", 8081);
    ServiceInstance i4 = new ServiceInstance("app", "127.0.0.4", 9090);
    List<ServiceInstance> instances = Arrays.asList(i1, i2, i3, i4);

    List<ServiceInstance> result = chain.route(invocation, instances);

    assertEquals(2, result.size());
    assertTrue(result.contains(i2));
    assertTrue(result.contains(i3));
  }
}
