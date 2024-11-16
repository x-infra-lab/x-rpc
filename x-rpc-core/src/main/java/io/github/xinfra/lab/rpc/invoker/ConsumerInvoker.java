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

import io.github.xinfra.lab.rpc.exception.RpcRemotingException;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConsumerInvoker implements Invoker {

  // too
  private static ExecutorService invokeCallBackExecutor =
      new ThreadPoolExecutor(10, 100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1024));

  private ClientTransport clientTransport;

  public ConsumerInvoker(ClientTransport clientTransport) {
    this.clientTransport = clientTransport;
  }

  @Override
  public InvocationResult invoke(Invocation invocation) {
    ServiceInstance targetServiceInstance = invocation.getTargetServiceInstance();
    try {
      RpcRequest request = new RpcRequest();
      // todo build request
      CompletableFuture<RpcResponse> future =
          clientTransport.sendAsync(
              targetServiceInstance.getSocketAddress(),
              request,
              invocation.getTimeoutMills(),
              invokeCallBackExecutor);
      InvocationResult result = new InvocationResult(future);
      return result;
    } catch (Exception e) {
      throw new RpcRemotingException("send RpcRequest fail. invocation:" + invocation, e);
    }
  }
}
