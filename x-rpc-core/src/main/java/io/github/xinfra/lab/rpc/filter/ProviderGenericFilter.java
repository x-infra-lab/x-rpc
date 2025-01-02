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
package io.github.xinfra.lab.rpc.filter;

import static io.github.xinfra.lab.rpc.common.Constants.GENERIC_KEY;
import static io.github.xinfra.lab.rpc.common.Constants.GENERIC_TYPE_KEY;

import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.exception.GenericException;
import io.github.xinfra.lab.rpc.generic.GenericType;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

public class ProviderGenericFilter implements Filter {
  @Override
  public InvocationResult filter(Invoker invoker, Invocation invocation) {
    if (invoker.serviceConfig() instanceof ExporterConfig) {
      Object generic = invocation.getAttachment(GENERIC_KEY);
      Object genericType = invocation.getAttachment(GENERIC_TYPE_KEY);

      if (Objects.equals(generic, true) && Objects.equals(genericType, GenericType.JSON.name())) {

        Method method = invocation.getMethod();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Object[] args = invocation.getArgs();
        Object[] realArgs = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
          Object arg = args[i];
          if (arg == null) {
            realArgs[i] = null;
          } else {
            if (!(arg instanceof String)) {
              throw new GenericException(
                  "When using JSON to deserialize generic arguments, the arguments must be of type String");
            }
            // todo
            realArgs[i] = null;
          }
        }

        invocation.setArgs(realArgs);
      }
    }
    return invoker.invoke(invocation);
  }

  @Override
  public void onResult(InvocationResult invocationResult) {
    // todo
    Filter.super.onResult(invocationResult);
  }
}
