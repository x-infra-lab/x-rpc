package io.github.xinfra.lab.rpc.bootstrap;

import io.github.xinfra.lab.rpc.cluster.ClusterProviderInfoListener;
import io.github.xinfra.lab.rpc.cluster.DefaultCluster;
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
        DefaultCluster cluster = new DefaultCluster(config);
        ConsumerProxyInvoker invoker = new ConsumerProxyInvoker(config, cluster);
        config.providerInfoListener(new ClusterProviderInfoListener(cluster));
        cluster.init();
        return ProxyFactory.getProxy(config).getObject(config.getInterfaceId(), invoker);
    }


}
