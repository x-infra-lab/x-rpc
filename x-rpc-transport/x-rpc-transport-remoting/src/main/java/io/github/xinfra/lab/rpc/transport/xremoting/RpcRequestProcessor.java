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
package io.github.xinfra.lab.rpc.transport.xremoting;

import io.github.xinfra.lab.remoting.processor.UserProcessor;
import io.github.xinfra.lab.rpc.exception.RpcServiceNotExistException;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.InvokeTypes;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.invoker.RpcRequest;
import io.github.xinfra.lab.rpc.invoker.RpcResponse;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class RpcRequestProcessor implements UserProcessor<RpcRequest> {
  private static final Logger log = LoggerFactory.getLogger(RpcRequestProcessor.class);
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
        throw new RpcServiceNotExistException(
            "service not exist. serviceName:" + request.getServiceName());
      }
      Method method =
          xremotingServerTransport.reflectCache.find(
              request.getServiceName(), request.getMethodName(), request.getMethodArgTypes());
      if (method == null) {
        throw new RpcServiceNotExistException(
            "method not exist. serviceName:"
                + request.getServiceName()
                + " methodName:"
                + request.getMethodName()
                + " methodArgTypes:"
                + request.getMethodArgTypes());
      }

      Invocation invocation = new Invocation();
      invocation.setServiceClass(method.getDeclaringClass());
      invocation.setServiceName(method.getDeclaringClass().getName());
      invocation.setMethod(method);
      invocation.setMethodName(method.getName());
      invocation.setArgs(request.getMethodArgs());
      invocation.setArgTypes(request.getMethodArgTypes());
      invocation.setAttachment(request.getAttachment());

      InvocationResult invocationResult = invoker.invoke(invocation);
      return InvokeTypes.convertRpcResponse(invocationResult);
    } catch (Throwable e) {
      RpcResponse rpcResponse = new RpcResponse();
      rpcResponse.setSuccess(false);
      rpcResponse.setErrorMsg(e.getMessage());
      return rpcResponse;
    }
  }
}
