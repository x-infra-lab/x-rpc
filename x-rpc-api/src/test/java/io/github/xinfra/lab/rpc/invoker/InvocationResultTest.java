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

import io.github.xinfra.lab.rpc.exception.RpcServerException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;

class InvocationResultTest {

  @Test
  void completeWithResult() throws Exception {
    InvocationResult ir = new InvocationResult();
    ir.complete("hello");

    InvocationResult completed = ir.get(1000);
    assertTrue(completed.isSuccess());
    assertEquals("hello", completed.getResult());
  }

  @Test
  void completeExceptionally() {
    InvocationResult ir = new InvocationResult();
    ir.completeExceptionally(new RuntimeException("error"));

    assertThrows(ExecutionException.class, () -> ir.get(1000));
  }

  @Test
  void invokeResultSuccess() {
    InvocationResult ir = new InvocationResult();
    ir.setSuccess(true);
    ir.setResult("data");

    assertEquals("data", ir.invokeResult());
  }

  @Test
  void invokeResultFailure() {
    InvocationResult ir = new InvocationResult();
    ir.setSuccess(false);
    ir.setErrorMsg("something went wrong");

    assertThrows(RpcServerException.class, () -> ir.invokeResult());
  }

  @Test
  void getTimesOut() {
    InvocationResult ir = new InvocationResult();

    assertThrows(TimeoutException.class, () -> ir.get(100));
  }

  @Test
  void whenCompleteCallback() throws Exception {
    InvocationResult ir = new InvocationResult();
    boolean[] called = {false};
    ir.whenComplete(
        (result, throwable) -> {
          called[0] = true;
        });

    ir.complete("done");
    Thread.sleep(50);
    assertTrue(called[0]);
  }
}
