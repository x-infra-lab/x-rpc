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
package io.github.xinfra.lab.rpc.spring.boot.autoconfigure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.xinfra.lab.rpc.config.ApplicationConfig;
import io.github.xinfra.lab.rpc.registry.zookeeper.ZookeeperConfig;
import io.github.xinfra.lab.rpc.registry.zookeeper.ZookeeperRegistryConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

class XRpcAutoConfigurationTest {

  @Test
  void applicationConfigBean() {
    XRpcAutoConfiguration config = new XRpcAutoConfiguration();
    ApplicationConfig applicationConfig = config.applicationConfig();
    assertNotNull(applicationConfig);
  }

  @Test
  void zookeeperConfigDefaults() {
    XRpcAutoConfiguration config = new XRpcAutoConfiguration();
    ZookeeperConfig zkConfig = config.zookeeperConfig();
    assertNotNull(zkConfig);
    assertEquals("/x/services", zkConfig.getBasePath());
    assertEquals(1000, zkConfig.getSleepMsBetweenRetry());
    assertEquals(3, zkConfig.getMaxRetries());
  }

  @Test
  void zookeeperRegistryConfigDelegates() {
    XRpcAutoConfiguration config = new XRpcAutoConfiguration();
    ZookeeperConfig zkConfig = new ZookeeperConfig();
    zkConfig.setZkAddress("10.0.0.1:2181");

    ZookeeperRegistryConfig registryConfig =
        (ZookeeperRegistryConfig) config.zookeeperRegistryConfig(zkConfig);
    assertEquals("10.0.0.1:2181", registryConfig.getRegistryClientConfig().getZkAddress());
  }

  @Test
  void conditionalOnPropertyAnnotationPresent() {
    ConditionalOnProperty annotation =
        XRpcAutoConfiguration.class.getAnnotation(ConditionalOnProperty.class);
    assertNotNull(annotation);
    assertEquals("x.rpc.enabled", annotation.name()[0]);
    assertEquals("true", annotation.havingValue());
  }
}
