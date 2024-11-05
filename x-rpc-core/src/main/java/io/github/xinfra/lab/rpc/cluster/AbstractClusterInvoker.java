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
package io.github.xinfra.lab.rpc.cluster;

import io.github.xinfra.lab.rpc.cluster.loadblancer.LoadBalancer;
import io.github.xinfra.lab.rpc.cluster.loadblancer.LoadBalancerManger;
import io.github.xinfra.lab.rpc.cluster.naming.NamingService;
import io.github.xinfra.lab.rpc.cluster.router.RouterChain;
import io.github.xinfra.lab.rpc.config.ReferenceConfig;
import io.github.xinfra.lab.rpc.filter.FilterChainBuilder;
import io.github.xinfra.lab.rpc.invoker.ConsumerInvoker;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import io.github.xinfra.lab.rpc.registry.ServiceInstance;
import java.util.List;

public abstract class AbstractClusterInvoker implements ClusterInvoker {
  private Cluster cluster;

  private ReferenceConfig<?> referenceConfig;

  private NamingService namingService;

  protected Invoker filteringConsumerInvoker;

  protected LoadBalancer loadBalancer;
  // todo init
  protected RouterChain routerChain;

  public AbstractClusterInvoker(Cluster cluster) {
    this.cluster = cluster;
    this.referenceConfig = cluster.referenceConfig();
    this.namingService = cluster.namingService();
    this.filteringConsumerInvoker =
        FilterChainBuilder.buildFilterChainInvoker(
            referenceConfig.getConsumerConfig().getFilters(),
            new ConsumerInvoker(cluster.clientTransport()));

    this.loadBalancer = LoadBalancerManger.getLoadBalancer(referenceConfig.getLoadBalanceType());
  }

  @Override
  public Cluster cluster() {
    return cluster;
  }

  @Override
  public InvocationResult invoke(Invocation invocation) {
    List<ServiceInstance> serviceInstances = namingService.queryService(referenceConfig);
    List<ServiceInstance> routedServiceInstances = routerChain.route(invocation, serviceInstances);
    return doInvoke(invocation, routedServiceInstances);
  }

  protected ServiceInstance select(Invocation invocation, List<ServiceInstance> serviceInstances) {
    // todo
    return loadBalancer.select(serviceInstances, invocation);
  }

  protected abstract InvocationResult doInvoke(
      Invocation invocation, List<ServiceInstance> serviceInstances);
}
