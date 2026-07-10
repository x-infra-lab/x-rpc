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
package io.github.xinfra.lab.rpc.registry.zookeeper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.github.xinfra.lab.rpc.registry.RegistryType;
import org.junit.jupiter.api.Test;

class ZookeeperRegistryConfigTest {

  @Test
  void registryTypeIsZookeeper() {
    ZookeeperConfig config = new ZookeeperConfig();
    ZookeeperRegistryConfig registryConfig = new ZookeeperRegistryConfig(config);

    assertEquals(RegistryType.ZOOKEEPER, registryConfig.getRegistryType());
  }

  @Test
  void returnsClientConfig() {
    ZookeeperConfig config = new ZookeeperConfig();
    config.setZkAddress("127.0.0.1:2181");
    config.setSleepMsBetweenRetry(200);
    config.setBasePath("/custom/path");

    ZookeeperRegistryConfig registryConfig = new ZookeeperRegistryConfig(config);

    assertSame(config, registryConfig.getRegistryClientConfig());
    assertEquals("127.0.0.1:2181", registryConfig.getRegistryClientConfig().getZkAddress());
    assertEquals(200, registryConfig.getRegistryClientConfig().getSleepMsBetweenRetry());
    assertEquals("/custom/path", registryConfig.getRegistryClientConfig().getBasePath());
  }

  @Test
  void defaultConfigValues() {
    ZookeeperConfig config = new ZookeeperConfig();

    assertEquals(1000, config.getSleepMsBetweenRetry());
    assertEquals(3, config.getMaxRetries());
    assertEquals("/x/services", config.getBasePath());
  }
}
