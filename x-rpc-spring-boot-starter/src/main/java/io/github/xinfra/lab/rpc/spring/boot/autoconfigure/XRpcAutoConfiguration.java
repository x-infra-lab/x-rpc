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

import io.github.xinfra.lab.registry.ZookeeperConfig;
import io.github.xinfra.lab.registry.ZookeeperRegistryConfig;
import io.github.xinfra.lab.rpc.bootstrap.ProviderBoostrap;
import io.github.xinfra.lab.rpc.config.ApplicationConfig;
import io.github.xinfra.lab.rpc.config.ProtocolConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;
import io.github.xinfra.lab.rpc.config.RegistryConfig;
import io.github.xinfra.lab.rpc.filter.Filter;
import io.github.xinfra.lab.rpc.filter.ProviderGenericFilter;
import io.github.xinfra.lab.rpc.protocol.XProtocolConfig;
import io.github.xinfra.lab.transport.XRemotingTransportClientConfig;
import io.github.xinfra.lab.transport.XRemotingTransportConfig;
import io.github.xinfra.lab.transport.XRemotingTransportServerConfig;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "x.rpc.enabled", havingValue = "true", matchIfMissing = true)
public class XRpcAutoConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "x.rpc.application")
  public ApplicationConfig applicationConfig() {
    return new ApplicationConfig();
  }

  @Bean
  @ConditionalOnClass(ZookeeperConfig.class)
  @ConditionalOnProperty(name = "x.rpc.registry", havingValue = "zookeeper")
  @ConfigurationProperties(prefix = "x.rpc.registry.zookeeper")
  public ZookeeperConfig zookeeperConfig() {
    return new ZookeeperConfig();
  }

  @Bean
  @ConditionalOnBean(ZookeeperConfig.class)
  public ZookeeperRegistryConfig zookeeperRegistryConfig(ZookeeperConfig zookeeperConfig) {
    return new ZookeeperRegistryConfig(zookeeperConfig);
  }

  @Bean
  @ConditionalOnClass(XRemotingTransportServerConfig.class)
  @ConditionalOnProperty(name = "x.rpc.protocol", havingValue = "x")
  @ConditionalOnMissingBean(XRemotingTransportServerConfig.class)
  @ConfigurationProperties(prefix = "x.rpc.protocol.server")
  public XRemotingTransportServerConfig xRemotingTransportServerConfig() {
    return new XRemotingTransportServerConfig();
  }

  @Bean
  @ConditionalOnClass(XRemotingTransportClientConfig.class)
  @ConditionalOnProperty(name = "x.rpc.protocol", havingValue = "x")
  @ConditionalOnMissingBean(XRemotingTransportClientConfig.class)
  @ConfigurationProperties(prefix = "x.rpc.protocol.client")
  public XRemotingTransportClientConfig xRemotingTransportClientConfig() {
    return new XRemotingTransportClientConfig();
  }

  @Bean
  @ConditionalOnBean({XRemotingTransportClientConfig.class, XRemotingTransportServerConfig.class})
  public XRemotingTransportConfig xRemotingTransportConfig(
      XRemotingTransportClientConfig xRemotingTransportClientConfig,
      XRemotingTransportServerConfig xRemotingTransportServerConfig) {
    XRemotingTransportConfig xRemotingTransportConfig = new XRemotingTransportConfig();
    xRemotingTransportConfig.setTransportClientConfig(xRemotingTransportClientConfig);
    xRemotingTransportConfig.setTransportServerConfig(xRemotingTransportServerConfig);
    return xRemotingTransportConfig;
  }

  @Bean
  @ConditionalOnBean(XRemotingTransportConfig.class)
  public XProtocolConfig xProtocolConfig(XRemotingTransportConfig xRemotingTransportConfig) {
    XProtocolConfig xProtocolConfig = new XProtocolConfig();
    xProtocolConfig.setXRemotingTransportConfig(xRemotingTransportConfig);
    return xProtocolConfig;
  }

  @Bean
  public Filter providerGenericFilter() {
    return new ProviderGenericFilter();
  }

  @Bean
  public ProviderBoostrap providerBoostrap(
      ApplicationConfig applicationConfig,
      RegistryConfig<?> registryConfig,
      ProtocolConfig protocolConfig,
      List<Filter> filters) {
    ProviderConfig providerConfig = new ProviderConfig();
    providerConfig.setApplicationConfig(applicationConfig);
    providerConfig.setRegistryConfig(registryConfig);
    providerConfig.setProtocolConfig(protocolConfig);
    providerConfig.setFilters(filters);
    providerConfig.setAutoRegister(false);

    return ProviderBoostrap.form(providerConfig);
  }
}
