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

import io.github.xinfra.lab.rpc.filter.Filter;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilterChainBuilderTest {

  @Mock private Invoker invoker;

  @Test
  void emptyFilterListReturnsOriginalInvoker() {
    Invoker result = FilterChainBuilder.buildFilterChainInvoker(Collections.emptyList(), invoker);
    assertSame(invoker, result);
  }

  @Test
  void nullFilterListReturnsOriginalInvoker() {
    Invoker result = FilterChainBuilder.buildFilterChainInvoker(null, invoker);
    assertSame(invoker, result);
  }

  @Test
  void filtersExecuteInOrder() {
    List<String> executionOrder = new ArrayList<>();

    Filter filter1 =
        (inv, invocation) -> {
          executionOrder.add("filter1");
          return inv.invoke(invocation);
        };
    Filter filter2 =
        (inv, invocation) -> {
          executionOrder.add("filter2");
          return inv.invoke(invocation);
        };

    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    Invoker chain =
        FilterChainBuilder.buildFilterChainInvoker(Arrays.asList(filter1, filter2), invoker);

    Invocation invocation = new Invocation();
    chain.invoke(invocation);

    assertEquals(Arrays.asList("filter1", "filter2"), executionOrder);
    verify(invoker).invoke(invocation);
  }

  @Test
  void singleFilterWorks() {
    Filter filter =
        (inv, invocation) -> {
          invocation.addAttachment("filtered", true);
          return inv.invoke(invocation);
        };

    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    Invoker chain =
        FilterChainBuilder.buildFilterChainInvoker(Collections.singletonList(filter), invoker);

    Invocation invocation = new Invocation();
    chain.invoke(invocation);

    assertEquals(true, invocation.getAttachment("filtered"));
  }

  @Test
  void filterExceptionPropagates() {
    Filter filter =
        (inv, invocation) -> {
          throw new RuntimeException("filter error");
        };

    Invoker chain =
        FilterChainBuilder.buildFilterChainInvoker(Collections.singletonList(filter), invoker);

    assertThrows(RuntimeException.class, () -> chain.invoke(new Invocation()));
    verify(invoker, never()).invoke(any());
  }
}
