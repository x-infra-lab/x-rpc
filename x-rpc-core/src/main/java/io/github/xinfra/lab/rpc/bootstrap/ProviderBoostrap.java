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
package io.github.xinfra.lab.rpc.bootstrap;

import io.github.xinfra.lab.rpc.config.ExporterConfig;
import io.github.xinfra.lab.rpc.config.ProviderConfig;
import java.io.Closeable;
import java.io.IOException;

public class ProviderBoostrap implements Closeable {
  private ProviderConfig providerConfig;

  public ProviderBoostrap(ProviderConfig providerConfig) {
    this.providerConfig = providerConfig;
  }

  public static ProviderBoostrap form(ProviderConfig providerConfig) {
    return new ProviderBoostrap(providerConfig);
  }

  public void export(ExporterConfig<?> exporterConfig) {
    exporterConfig.setProviderConfig(providerConfig);
    // todo


  }

  public void unExport(ExporterConfig<?> exporterConfig) {
    // too
  }

  @Override
  public void close() throws IOException {
    // todo
  }
}
