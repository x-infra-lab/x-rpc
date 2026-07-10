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
package io.github.xinfra.lab.rpc.core.filter;

import com.google.common.util.concurrent.RateLimiter;
import io.github.xinfra.lab.rpc.exception.RpcException;
import io.github.xinfra.lab.rpc.filter.Filter;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import java.util.concurrent.ConcurrentHashMap;

public class ProviderTpsLimitFilter implements Filter {

  private final double tpsLimit;
  private final ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

  public ProviderTpsLimitFilter(double tpsLimit) {
    this.tpsLimit = tpsLimit;
  }

  @Override
  public InvocationResult filter(Invoker invoker, Invocation invocation) {
    if (tpsLimit > 0) {
      String key = invocation.getServiceName();
      RateLimiter rateLimiter =
          rateLimiterMap.computeIfAbsent(key, k -> RateLimiter.create(tpsLimit));
      if (!rateLimiter.tryAcquire()) {
        throw new RpcException(
            "Provider TPS limit exceeded for service: "
                + invocation.getServiceName()
                + " limit: "
                + tpsLimit);
      }
    }
    return invoker.invoke(invocation);
  }
}
