package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.filter.DefaultFilterChain;
import io.github.xinfra.lab.rpc.filter.Filter;
import io.github.xinfra.lab.rpc.filter.FilterChain;
import io.github.xinfra.lab.rpc.filter.MetricFilter;
import io.github.xinfra.lab.rpc.filter.MockFilter;

import java.util.ArrayList;
import java.util.List;

public class ConsumerProxyInvoker implements Invoker {

    private ConsumerConfig<?> config;

    private Cluster cluster;

    private FilterChain filterChain;


    public ConsumerProxyInvoker(ConsumerConfig<?> config, Cluster cluster) {
        this.config = config;
        this.cluster = cluster;
        this.filterChain = buildFilterChain();
    }

    protected FilterChain buildFilterChain() {
        // TODO
        List<Filter> filters = new ArrayList<>();
        filters.add(new MockFilter());
        filters.add(new MetricFilter());

        Invoker invoker = cluster.invoker();
        return new DefaultFilterChain(filters, invoker);
    }

    @Override
    public InvocationResult invoke(Invocation invocation) {
        return filterChain.invoke(invocation);
    }
}
