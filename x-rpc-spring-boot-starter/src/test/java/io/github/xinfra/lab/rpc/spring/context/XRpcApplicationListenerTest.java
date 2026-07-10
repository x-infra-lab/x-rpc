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
package io.github.xinfra.lab.rpc.spring.context;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.xinfra.lab.rpc.core.bootstrap.ProviderBoostrap;
import org.junit.jupiter.api.Test;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;

class XRpcApplicationListenerTest {

  @Test
  void registersOnContextRefreshed() {
    ProviderBoostrap providerBoostrap = mock(ProviderBoostrap.class);
    XRpcApplicationListener listener = new XRpcApplicationListener(providerBoostrap);

    GenericApplicationContext ctx = new GenericApplicationContext();
    ContextRefreshedEvent event = new ContextRefreshedEvent(ctx);

    listener.onApplicationEvent(event);

    verify(providerBoostrap, times(1)).register();
  }

  @Test
  void registersOnlyOnce() {
    ProviderBoostrap providerBoostrap = mock(ProviderBoostrap.class);
    XRpcApplicationListener listener = new XRpcApplicationListener(providerBoostrap);

    GenericApplicationContext ctx = new GenericApplicationContext();
    ContextRefreshedEvent event = new ContextRefreshedEvent(ctx);

    listener.onApplicationEvent(event);
    listener.onApplicationEvent(event);

    verify(providerBoostrap, times(1)).register();
  }

  @Test
  void ignoresNonRefreshEvents() {
    ProviderBoostrap providerBoostrap = mock(ProviderBoostrap.class);
    XRpcApplicationListener listener = new XRpcApplicationListener(providerBoostrap);

    GenericApplicationContext ctx = new GenericApplicationContext();
    ContextClosedEvent event = new ContextClosedEvent(ctx);

    listener.onApplicationEvent(event);

    verify(providerBoostrap, never()).register();
  }
}
