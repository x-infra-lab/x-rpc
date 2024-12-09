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
package io.github.xinfra.lab.transport;

import io.github.xinfra.lab.remoting.processor.UserProcessor;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.InvokeTypes;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;

import java.lang.reflect.Method;

public class RpcRequestProcessor implements UserProcessor<RpcRequest> {
  private final XRemotingServerTransport xremotingServerTransport;



  public RpcRequestProcessor(XRemotingServerTransport xremotingServerTransport) {
    this.xremotingServerTransport = xremotingServerTransport;
  }

  @Override
  public String interest() {
    return RpcRequest.class.getName();
  }

  @Override
  public RpcResponse handRequest(RpcRequest request) {
    try {
      Invoker invoker = xremotingServerTransport.invokerMap.get(request.getServiceName());
      if (invoker == null) {
        // todo
        return null;
      }
      Method method = xremotingServerTransport.reflectCache.find(request.getServiceName(),
              request.getMethodName(), request.getMethodArgTypes());
      if (method == null) {
        // todo
        return null;
      }

      // todo
      Invocation invocation = new Invocation();
      invocation.setServiceClass(method.getDeclaringClass());
      invocation.setMethod(method);
      invocation.setArgs(request.getMethodArgs());

      InvocationResult invocationResult = invoker.invoke(invocation);
      return InvokeTypes.convertRpcResponse(invocationResult);
    } catch (Throwable e) {
      // todo
      return null;
    }
  }
}
