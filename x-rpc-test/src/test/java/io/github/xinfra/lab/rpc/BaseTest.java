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
import io.github.xinfra.lab.rpc.config.ApplicationConfig;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.core.bootstrap.ConsumerBootstrap;
import io.github.xinfra.lab.rpc.core.bootstrap.ProviderBoostrap;
import io.github.xinfra.lab.rpc.core.filter.ConsumerGenericFilter;
import io.github.xinfra.lab.rpc.core.filter.ProviderGenericFilter;
import io.github.xinfra.lab.rpc.core.protocol.XProtocolConfig;
import io.github.xinfra.lab.rpc.generic.GenericService;
import io.github.xinfra.lab.rpc.generic.GenericType;
import io.github.xinfra.lab.rpc.registry.zookeeper.ZookeeperConfig;
import io.github.xinfra.lab.rpc.registry.zookeeper.ZookeeperRegistryConfig;
import io.github.xinfra.lab.rpc.test.api.EchoService;
import io.github.xinfra.lab.rpc.test.service.EchoServiceImpl;
import io.github.xinfra.lab.rpc.transport.xremoting.XRemotingTransportClientConfig;
import io.github.xinfra.lab.rpc.transport.xremoting.XRemotingTransportConfig;
import io.github.xinfra.lab.rpc.transport.xremoting.XRemotingTransportServerConfig;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BaseTest {

  private static TestingServer testingServer;

  private static EchoService echoService;

  private static GenericService genericService;

  private static ConsumerBootstrap consumerBootstrap;
  private static ProviderBoostrap providerBoostrap;

  @BeforeAll
  public static void beforeAll() throws Exception {
    testingServer = new TestingServer();
    testingServer.start();
    exportService();
    echoService = referService();
    genericService = referGenericService();
  }

  @AfterAll
  public static void afterAll() throws Exception {
    CloseableUtils.closeQuietly(testingServer);
    CloseableUtils.closeQuietly(consumerBootstrap);
    CloseableUtils.closeQuietly(providerBoostrap);
  }

  private static void exportService() {
    // app config
    ApplicationConfig applicationConfig = new ApplicationConfig();
    applicationConfig.setAppName("unit-test-provider-app");

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
    providerConfig.setFilters(Lists.newArrayList(new ProviderGenericFilter()));

    // provider bootstrap
    providerBoostrap = ProviderBoostrap.form(providerConfig);

    // exporter config
    ExporterConfig<EchoService> exporterConfig = new ExporterConfig<>(EchoService.class);
    exporterConfig.setServiceImpl(new EchoServiceImpl());

    providerBoostrap.export(exporterConfig);
  }

  private static EchoService referService() {
    // app config
    ApplicationConfig applicationConfig = new ApplicationConfig();
    applicationConfig.setAppName("unit-test-consumer-app");

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

    // consumer config
    ConsumerConfig consumerConfig = new ConsumerConfig();
    consumerConfig.setApplicationConfig(applicationConfig);
    consumerConfig.setRegistryConfig(zookeeperRegistryConfig);
    consumerConfig.setProtocolConfig(xProtocolConfig);
    consumerConfig.setClusterFilters(Lists.newArrayList());
    consumerConfig.setFilters(Lists.newArrayList(new ConsumerGenericFilter()));

    // consumer bootstrap
    consumerBootstrap = ConsumerBootstrap.from(consumerConfig);

    // refer config
    ReferenceConfig<EchoService> referenceConfig = new ReferenceConfig<>(EchoService.class);
    referenceConfig.setAppName("unit-test-provider-app");

    return consumerBootstrap.refer(referenceConfig);
  }

  private static GenericService referGenericService() {
    // refer config
    ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>(GenericService.class);
    referenceConfig.setGeneric(true);
    referenceConfig.setGenericType(GenericType.JSON);
    referenceConfig.setServiceInterfaceName(EchoService.class.getName());
    referenceConfig.setAppName("unit-test-provider-app");
    return consumerBootstrap.refer(referenceConfig);
  }

  @Test
  public void testEcho() {
    String result = echoService.hello("joe");
    Assertions.assertNotNull(result);
  }

  @Test
  public void testGenericEcho() {
    Object result =
        genericService.$invoke(
            "hello", new String[] {"java.lang.String"}, new String[] {"\"joe\""});
    Assertions.assertNotNull(result);
  }
}
