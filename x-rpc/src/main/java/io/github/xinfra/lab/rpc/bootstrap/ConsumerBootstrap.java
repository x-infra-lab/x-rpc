package io.github.xinfra.lab.rpc.bootstrap;

import io.github.xinfra.lab.rpc.cluster.Cluster;
import io.github.xinfra.lab.rpc.cluster.ClusterFactory;
import io.github.xinfra.lab.rpc.config.ConsumerConfig;
import io.github.xinfra.lab.rpc.invoker.ConsumerProxyInvoker;
import io.github.xinfra.lab.rpc.proxy.ProxyFactory;

public class ConsumerBootstrap<T> {

    private final ConsumerConfig<T> config;

    public ConsumerBootstrap(ConsumerConfig<T> config) {
        this.config = config;
    }

    public T refer() {
        // TODO: check duplicate refer
        Cluster cluster = ClusterFactory.create(config);
        ConsumerProxyInvoker invoker = new ConsumerProxyInvoker(config, cluster);
        return ProxyFactory.getProxy(config).getObject(config.getInterfaceId(), invoker);
    }


    public void unRefer() {
        // TODO
    }
}
