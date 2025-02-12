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
package io.github.xinfra.lab.rpc.core.invoker;

import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.exception.RpcClientException;
import io.github.xinfra.lab.rpc.exception.RpcTimeoutException;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.InvokeTypes;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConsumerInvoker implements Invoker {

  // todo
  private static ExecutorService invokeCallBackExecutor =
      new ThreadPoolExecutor(10, 100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1024));

  private ReferenceConfig<?> referenceConfig;
  private ClientTransport clientTransport;

  public ConsumerInvoker(ReferenceConfig<?> referenceConfig, ClientTransport clientTransport) {
    this.referenceConfig = referenceConfig;
    this.clientTransport = clientTransport;
  }

  @Override
  public InvocationResult invoke(Invocation invocation) {
    try {
      InvocationResult result = new InvocationResult();
      RpcRequest request = InvokeTypes.convertRpcRequest(invocation);
      CompletableFuture<RpcResponse> future =
          clientTransport.sendAsync(
              invocation.getTargetAddress(),
              request,
              invocation.getTimeoutMills(),
              invokeCallBackExecutor);

      future.whenComplete(
          (rpcResponse, throwable) -> {
            if (throwable != null) {
              result.completeExceptionally(throwable);
            } else {
              result.complete(rpcResponse);
            }
          });

      // todo: support async invoke
      return result.get(invocation.getTimeoutMills());
    } catch (TimeoutException te) {
      throw new RpcTimeoutException("consumer invoke timeout. invocation:" + invocation, te);
    } catch (Exception e) {
      throw new RpcClientException("consumer invoke fail. invocation:" + invocation, e);
    }
  }

  @Override
  public ServiceConfig<?> serviceConfig() {
    return referenceConfig;
  }
}
