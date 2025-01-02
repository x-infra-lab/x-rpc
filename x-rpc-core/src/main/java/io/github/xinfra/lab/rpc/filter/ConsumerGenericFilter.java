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

import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.exception.GenericException;
import io.github.xinfra.lab.rpc.generic.GenericType;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;

public class ConsumerGenericFilter implements Filter {
  @Override
  public InvocationResult filter(Invoker invoker, Invocation invocation) {
    if (invoker.serviceConfig() instanceof ReferenceConfig) {
      ReferenceConfig<?> referenceConfig = (ReferenceConfig<?>) invoker.serviceConfig();
      if (referenceConfig.isGeneric()) {
        Object[] args = invocation.getArgs();
        String methodName = (String) args[0];
        String[] methodArgTypes = (String[]) args[1];
        Object[] methodArgs = (Object[]) args[2];

        if (GenericType.JSON.equals(referenceConfig.getGenericType())) {
          for (Object methodArg : methodArgs) {
            if (!(methodArg instanceof String)) {
              throw new GenericException(
                  "When using JSON to serialize generic arguments, the arguments must be of type String");
            }
          }
        }

        invocation.setServiceName(referenceConfig.getServiceInterfaceName());
        invocation.setMethodName(methodName);
        invocation.setArgTypes(methodArgTypes);
        invocation.setArgs(methodArgs);

        invocation.addAttachment(GENERIC_KEY, referenceConfig.isGeneric());
        invocation.addAttachment(GENERIC_TYPE_KEY, referenceConfig.getGenericType().name());
      }
    }
    return invoker.invoke(invocation);
  }
}
