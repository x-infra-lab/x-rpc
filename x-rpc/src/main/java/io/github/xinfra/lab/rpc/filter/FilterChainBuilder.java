package io.github.xinfra.lab.rpc.filter;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterInvoker;
import io.github.xinfra.lab.rpc.cluster.Directory;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.invoker.Invocation;
import io.github.xinfra.lab.rpc.invoker.InvocationResult;
import io.github.xinfra.lab.rpc.invoker.Invoker;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


public class FilterChainBuilder {

    public static <INVOKER, FILTER> INVOKER buildFilterChainInvoker(List<FILTER> filters,
                                                                    INVOKER invoker,
                                                                    Class<? extends INVOKER> filterInvokerClass) {
        try {
            Class<?> filterClass = filters.getClass().getComponentType();
            Constructor<? extends INVOKER> constructor = filterInvokerClass.getConstructor(filterClass, invoker.getClass());

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


    public static Invoker buildFilterChainInvoker(ConsumerConfig consumerConfig, Invoker invoker) {
        // todo config this
        List<Filter> filters = new ArrayList<>();

        return buildFilterChainInvoker(filters, invoker, FilterChainNodeInvoker.class);
    }

    public static ClusterInvoker buildClusterFilterChainInvoker(ConsumerConfig consumerConfig,
                                                                ClusterInvoker clusterInvoker) {
        // todo config this
        List<ClusterFilter> clusterFilters = new ArrayList<>();

        return buildFilterChainInvoker(clusterFilters, clusterInvoker, ClusterFilterChainNodeInvoker.class);
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
            return filter.filter(nextNode, invocation).whenComplete(
                    (invocationResult, throwable) -> {
                        if (throwable != null) {
                            filter.onError(throwable);
                        } else {
                            filter.onResult(invocationResult);
                        }
                    }
            );
        }
    }

    public static class ClusterFilterChainNodeInvoker implements ClusterInvoker {
        private ClusterFilter clusterFilter;
        private ClusterInvoker nextNode;


        public ClusterFilterChainNodeInvoker(ClusterFilter clusterFilter, ClusterInvoker clusterInvoker) {
            this.clusterFilter = clusterFilter;
            this.nextNode = clusterInvoker;
        }

        @Override
        public InvocationResult invoke(Invocation invocation) {
            return clusterFilter.filter(nextNode, invocation).whenComplete(
                    (invocationResult, throwable) -> {
                        if (throwable != null) {
                            clusterFilter.onError(throwable);
                        } else {
                            clusterFilter.onResult(invocationResult);
                        }
                    }
            );
        }

        @Override
        public Directory directory() {
            return nextNode.directory();
        }
    }
}
