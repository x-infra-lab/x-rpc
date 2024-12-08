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
package io.github.xinfra.lab.rpc.proxy;

import io.github.xinfra.lab.rpc.common.ClassUtils;
import io.github.xinfra.lab.rpc.exception.RpcServerException;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JDKProxy implements Proxy {
  @Override
  public <T> T createProxyObject(Class<T> serviceClass, Invoker invoker) {

    return (T)
        java.lang.reflect.Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(),
            new Class[] {serviceClass},
            new JDKInvocationHandler(invoker, serviceClass));
  }

  public static class JDKInvocationHandler implements InvocationHandler {
    private Invoker invoker;
    private Class<?> serviceClass;

    public JDKInvocationHandler(Invoker invoker, Class<?> serviceClass) {
      this.invoker = invoker;
      this.serviceClass = serviceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      // TODO: handle toString hashCode equals method

      Invocation invocation = new Invocation();
      invocation.setArgs(args);
      invocation.setServiceClass(serviceClass);
      invocation.setMethod(method);

      // todo: handle throw exception?
      InvocationResult invocationResult = invoker.invoke(invocation);

      // todo
      if (invocationResult.isError()) {
        // todo
        throw new RpcServerException(invocationResult.getErrorMsg());
      }

      Object result = invocationResult.getResult();

      if (result instanceof Throwable) {
        // todo
        throw (Throwable) result;
      }

      // todo
      if (result == null) {
        return ClassUtils.getDefaultPrimitiveValue(method.getReturnType());
      }
      return result;
    }
  }
}
