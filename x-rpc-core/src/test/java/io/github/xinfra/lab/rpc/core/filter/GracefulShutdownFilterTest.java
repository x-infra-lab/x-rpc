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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GracefulShutdownFilterTest {

  private GracefulShutdownFilter filter;

  @Mock private Invoker invoker;

  private Invocation invocation;

  @BeforeEach
  void setUp() {
    filter = new GracefulShutdownFilter();
    invocation = new Invocation();
    invocation.setServiceName("testService");
  }

  @Test
  void normalRequestPassesThrough() {
    InvocationResult result = new InvocationResult();
    when(invoker.invoke(any())).thenReturn(result);

    InvocationResult actual = filter.filter(invoker, invocation);

    assertSame(result, actual);
    verify(invoker).invoke(invocation);
  }

  @Test
  void invokerExceptionStillDecrementsActive() {
    when(invoker.invoke(any())).thenThrow(new RuntimeException("test error"));

    assertThrows(RuntimeException.class, () -> filter.filter(invoker, invocation));
  }
}
