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

import io.github.xinfra.lab.rpc.bootstrap.ProviderBoostrap;
import io.github.xinfra.lab.rpc.config.ExporterConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ExporterConfigBean extends ExporterConfig implements InitializingBean, DisposableBean {

  private ProviderBoostrap providerBoostrap;

  public ExporterConfigBean(Class<?> serviceInterfaceClass) {
    super(serviceInterfaceClass);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    providerBoostrap.export(this);
  }

  @Override
  public void destroy() throws Exception {
    providerBoostrap.unExport(this);
  }
}
