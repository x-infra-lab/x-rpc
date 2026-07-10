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
package io.github.xinfra.lab.rpc.core.bootstrap;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GracefulShutdown {

  public static final GracefulShutdown INSTANCE = new GracefulShutdown();

  private static final int MAX_WAIT_SECONDS = 30;
  private static final int POLL_INTERVAL_MILLIS = 100;

  private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
  private final AtomicInteger activeCount = new AtomicInteger(0);
  private final AtomicBoolean hookRegistered = new AtomicBoolean(false);

  private GracefulShutdown() {}

  public void registerShutdownHook(ProviderBoostrap providerBoostrap) {
    if (hookRegistered.compareAndSet(false, true)) {
      Runtime.getRuntime()
          .addShutdownHook(
              new Thread(
                  () -> {
                    log.info("Graceful shutdown initiated");
                    shuttingDown.set(true);

                    long deadline = System.currentTimeMillis() + MAX_WAIT_SECONDS * 1000L;
                    while (activeCount.get() > 0 && System.currentTimeMillis() < deadline) {
                      try {
                        Thread.sleep(POLL_INTERVAL_MILLIS);
                      } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                      }
                    }

                    if (activeCount.get() > 0) {
                      log.warn(
                          "Graceful shutdown timed out with {} active requests", activeCount.get());
                    }

                    try {
                      providerBoostrap.close();
                    } catch (Exception e) {
                      log.error("Error during provider bootstrap shutdown", e);
                    }
                    log.info("Graceful shutdown completed");
                  },
                  "x-rpc-graceful-shutdown"));
    }
  }

  public boolean isShuttingDown() {
    return shuttingDown.get();
  }

  public void incrementActive() {
    activeCount.incrementAndGet();
  }

  public void decrementActive() {
    activeCount.decrementAndGet();
  }
}
