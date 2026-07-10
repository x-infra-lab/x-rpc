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
package io.github.xinfra.lab.rpc.invoker;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvocationTest {

  @Test
  void attachmentOperations() {
    Invocation invocation = new Invocation();
    assertNull(invocation.getAttachment("key"));

    invocation.addAttachment("key", "value");
    assertEquals("value", invocation.getAttachment("key"));
  }

  @Test
  void defaultTimeout() {
    Invocation invocation = new Invocation();
    assertEquals(3000, invocation.getTimeoutMills());
  }

  @Test
  void setServiceInfo() {
    Invocation invocation = new Invocation();
    invocation.setServiceName("com.example.MyService");
    invocation.setMethodName("hello");
    invocation.setArgTypes(new String[] {"java.lang.String"});
    invocation.setArgs(new Object[] {"world"});

    assertEquals("com.example.MyService", invocation.getServiceName());
    assertEquals("hello", invocation.getMethodName());
    assertEquals(1, invocation.getArgTypes().length);
    assertEquals(1, invocation.getArgs().length);
  }
}
