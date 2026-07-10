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

import io.github.xinfra.lab.rpc.common.Constants;
import io.github.xinfra.lab.rpc.filter.Filter;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceFilter implements Filter {

  @Override
  public InvocationResult filter(Invoker invoker, Invocation invocation) {
    Object existingTraceId = invocation.getAttachment(Constants.TRACE_ID_KEY);
    String traceId;
    if (existingTraceId == null) {
      traceId = UUID.randomUUID().toString().replace("-", "");
      invocation.addAttachment(Constants.TRACE_ID_KEY, traceId);
    } else {
      traceId = existingTraceId.toString();
    }

    String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    invocation.addAttachment(Constants.SPAN_ID_KEY, spanId);

    log.debug(
        "traceId: {} spanId: {} service: {} method: {}",
        traceId,
        spanId,
        invocation.getServiceName(),
        invocation.getMethodName());

    return invoker.invoke(invocation);
  }
}
