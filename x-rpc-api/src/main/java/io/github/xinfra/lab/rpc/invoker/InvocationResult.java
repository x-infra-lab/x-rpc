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

import io.github.xinfra.lab.rpc.exception.RpcServerException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvocationResult {
  // todo
  private boolean success = true;
  // todo
  private String errorMsg;
  // todo
  private Object result;

  // todo
  CompletableFuture<InvocationResult> invocationResultCompletableFuture;

  public InvocationResult() {
    this.invocationResultCompletableFuture = new CompletableFuture<>();
  }

  public InvocationResult complete(RpcResponse rpcResponse) {
    invocationResultCompletableFuture.complete(from(rpcResponse));
    return this;
  }

  public InvocationResult complete(Object result) {
    invocationResultCompletableFuture.complete(from(result));
    return this;
  }

  public InvocationResult completeExceptionally(Throwable throwable) {
    invocationResultCompletableFuture.completeExceptionally(throwable);
    return this;
  }

  public InvocationResult whenComplete(
      BiConsumer<? super InvocationResult, ? super Throwable> action) {
    invocationResultCompletableFuture.whenComplete(action);
    return this;
  }

  private static InvocationResult from(Object result) {
    InvocationResult invocationResult = new InvocationResult();
    invocationResult.setResult(result);
    invocationResult.setSuccess(true);
    return invocationResult;
  }

  private static InvocationResult from(RpcResponse rpcResponse) {
    InvocationResult invocationResult = new InvocationResult();
    invocationResult.setSuccess(rpcResponse.isSuccess());
    invocationResult.setErrorMsg(rpcResponse.getErrorMsg());
    invocationResult.setResult(rpcResponse.getResult());
    return invocationResult;
  }

  public InvocationResult get(int timeoutMills)
      throws ExecutionException, InterruptedException, TimeoutException {
    return invocationResultCompletableFuture.get(timeoutMills, TimeUnit.MILLISECONDS);
  }

  public InvocationResult get() throws ExecutionException, InterruptedException {
    return invocationResultCompletableFuture.get();
  }

  /** @return */
  public Object invokeResult() {
    if (success) {
      return result;
    } else {
      throw new RpcServerException(errorMsg);
    }
  }
}
