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
package io.github.xinfra.lab.rpc.spring.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.core.bootstrap.ConsumerBootstrap;
import org.junit.jupiter.api.Test;

class XRpcReferenceFactoryBeanTest {

  interface EchoService {
    String echo(String msg);
  }

  @Test
  void getObjectType() {
    XRpcReferenceFactoryBean<EchoService> factoryBean =
        new XRpcReferenceFactoryBean<>(EchoService.class);
    assertEquals(EchoService.class, factoryBean.getObjectType());
  }

  @SuppressWarnings("unchecked")
  @Test
  void getObjectLazyInit() throws Exception {
    ConsumerBootstrap consumerBootstrap = mock(ConsumerBootstrap.class);
    ReferenceConfig<EchoService> referenceConfig = mock(ReferenceConfig.class);
    EchoService proxy = mock(EchoService.class);

    when(consumerBootstrap.refer(referenceConfig)).thenReturn(proxy);

    XRpcReferenceFactoryBean<EchoService> factoryBean =
        new XRpcReferenceFactoryBean<>(EchoService.class);
    factoryBean.setConsumerBootstrap(consumerBootstrap);
    factoryBean.setReferenceConfig(referenceConfig);

    EchoService result = factoryBean.getObject();
    assertSame(proxy, result);
    verify(consumerBootstrap, times(1)).refer(referenceConfig);
  }

  @SuppressWarnings("unchecked")
  @Test
  void getObjectCachesResult() throws Exception {
    ConsumerBootstrap consumerBootstrap = mock(ConsumerBootstrap.class);
    ReferenceConfig<EchoService> referenceConfig = mock(ReferenceConfig.class);
    EchoService proxy = mock(EchoService.class);

    when(consumerBootstrap.refer(referenceConfig)).thenReturn(proxy);

    XRpcReferenceFactoryBean<EchoService> factoryBean =
        new XRpcReferenceFactoryBean<>(EchoService.class);
    factoryBean.setConsumerBootstrap(consumerBootstrap);
    factoryBean.setReferenceConfig(referenceConfig);

    EchoService first = factoryBean.getObject();
    EchoService second = factoryBean.getObject();
    assertSame(first, second);
    verify(consumerBootstrap, times(1)).refer(referenceConfig);
  }
}
