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

import io.github.xinfra.lab.rpc.common.Constants;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceFilterTest {

  private TraceFilter traceFilter;

  @Mock private Invoker invoker;

  private Invocation invocation;

  @BeforeEach
  void setUp() {
    traceFilter = new TraceFilter();
    invocation = new Invocation();
    invocation.setServiceName("testService");
    invocation.setMethodName("testMethod");
  }

  @Test
  void generatesTraceIdWhenAbsent() {
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    traceFilter.filter(invoker, invocation);

    Object traceId = invocation.getAttachment(Constants.TRACE_ID_KEY);
    assertNotNull(traceId);
    assertEquals(32, traceId.toString().length());
  }

  @Test
  void preservesExistingTraceId() {
    String existingTraceId = "existing-trace-id-12345";
    invocation.addAttachment(Constants.TRACE_ID_KEY, existingTraceId);
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    traceFilter.filter(invoker, invocation);

    assertEquals(existingTraceId, invocation.getAttachment(Constants.TRACE_ID_KEY));
  }

  @Test
  void generatesSpanId() {
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    traceFilter.filter(invoker, invocation);

    Object spanId = invocation.getAttachment(Constants.SPAN_ID_KEY);
    assertNotNull(spanId);
    assertEquals(16, spanId.toString().length());
  }

  @Test
  void delegatesToNextInvoker() {
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    InvocationResult actual = traceFilter.filter(invoker, invocation);

    assertSame(result, actual);
    verify(invoker).invoke(invocation);
  }
}
