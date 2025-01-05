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

import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.exception.RpcServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProviderInvoker implements Invoker {

  private ExporterConfig<?> exporterConfig;

  public ProviderInvoker(ExporterConfig<?> exporterConfig) {
    this.exporterConfig = exporterConfig;
  }

  @Override
  public InvocationResult invoke(Invocation invocation) {
    InvocationResult invocationResult = new InvocationResult();
    try {
      Object result =
          invocation.getMethod().invoke(exporterConfig.getServiceImpl(), invocation.getArgs());
      invocationResult.complete(result);
    } catch (Throwable t) {
      log.error("provider method invoke error", t);
      invocationResult.completeExceptionally(t);
    } finally {
      try {
        return invocationResult.get();
      } catch (Throwable t) {
        throw new RpcServerException(t);
      }
    }
  }

  @Override
  public ServiceConfig<?> serviceConfig() {
    return exporterConfig;
  }
}
