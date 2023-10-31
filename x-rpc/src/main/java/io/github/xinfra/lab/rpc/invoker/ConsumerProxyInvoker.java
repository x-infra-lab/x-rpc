package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.RpcResponse;
import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.filter.DefaultFilterChain;
import io.github.xinfra.lab.rpc.filter.Filter;
import io.github.xinfra.lab.rpc.filter.FilterChain;

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
        List<Filter> filters = null;
        // TODO
        Invoker invoker = null;

        return new DefaultFilterChain(filters, invoker);
    }

    @Override
    public RpcResponse invoke(RpcRequest request) {

        return filterChain.invoke(request);
    }
}
