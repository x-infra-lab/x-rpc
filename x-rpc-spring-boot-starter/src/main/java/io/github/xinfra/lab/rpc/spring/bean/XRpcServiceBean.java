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

import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.core.bootstrap.ProviderBoostrap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class XRpcServiceBean implements InitializingBean {

  @Getter @Setter private ProviderBoostrap providerBoostrap;

  @Getter @Setter private ExporterConfig<?> exporterConfig;

  @Override
  public void afterPropertiesSet() throws Exception {
    providerBoostrap.export(exporterConfig);
    log.info("XRpc export service: {}", exporterConfig.getServiceInterfaceName());
  }
}
