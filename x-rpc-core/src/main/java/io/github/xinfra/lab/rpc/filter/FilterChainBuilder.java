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
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;
import java.lang.reflect.Constructor;
import java.util.List;

public class FilterChainBuilder {

  private static <INVOKER, FILTER> INVOKER buildFilterChainInvoker(
      List<FILTER> filters, INVOKER invoker, Class<? extends INVOKER> filterInvokerClass) {
    try {
      Class<?> filterClass = filters.getClass().getComponentType();
      Constructor<? extends INVOKER> constructor =
          filterInvokerClass.getConstructor(filterClass, invoker.getClass());

      INVOKER nextNode = invoker;
      for (int i = filters.size() - 1; i >= 0; i--) {
        invoker = constructor.newInstance(filters.get(i), nextNode);
        nextNode = invoker;
      }

      return invoker;
    } catch (Throwable throwable) {
      throw new RuntimeException("fail build filter chain.", throwable);
    }
  }

  public static Invoker buildFilterChainInvoker(List<Filter> filters, Invoker invoker) {

    return buildFilterChainInvoker(filters, invoker, FilterChainNodeInvoker.class);
  }

  public static ClusterInvoker buildClusterFilterChainInvoker(
      List<ClusterFilter> clusterFilters, ClusterInvoker clusterInvoker) {
    return buildFilterChainInvoker(
        clusterFilters, clusterInvoker, ClusterFilterChainNodeInvoker.class);
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
    public Cluster cluster() {
      return nextNode.cluster();
    }
  }
}
