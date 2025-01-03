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
package io.github.xinfra.lab.rpc.filter;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.config.ServiceConfig;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import java.util.List;

public class FilterChainBuilder {

  public static Invoker buildFilterChainInvoker(List<Filter> filters, Invoker invoker) {
    if (filters.isEmpty()) {
      return invoker;
    }

    try {
      Invoker nextNode = invoker;
      for (int i = filters.size() - 1; i >= 0; i--) {
        invoker = new FilterChainNodeInvoker(filters.get(i), nextNode);
        nextNode = invoker;
      }

      return invoker;
    } catch (Throwable throwable) {
      throw new RuntimeException("fail build filter chain.", throwable);
    }
  }

  public static ClusterInvoker buildClusterFilterChainInvoker(
      List<ClusterFilter> clusterFilters, ClusterInvoker clusterInvoker) {
    if (clusterFilters.isEmpty()) {
      return clusterInvoker;
    }

    try {
      ClusterInvoker nextNode = clusterInvoker;
      for (int i = clusterFilters.size() - 1; i >= 0; i--) {
        clusterInvoker = new ClusterFilterChainNodeInvoker(clusterFilters.get(i), nextNode);
        nextNode = clusterInvoker;
      }

      return clusterInvoker;
    } catch (Throwable throwable) {
      throw new RuntimeException("fail build filter chain.", throwable);
    }
  }

  public static class FilterChainNodeInvoker implements Invoker {
    private Filter filter;
    private Invoker nextNode;

    public FilterChainNodeInvoker(Filter filter, Invoker nextNode) {
      this.filter = filter;
      this.nextNode = nextNode;
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
      InvocationResult invocationResult = null;
      try {
        invocationResult = filter.filter(nextNode, invocation);
      } catch (Exception t) {
        filter.onError(t);
        throw t;
      }
      return invocationResult.whenComplete(
          (result, throwable) -> {
            if (throwable != null) {
              filter.onError(throwable);
            } else {
              filter.onResult(result);
            }
          });
    }

    @Override
    public ServiceConfig<?> serviceConfig() {
      return nextNode.serviceConfig();
    }
  }

  public static class ClusterFilterChainNodeInvoker implements ClusterInvoker {
    private ClusterFilter clusterFilter;
    private ClusterInvoker nextNode;

    public ClusterFilterChainNodeInvoker(
        ClusterFilter clusterFilter, ClusterInvoker clusterInvoker) {
      this.clusterFilter = clusterFilter;
      this.nextNode = clusterInvoker;
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
      InvocationResult invocationResult = null;
      try {
        invocationResult = clusterFilter.filter(nextNode, invocation);
      } catch (Throwable t) {
        clusterFilter.onError(t);
        throw t;
      }
      return invocationResult.whenComplete(
          (result, throwable) -> {
            if (throwable != null) {
              clusterFilter.onError(throwable);
            } else {
              clusterFilter.onResult(result);
            }
          });
    }

    @Override
    public ServiceConfig<?> serviceConfig() {
      return nextNode.serviceConfig();
    }

    @Override
    public Cluster cluster() {
      return nextNode.cluster();
    }
  }
}
