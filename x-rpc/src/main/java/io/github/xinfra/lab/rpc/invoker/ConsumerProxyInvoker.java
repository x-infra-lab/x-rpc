package io.github.xinfra.lab.rpc.invoker;

import io.github.xinfra.lab.rpc.RpcRequest;
import io.github.xinfra.lab.rpc.RpcResponse;
import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;

public class ConsumerProxyInvoker implements Invoker {

    private ConsumerConfig<?> config;

    private Cluster cluster;


    public ConsumerProxyInvoker(ConsumerConfig<?> config, Cluster cluster) {
        this.config = config;
        this.cluster = cluster;
    }

    @Override
    public RpcResponse invoke(RpcRequest request) {
        // TODO
        return null;
    }
}
