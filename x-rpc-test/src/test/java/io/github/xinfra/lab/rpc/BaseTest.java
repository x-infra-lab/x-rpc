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
package io.github.xinfra.lab.rpc;

import com.google.common.collect.Lists;
import io.github.xinfra.lab.registry.ZookeeperConfig;
import io.github.xinfra.lab.registry.ZookeeperRegistryConfig;
import io.github.xinfra.lab.rpc.api.EchoService;
import io.github.xinfra.lab.rpc.bootstrap.ConsumerBootstrap;
import io.github.xinfra.lab.rpc.bootstrap.ProviderBoostrap;
import io.github.xinfra.lab.rpc.config.ApplicationConfig;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.protocol.XProtocolConfig;
import io.github.xinfra.lab.rpc.service.EchoServiceImpl;
import io.github.xinfra.lab.transport.XRemotingTransportClientConfig;
import io.github.xinfra.lab.transport.XRemotingTransportConfig;
import io.github.xinfra.lab.transport.XRemotingTransportServerConfig;
import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BaseTest {

  private static TestingServer testingServer;

  @BeforeAll
  public static void beforeAll() throws Exception {
    testingServer = new TestingServer();
    testingServer.start();
  }

  @AfterAll
  public static void afterAll() throws Exception {
    testingServer.stop();
  }

  @Test
  public void testExportService1() {
    // app config
    ApplicationConfig applicationConfig = new ApplicationConfig();
    applicationConfig.setAppName("unit-test-app");

    // registry config
    ZookeeperConfig zookeeperConfig = new ZookeeperConfig();
    zookeeperConfig.setZkAddress(testingServer.getConnectString());
    ZookeeperRegistryConfig zookeeperRegistryConfig = new ZookeeperRegistryConfig(zookeeperConfig);

    // transport config
    XRemotingTransportConfig xRemotingTransportConfig = new XRemotingTransportConfig();
    XRemotingTransportServerConfig xRemotingTransportServerConfig =
        new XRemotingTransportServerConfig();
    xRemotingTransportConfig.setTransportServerConfig(xRemotingTransportServerConfig);

    // protocol config
    XProtocolConfig xProtocolConfig = new XProtocolConfig();
    xProtocolConfig.setXRemotingTransportConfig(xRemotingTransportConfig);

    //  provider config
    ProviderConfig providerConfig = new ProviderConfig();
    providerConfig.setApplicationConfig(applicationConfig);
    providerConfig.setRegistryConfig(zookeeperRegistryConfig);
    providerConfig.setProtocolConfig(xProtocolConfig);
    providerConfig.setFilters(Lists.newArrayList());

    // provider bootstrap
    ProviderBoostrap providerBoostrap = ProviderBoostrap.form(providerConfig);

    // exporter config
    ExporterConfig exporterConfig = new ExporterConfig(EchoService.class);
    exporterConfig.setServiceImpl(new EchoServiceImpl());

    providerBoostrap.export(exporterConfig);
  }

  @Test
  public void testReferService1() {
    // app config
    ApplicationConfig applicationConfig = new ApplicationConfig();
    applicationConfig.setAppName("unit-test-app");

    // registry config
    ZookeeperConfig zookeeperConfig = new ZookeeperConfig();
    zookeeperConfig.setZkAddress(testingServer.getConnectString());
    ZookeeperRegistryConfig zookeeperRegistryConfig = new ZookeeperRegistryConfig(zookeeperConfig);

    // transport config
    XRemotingTransportConfig xRemotingTransportConfig = new XRemotingTransportConfig();
    XRemotingTransportClientConfig xRemotingTransportClientConfig =
        new XRemotingTransportClientConfig();
    xRemotingTransportConfig.setTransportClientConfig(xRemotingTransportClientConfig);

    // protocol config
    XProtocolConfig xProtocolConfig = new XProtocolConfig();
    xProtocolConfig.setXRemotingTransportConfig(xRemotingTransportConfig);

    //  consumer config
    ConsumerConfig consumerConfig = new ConsumerConfig();
    consumerConfig.setApplicationConfig(applicationConfig);
    consumerConfig.setRegistryConfig(zookeeperRegistryConfig);
    consumerConfig.setProtocolConfig(xProtocolConfig);
    consumerConfig.setFilters(Lists.newArrayList());
    consumerConfig.setClusterFilters(Lists.newArrayList());

    // todo routerChain
    //        consumerConfig.setRouterChain();

    // consumer bootstrap
    ConsumerBootstrap consumerBootstrap = ConsumerBootstrap.from(consumerConfig);

    // refer config
    ReferenceConfig<?> referenceConfig = new ReferenceConfig(EchoService.class);

    consumerBootstrap.refer(referenceConfig);
  }
}
