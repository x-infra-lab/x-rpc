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
package io.github.xinfra.lab.rpc.core.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.xinfra.lab.rpc.exception.RpcException;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsumerTpsLimitFilterTest {

  @Mock private Invoker invoker;

  @Test
  void noLimitPassesThrough() {
    ConsumerTpsLimitFilter filter = new ConsumerTpsLimitFilter(-1);
    Invocation invocation = new Invocation();
    invocation.setServiceName("testService");
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    InvocationResult actual = filter.filter(invoker, invocation);

    assertSame(result, actual);
    verify(invoker).invoke(invocation);
  }

  @Test
  void withinLimitPassesThrough() {
    ConsumerTpsLimitFilter filter = new ConsumerTpsLimitFilter(1000);
    Invocation invocation = new Invocation();
    invocation.setServiceName("testService");
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    InvocationResult actual = filter.filter(invoker, invocation);

    assertSame(result, actual);
  }

  @Test
  void exceedingLimitThrowsException() {
    ConsumerTpsLimitFilter filter = new ConsumerTpsLimitFilter(1);
    Invocation invocation = new Invocation();
    invocation.setServiceName("testService");
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    filter.filter(invoker, invocation);

    assertThrows(RpcException.class, () -> filter.filter(invoker, invocation));
  }

  @Test
  void differentServicesHaveSeparateLimits() {
    ConsumerTpsLimitFilter filter = new ConsumerTpsLimitFilter(1);
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    Invocation inv1 = new Invocation();
    inv1.setServiceName("service1");
    filter.filter(invoker, inv1);

    Invocation inv2 = new Invocation();
    inv2.setServiceName("service2");
    assertDoesNotThrow(() -> filter.filter(invoker, inv2));
  }
}
