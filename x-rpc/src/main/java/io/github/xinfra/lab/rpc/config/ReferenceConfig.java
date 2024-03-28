package io.github.xinfra.lab.rpc.config;

import io.github.xinfra.lab.rpc.cluster.ClusterType;
import io.github.xinfra.lab.rpc.proxy.ProxyType;
import lombok.Data;

@Data
public class ReferenceConfig<T> extends ServiceConfig<T> {

    private ConsumerConfig consumerConfig;

    private ProxyType proxyType = ProxyType.jdk;

    private ClusterType clusterType = ClusterType.fastFail;

    private String directUrl;

}
