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

import io.github.xinfra.lab.rpc.filter.Filter;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricFilter implements Filter {

  private static final int LOG_INTERVAL = 1000;

  private final AtomicLong totalCount = new AtomicLong(0);
  private final AtomicLong successCount = new AtomicLong(0);
  private final AtomicLong failCount = new AtomicLong(0);
  private final LongAdder totalLatencyNanos = new LongAdder();

  @Override
  public InvocationResult filter(Invoker invoker, Invocation invocation) {
    totalCount.incrementAndGet();
    long startTime = System.nanoTime();
    boolean success = false;
    try {
      InvocationResult result = invoker.invoke(invocation);
      success = true;
      return result;
    } finally {
      totalLatencyNanos.add(System.nanoTime() - startTime);
      if (success) {
        long total = successCount.incrementAndGet() + failCount.get();
        logMetricsIfNeeded(total);
      } else {
        long total = successCount.get() + failCount.incrementAndGet();
        logMetricsIfNeeded(total);
      }
    }
  }

  private void logMetricsIfNeeded(long currentTotal) {
    if (currentTotal > 0 && currentTotal % LOG_INTERVAL == 0) {
      long total = totalCount.get();
      long success = successCount.get();
      long fail = failCount.get();
      long totalNanos = totalLatencyNanos.sum();
      double avgLatencyMs = total > 0 ? (double) totalNanos / total / 1_000_000.0 : 0;
      log.info(
          "RPC metrics - total: {} success: {} fail: {} avgLatency: {}ms",
          total,
          success,
          fail,
          String.format("%.2f", avgLatencyMs));
    }
  }
}
