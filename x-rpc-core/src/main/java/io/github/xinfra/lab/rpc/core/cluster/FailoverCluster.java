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
package io.github.xinfra.lab.rpc.core.cluster;

import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.core.filter.FilterChainBuilder;
import io.github.xinfra.lab.rpc.transport.ClientTransport;
import java.util.concurrent.ExecutorService;

public class FailoverCluster extends AbstractCluster {

  public FailoverCluster(
      ReferenceConfig<?> referenceConfig,
      ClientTransport clientTransport,
      ExecutorService callbackExecutor) {
    super(referenceConfig, clientTransport, callbackExecutor);
  }

  @Override
  public ClusterInvoker filteringInvoker() {
    FailoverClusterInvoker clusterInvoker = new FailoverClusterInvoker(this);
    return FilterChainBuilder.buildClusterFilterChainInvoker(
        referenceConfig.getConsumerConfig().getClusterFilters(), clusterInvoker);
  }
}
